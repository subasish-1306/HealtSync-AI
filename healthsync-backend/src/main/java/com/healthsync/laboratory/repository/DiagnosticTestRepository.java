package com.healthsync.laboratory.repository;

import com.healthsync.laboratory.entity.DiagnosticTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing DiagnosticTest persistence operations.
 */
@Repository
public interface DiagnosticTestRepository extends JpaRepository<DiagnosticTest, UUID> {
    Optional<DiagnosticTest> findByCode(String code);
}
