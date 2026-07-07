package com.healthsync.bed.repository;

import com.healthsync.bed.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Ward persistence operations.
 */
@Repository
public interface WardRepository extends JpaRepository<Ward, UUID> {

    Optional<Ward> findByCode(String code);

    boolean existsByCode(String code);
}
