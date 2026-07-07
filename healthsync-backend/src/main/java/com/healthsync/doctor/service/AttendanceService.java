package com.healthsync.doctor.service;

import com.healthsync.doctor.entity.Attendance;
import com.healthsync.doctor.entity.AttendanceStatus;
import com.healthsync.doctor.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service class handling queries for Doctor Attendance records.
 */
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }

    @Transactional(readOnly = true)
    public long getPresentDoctorCount(UUID healthCenterId, LocalDate date) {
        return attendanceRepository.countByDoctorHealthCenterIdAndDateAndStatus(
                healthCenterId, date, AttendanceStatus.PRESENT);
    }
}
