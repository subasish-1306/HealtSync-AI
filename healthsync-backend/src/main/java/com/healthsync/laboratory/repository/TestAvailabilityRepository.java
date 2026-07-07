package com.healthsync.laboratory.repository;

import com.healthsync.laboratory.entity.TestAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing TestAvailability persistence operations.
 */
@Repository
public interface TestAvailabilityRepository extends JpaRepository<TestAvailability, UUID> {
    List<TestAvailability> findByLaboratoryId(UUID laboratoryId);
}
