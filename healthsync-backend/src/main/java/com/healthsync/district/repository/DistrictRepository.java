package com.healthsync.district.repository;

import com.healthsync.district.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for managing District persistence operations.
 */
@Repository
public interface DistrictRepository extends JpaRepository<District, UUID> {
}
