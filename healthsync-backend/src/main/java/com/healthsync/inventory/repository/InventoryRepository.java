package com.healthsync.inventory.repository;

import com.healthsync.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing live stock inventories.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    List<Inventory> findByHealthCenterId(UUID healthCenterId);
    long countByHealthCenterIdAndQuantityLessThan(UUID healthCenterId, Integer reorderLevel);
}
