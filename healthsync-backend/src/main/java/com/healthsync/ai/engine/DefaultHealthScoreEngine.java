package com.healthsync.ai.engine;

import com.healthsync.ai.dto.HealthScoreResponse;
import com.healthsync.district.entity.HealthCenter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Default implementation of HealthScoreEngine executing weighted rule evaluations.
 */
@Component
public class DefaultHealthScoreEngine implements HealthScoreEngine {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public HealthScoreResponse calculateHealthScore(UUID healthCenterId) {
        HealthCenter center = entityManager.find(HealthCenter.class, healthCenterId);
        if (center == null) {
            return new HealthScoreResponse(healthCenterId, "Unknown", 0, "RED", "Health Center not found");
        }

        // 1. Medicine Availability Score (0-25)
        long totalInventory = entityManager.createQuery(
                "SELECT COUNT(i) FROM Inventory i WHERE i.healthCenter.id = :centerId", Long.class)
                .setParameter("centerId", healthCenterId)
                .getSingleResult();

        long stockedInventory = entityManager.createQuery(
                "SELECT COUNT(i) FROM Inventory i WHERE i.healthCenter.id = :centerId AND i.quantity > i.reorderLevel", Long.class)
                .setParameter("centerId", healthCenterId)
                .getSingleResult();

        int medicineScore = totalInventory == 0 ? 25 : (int) ((stockedInventory * 25.0) / totalInventory);

        // 2. Doctor Availability Score (0-25)
        long totalDoctors = entityManager.createQuery(
                "SELECT COUNT(d) FROM Doctor d WHERE d.healthCenter.id = :centerId", Long.class)
                .setParameter("centerId", healthCenterId)
                .getSingleResult();

        long presentDoctors = entityManager.createQuery(
                "SELECT COUNT(a) FROM Attendance a WHERE a.doctor.healthCenter.id = :centerId AND a.date = CURRENT_DATE AND a.status = 'PRESENT'", Long.class)
                .setParameter("centerId", healthCenterId)
                .getSingleResult();

        int doctorScore = totalDoctors == 0 ? 25 : (int) ((presentDoctors * 25.0) / totalDoctors);

        // 3. Bed Availability Score (0-25)
        long totalBeds = entityManager.createQuery(
                "SELECT COUNT(b) FROM Bed b WHERE b.ward.healthCenter.id = :centerId", Long.class)
                .setParameter("centerId", healthCenterId)
                .getSingleResult();

        long availableBeds = entityManager.createQuery(
                "SELECT COUNT(b) FROM Bed b WHERE b.ward.healthCenter.id = :centerId AND b.availabilityStatus = 'AVAILABLE'", Long.class)
                .setParameter("centerId", healthCenterId)
                .getSingleResult();

        int bedScore = totalBeds == 0 ? 25 : (int) ((availableBeds * 25.0) / totalBeds);

        // 4. Patient Waiting Time Score (0-25)
        Double avgWaitingTime = entityManager.createQuery(
                "SELECT AVG(v.waitingTime) FROM PatientVisit v WHERE v.healthCenter.id = :centerId AND v.visitDate = CURRENT_DATE AND v.status = 'COMPLETED'", Double.class)
                .setParameter("centerId", healthCenterId)
                .getSingleResult();

        int waitTimeScore = 25;
        if (avgWaitingTime != null) {
            if (avgWaitingTime <= 15) waitTimeScore = 25;
            else if (avgWaitingTime <= 30) waitTimeScore = 20;
            else if (avgWaitingTime <= 60) waitTimeScore = 15;
            else if (avgWaitingTime <= 120) waitTimeScore = 10;
            else waitTimeScore = 5;
        }

        int score = medicineScore + doctorScore + bedScore + waitTimeScore;
        String status = score >= 80 ? "GREEN" : (score >= 50 ? "YELLOW" : "RED");

        String breakdown = String.format("Medicine: %d/25, Doctor: %d/25, Bed: %d/25, WaitTime: %d/25",
                medicineScore, doctorScore, bedScore, waitTimeScore);

        return new HealthScoreResponse(healthCenterId, center.getName(), score, status, breakdown);
    }
}
