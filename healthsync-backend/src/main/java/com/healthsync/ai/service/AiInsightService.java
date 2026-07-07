package com.healthsync.ai.service;

import com.healthsync.ai.dto.*;
import com.healthsync.ai.engine.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class orchestrating execution of strategy engines to deliver unified insights.
 */
@Service
@RequiredArgsConstructor
public class AiInsightService {

    private final HealthScoreEngine healthScoreEngine;
    private final MedicinePredictionEngine medicinePredictionEngine;
    private final PatientLoadPredictor patientLoadPredictor;
    private final BedOccupancyPredictor bedOccupancyPredictor;
    private final DoctorAvailabilityAnalyzer doctorAvailabilityAnalyzer;
    private final ResourceRedistributionEngine resourceRedistributionEngine;
    private final AlertGenerationEngine alertGenerationEngine;
    private final RecommendationEngine recommendationEngine;

    @Transactional(readOnly = true)
    public HealthScoreResponse getHealthScore(UUID healthCenterId) {
        return healthScoreEngine.calculateHealthScore(healthCenterId);
    }

    @Transactional(readOnly = true)
    public PredictionSummary getPredictions(UUID healthCenterId) {
        return new PredictionSummary(
                patientLoadPredictor.predictPatientLoad(healthCenterId),
                List.of(bedOccupancyPredictor.predictBedOccupancy(healthCenterId)),
                doctorAvailabilityAnalyzer.analyzeDoctorAvailability(healthCenterId),
                medicinePredictionEngine.predictMedicineStockOuts(healthCenterId)
        );
    }

    @Transactional(readOnly = true)
    public List<RecommendationDto> getRecommendations(UUID healthCenterId) {
        return recommendationEngine.generateRecommendations(healthCenterId);
    }

    @Transactional(readOnly = true)
    public List<AiAlertDto> getAlerts(UUID healthCenterId) {
        return alertGenerationEngine.generateAlerts(healthCenterId);
    }

    @Transactional(readOnly = true)
    public List<RedistributionTransfer> getResourceRedistributions(UUID healthCenterId) {
        return resourceRedistributionEngine.recommendRedistributions(healthCenterId);
    }
}
