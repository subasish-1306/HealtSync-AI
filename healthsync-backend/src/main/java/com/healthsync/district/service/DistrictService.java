package com.healthsync.district.service;

import com.healthsync.district.entity.District;
import com.healthsync.district.repository.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class handling queries for District profiles.
 */
@Service
@RequiredArgsConstructor
public class DistrictService {

    private final DistrictRepository districtRepository;

    @Transactional(readOnly = true)
    public List<District> getAllDistricts() {
        return districtRepository.findAll();
    }

    @Transactional(readOnly = true)
    public District getDistrictById(UUID id) {
        return districtRepository.findById(id).orElse(null);
    }
}
