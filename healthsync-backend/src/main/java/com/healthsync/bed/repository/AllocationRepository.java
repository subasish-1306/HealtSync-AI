package com.healthsync.bed.repository;

import com.healthsync.bed.entity.BedAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing BedAllocation persistence operations.
 */
@Repository
public interface AllocationRepository extends JpaRepository<BedAllocation, UUID> {

    Optional<BedAllocation> findByPatientIdAndStatus(UUID patientId, String status);

    @Query("SELECT COUNT(a) FROM BedAllocation a WHERE a.bed.ward.id = :wardId AND a.status = 'ADMITTED'")
    long countActiveAllocationsInWard(@Param("wardId") UUID wardId);

    List<BedAllocation> findByPatientId(UUID patientId);
}
