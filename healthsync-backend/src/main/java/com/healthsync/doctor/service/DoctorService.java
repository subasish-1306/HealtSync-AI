package com.healthsync.doctor.service;

import com.healthsync.doctor.entity.Doctor;
import com.healthsync.doctor.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class handling queries for Doctor profiles.
 */
@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    @Transactional(readOnly = true)
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public long getCount() {
        return doctorRepository.count();
    }

    @Transactional(readOnly = true)
    public long countByHealthCenter(UUID healthCenterId) {
        return doctorRepository.countByHealthCenterId(healthCenterId);
    }
}
