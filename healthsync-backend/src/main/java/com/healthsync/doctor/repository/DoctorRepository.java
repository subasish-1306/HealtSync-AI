package com.healthsync.doctor.repository;

import com.healthsync.doctor.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing Doctor profiles.
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    List<Doctor> findByHealthCenterId(UUID healthCenterId);
    long countByHealthCenterId(UUID healthCenterId);
}
