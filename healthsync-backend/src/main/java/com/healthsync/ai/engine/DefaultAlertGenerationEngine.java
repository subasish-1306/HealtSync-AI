package com.healthsync.ai.engine;

import com.healthsync.ai.dto.AiAlertDto;
import com.healthsync.ai.dto.HealthScoreResponse;
import com.healthsync.ai.dto.PredictionSummary.BedOccupancyPrediction;
import com.healthsync.ai.dto.PredictionSummary.DoctorShortagePrediction;
import com.healthsync.ai.dto.PredictionSummary.PatientLoadPrediction;
import com.healthsync.district.entity.HealthCenter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Default implementation of AlertGenerationEngine compiling clinical and stock alerts.
 */
@Component
@RequiredArgsConstructor
public class DefaultAlertGenerationEngine implements AlertGenerationEngine {

    private final HealthScoreEngine healthScoreEngine;
    private final BedOccupancyPredictor bedOccupancyPredictor;
    private final PatientLoadPredictor patientLoadPredictor;
    private final DoctorAvailabilityAnalyzer doctorAvailabilityAnalyzer;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AiAlertDto> generateAlerts(UUID healthCenterId) {
        HealthCenter center = entityManager.find(HealthCenter.class, healthCenterId);
        String name = center == null ? "Unknown" : center.getName();

        List<AiAlertDto> alerts = new ArrayList<>();

        // 1. Medicine Shortage Alert
        long shortages = entityManager.createQuery(
                "SELECT COUNT(i) FROM Inventory i WHERE i.healthCenter.id = :centerId AND i.quantity < i.reorderLevel", Long.class)
                .setParameter("centerId", healthCenterId)
                .getSingleResult();

        if (shortages > 0) {
            alerts.add(new AiAlertDto(
                    "MEDICINE_SHORTAGE",
                    shortages + " medicine items are currently below reorder levels.",
                    "HIGH",
                    name
            ));
        }

        // 2. Doctor Shortage Alert
        List<DoctorShortagePrediction> doctorPredictions = doctorAvailabilityAnalyzer.analyzeDoctorAvailability(healthCenterId);
        for (DoctorShortagePrediction pred : doctorPredictions) {
            if ("HIGH".equals(pred.shortageRisk())) {
                alerts.add(new AiAlertDto(
                        "DOCTOR_SHORTAGE",
                        "Risk of doctor shortage in " + pred.department() + ". Needed: " + pred.predictedNeeded(),
                        "CRITICAL",
                        name
                ));
            }
        }

        // 3. Bed Full Alert
        BedOccupancyPrediction bedPred = bedOccupancyPredictor.predictBedOccupancy(healthCenterId);
        int totalCapacity = bedPred.predictedOccupiedBeds() + bedPred.availableBeds();
        if (totalCapacity > 0) {
            double rate = (double) bedPred.predictedOccupiedBeds() / totalCapacity;
            if (rate >= 0.90) {
                alerts.add(new AiAlertDto(
                        "BED_FULL",
                        "Inpatient bed occupancy is critically high (" + (int)(rate * 100) + "%). Available beds: " + bedPred.availableBeds(),
                        "CRITICAL",
                        name
                ));
            }
        }

        // 4. High Patient Load Alert
        List<PatientLoadPrediction> loadPredictions = patientLoadPredictor.predictPatientLoad(healthCenterId);
        for (PatientLoadPrediction pred : loadPredictions) {
            if ("Tomorrow".equalsIgnoreCase(pred.period()) && pred.predictedVisits() > 40) {
                alerts.add(new AiAlertDto(
                        "HIGH_PATIENT_LOAD",
                        "High patient footfall predicted tomorrow: " + pred.predictedVisits() + " visits.",
                        "MEDIUM",
                        name
                ));
            }
        }

        // 5. Critical Health Center Alert
        HealthScoreResponse healthScore = healthScoreEngine.calculateHealthScore(healthCenterId);
        if (healthScore.score() < 50) {
            alerts.add(new AiAlertDto(
                    "CRITICAL_HEALTH_CENTER",
                    "Health score is critically low (" + healthScore.score() + "). Breakdown: " + healthScore.breakdown(),
                    "CRITICAL",
                    name
            ));
        }

        return alerts;
    }
}
