package com.healthsync.patient.repository;

import com.healthsync.patient.entity.DiseaseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing DiseaseCategory persistence operations.
 */
@Repository
public interface DiseaseCategoryRepository extends JpaRepository<DiseaseCategory, UUID> {

    Optional<DiseaseCategory> findByCode(String code);

    boolean existsByCode(String code);
}
