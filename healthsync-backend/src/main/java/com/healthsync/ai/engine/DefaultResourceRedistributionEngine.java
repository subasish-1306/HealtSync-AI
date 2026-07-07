package com.healthsync.ai.engine;

import com.healthsync.ai.dto.RedistributionTransfer;
import com.healthsync.inventory.entity.Inventory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Default implementation of ResourceRedistributionEngine balancing supply inventories.
 */
@Component
public class DefaultResourceRedistributionEngine implements ResourceRedistributionEngine {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<RedistributionTransfer> recommendRedistributions(UUID healthCenterId) {
        List<Inventory> centerShortages = entityManager.createQuery(
                "SELECT i FROM Inventory i JOIN FETCH i.medicine JOIN FETCH i.healthCenter WHERE i.healthCenter.id = :centerId AND i.quantity < i.reorderLevel", Inventory.class)
                .setParameter("centerId", healthCenterId)
                .getResultList();

        List<RedistributionTransfer> recommendations = new ArrayList<>();

        for (Inventory shortageInv : centerShortages) {
            List<Inventory> surplusCenters = entityManager.createQuery(
                    "SELECT i FROM Inventory i JOIN FETCH i.healthCenter JOIN FETCH i.medicine WHERE i.medicine.code = :medCode AND i.quantity > i.reorderLevel * 2 AND i.healthCenter.id != :centerId", Inventory.class)
                    .setParameter("medCode", shortageInv.getMedicine().getCode())
                    .setParameter("centerId", healthCenterId)
                    .getResultList();

            if (!surplusCenters.isEmpty()) {
                Inventory surplusInv = surplusCenters.get(0);
                int transferQty = (surplusInv.getQuantity() - surplusInv.getReorderLevel()) / 2;
                if (transferQty > 0) {
                    recommendations.add(new RedistributionTransfer(
                            shortageInv.getMedicine().getName(),
                            transferQty,
                            surplusInv.getHealthCenter().getId(),
                            surplusInv.getHealthCenter().getName(),
                            healthCenterId,
                            shortageInv.getHealthCenter().getName(),
                            shortageInv.getQuantity() == 0 ? "HIGH" : "MEDIUM"
                    ));
                }
            }
        }

        return recommendations;
    }
}
