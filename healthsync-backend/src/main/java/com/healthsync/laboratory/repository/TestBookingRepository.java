package com.healthsync.laboratory.repository;

import com.healthsync.laboratory.entity.TestBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing TestBooking persistence operations.
 */
@Repository
public interface TestBookingRepository extends JpaRepository<TestBooking, UUID> {
    List<TestBooking> findByPatientId(UUID patientId);
}
