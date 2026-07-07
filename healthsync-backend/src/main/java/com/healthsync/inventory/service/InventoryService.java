package com.healthsync.inventory.service;

import com.healthsync.inventory.entity.Inventory;
import com.healthsync.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class handling queries for Live Stock Inventories.
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<Inventory> getInventoryByHealthCenter(UUID healthCenterId) {
        return inventoryRepository.findByHealthCenterId(healthCenterId);
    }

    @Transactional(readOnly = true)
    public long getLowStockCount(UUID healthCenterId) {
        // Query low stock items where quantity <= reorderLevel
        return inventoryRepository.findByHealthCenterId(healthCenterId).stream()
                .filter(i -> i.getQuantity() < i.getReorderLevel())
                .count();
    }

    @Transactional(readOnly = true)
    public long getTotalLowStockCount() {
        return inventoryRepository.findAll().stream()
                .filter(i -> i.getQuantity() < i.getReorderLevel())
                .count();
    }
}
