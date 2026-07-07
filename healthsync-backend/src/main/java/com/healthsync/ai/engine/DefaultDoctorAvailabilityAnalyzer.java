package com.healthsync.ai.engine;

import com.healthsync.ai.dto.PredictionSummary.DoctorShortagePrediction;
import com.healthsync.doctor.entity.Doctor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Default implementation of DoctorAvailabilityAnalyzer determining doctor supply and demand.
 */
@Component
public class DefaultDoctorAvailabilityAnalyzer implements DoctorAvailabilityAnalyzer {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<DoctorShortagePrediction> analyzeDoctorAvailability(UUID healthCenterId) {
        List<Doctor> doctors = entityManager.createQuery(
                "SELECT d FROM Doctor d WHERE d.healthCenter.id = :centerId", Doctor.class)
                .setParameter("centerId", healthCenterId)
                .getResultList();

        List<DoctorShortagePrediction> shortages = new ArrayList<>();

        long generalMedicineDoctors = doctors.stream()
                .filter(d -> "General Medicine".equalsIgnoreCase(d.getSpecialization()) || "Pediatrics".equalsIgnoreCase(d.getSpecialization()) || d.getSpecialization() != null)
                .count();

        long visitsCount = entityManager.createQuery(
                "SELECT COUNT(v) FROM PatientVisit v WHERE v.healthCenter.id = :centerId AND v.visitDate = CURRENT_DATE", Long.class)
                .setParameter("centerId", healthCenterId)
                .getSingleResult();

        int currentDocs = (int) generalMedicineDoctors;
        int predictedNeeded = (int) Math.max(1, visitsCount / 10);
        if (predictedNeeded == 0) {
            predictedNeeded = 2; // Baseline need
        }

        String risk = currentDocs < predictedNeeded ? "HIGH" : "LOW";
        shortages.add(new DoctorShortagePrediction("General Medicine", currentDocs, predictedNeeded, risk));

        return shortages;
    }
}
