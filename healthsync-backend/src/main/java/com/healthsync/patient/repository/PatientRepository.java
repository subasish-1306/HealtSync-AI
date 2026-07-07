package com.healthsync.patient.repository;

import com.healthsync.patient.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Patient persistence operations.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Optional<Patient> findByPatientId(String patientId);

    Optional<Patient> findByMobileNumber(String mobileNumber);

    boolean existsByMobileNumber(String mobileNumber);

    @Query("SELECT p FROM Patient p WHERE " +
           "(:query IS NULL OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:mobile IS NULL OR p.mobileNumber = :mobile) AND " +
           "(:patientId IS NULL OR p.patientId = :patientId) AND " +
           "(:status IS NULL OR p.status = :status)")
    List<Patient> searchPatients(
            @Param("query") String query,
            @Param("mobile") String mobile,
            @Param("patientId") String patientId,
            @Param("status") String status
    );
}
