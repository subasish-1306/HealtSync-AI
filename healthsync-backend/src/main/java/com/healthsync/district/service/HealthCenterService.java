package com.healthsync.district.service;

import com.healthsync.district.entity.HealthCenter;
import com.healthsync.district.repository.HealthCenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class handling queries for Health Center profiles.
 */
@Service
@RequiredArgsConstructor
public class HealthCenterService {

    private final HealthCenterRepository healthCenterRepository;

    @Transactional(readOnly = true)
    public List<HealthCenter> getAllHealthCenters() {
        return healthCenterRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<HealthCenter> getHealthCentersByDistrict(UUID districtId) {
        return healthCenterRepository.findByDistrictId(districtId);
    }

    @Transactional(readOnly = true)
    public HealthCenter getHealthCenterById(UUID id) {
        return healthCenterRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public long getCount() {
        return healthCenterRepository.count();
    }
}
