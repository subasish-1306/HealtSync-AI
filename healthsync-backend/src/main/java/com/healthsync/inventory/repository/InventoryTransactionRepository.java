package com.healthsync.inventory.repository;

import com.healthsync.inventory.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for auditing live stock transactions.
 */
@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, UUID> {
}
