package com.healthsync.ai.engine;

import com.healthsync.ai.dto.PredictionSummary.BedOccupancyPrediction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Default implementation of BedOccupancyPredictor estimating future bed needs.
 */
@Component
public class DefaultBedOccupancyPredictor implements BedOccupancyPredictor {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public BedOccupancyPrediction predictBedOccupancy(UUID healthCenterId) {
        long totalBeds = entityManager.createQuery(
                "SELECT COUNT(b) FROM Bed b WHERE b.ward.healthCenter.id = :centerId", Long.class)
                .setParameter("centerId", healthCenterId)
                .getSingleResult();

        long occupiedBeds = entityManager.createQuery(
                "SELECT COUNT(b) FROM Bed b WHERE b.ward.healthCenter.id = :centerId AND b.availabilityStatus = 'OCCUPIED'", Long.class)
                .setParameter("centerId", healthCenterId)
                .getSingleResult();

        int totalCapacity = (int) totalBeds;
        int currentOccupied = (int) occupiedBeds;
        int predictedOccupied = totalCapacity == 0 ? 0 : Math.min(totalCapacity, currentOccupied + 2);
        int availableBeds = Math.max(0, totalCapacity - predictedOccupied);
        int emergencyCapacity = (int) (totalCapacity * 0.15); // 15% emergency reserve

        return new BedOccupancyPrediction(
                predictedOccupied,
                availableBeds,
                emergencyCapacity,
                0.88
        );
    }
}
