package com.healthsync.bed.repository;

import com.healthsync.bed.entity.BedTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing BedTransfer persistence operations.
 */
@Repository
public interface TransferRepository extends JpaRepository<BedTransfer, UUID> {

    List<BedTransfer> findByPatientId(UUID patientId);
}
