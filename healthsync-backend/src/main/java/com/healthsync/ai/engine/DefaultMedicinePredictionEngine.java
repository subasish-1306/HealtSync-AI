package com.healthsync.ai.engine;

import com.healthsync.ai.dto.PredictionSummary.MedicineStockPrediction;
import com.healthsync.inventory.entity.Inventory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Default implementation of MedicinePredictionEngine calculating stock-out dates.
 */
@Component
public class DefaultMedicinePredictionEngine implements MedicinePredictionEngine {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<MedicineStockPrediction> predictMedicineStockOuts(UUID healthCenterId) {
        List<Inventory> inventories = entityManager.createQuery(
                "SELECT i FROM Inventory i JOIN FETCH i.medicine WHERE i.healthCenter.id = :centerId", Inventory.class)
                .setParameter("centerId", healthCenterId)
                .getResultList();

        List<MedicineStockPrediction> predictions = new ArrayList<>();

        for (Inventory inv : inventories) {
            Long totalDispensed = entityManager.createQuery(
                    "SELECT SUM(t.quantity) FROM InventoryTransaction t WHERE t.sourceCenter.id = :centerId AND t.medicine.id = :medId AND t.type = 'DISPENSE'", Long.class)
                    .setParameter("centerId", healthCenterId)
                    .setParameter("medId", inv.getMedicine().getId())
                    .getSingleResult();

            int avgDailyUsage = (totalDispensed == null || totalDispensed == 0) ? 5 : (int) Math.max(1, totalDispensed / 30.0);
            int currentStock = inv.getQuantity();
            int daysRemaining = currentStock / avgDailyUsage;

            String riskLevel = "LOW";
            if (daysRemaining < 3) {
                riskLevel = "HIGH";
            } else if (daysRemaining < 10) {
                riskLevel = "MEDIUM";
            }

            predictions.add(new MedicineStockPrediction(
                    inv.getMedicine().getName(),
                    currentStock,
                    avgDailyUsage,
                    daysRemaining,
                    riskLevel
            ));
        }

        return predictions;
    }
}
