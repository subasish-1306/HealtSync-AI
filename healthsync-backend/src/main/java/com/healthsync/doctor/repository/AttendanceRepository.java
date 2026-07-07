package com.healthsync.doctor.repository;

import com.healthsync.doctor.entity.Attendance;
import com.healthsync.doctor.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing Doctor daily attendance records.
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    List<Attendance> findByDate(LocalDate date);
    long countByDoctorHealthCenterIdAndDateAndStatus(UUID healthCenterId, LocalDate date, AttendanceStatus status);
}
