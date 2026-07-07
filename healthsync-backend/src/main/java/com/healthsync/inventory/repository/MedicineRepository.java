package com.healthsync.inventory.repository;

import com.healthsync.inventory.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for managing Medicine catalog profiles.
 */
@Repository
public interface MedicineRepository extends JpaRepository<Medicine, UUID> {
}
