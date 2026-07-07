package com.healthsync.laboratory.repository;

import com.healthsync.laboratory.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing TestResult persistence operations.
 */
@Repository
public interface TestResultRepository extends JpaRepository<TestResult, UUID> {
    Optional<TestResult> findByBookingId(UUID bookingId);
}
