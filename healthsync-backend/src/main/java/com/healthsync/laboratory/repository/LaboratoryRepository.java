package com.healthsync.laboratory.repository;

import com.healthsync.laboratory.entity.Laboratory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for managing Laboratory persistence operations.
 */
@Repository
public interface LaboratoryRepository extends JpaRepository<Laboratory, UUID> {
}
