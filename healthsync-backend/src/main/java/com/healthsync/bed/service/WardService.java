package com.healthsync.bed.service;

import com.healthsync.bed.dto.WardRequest;
import com.healthsync.bed.dto.WardResponse;
import com.healthsync.bed.entity.Ward;
import com.healthsync.bed.repository.WardRepository;
import com.healthsync.district.entity.HealthCenter;
import com.healthsync.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class handling business logic for Wards.
 */
@Service
@RequiredArgsConstructor
public class WardService {

    private final WardRepository wardRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<WardResponse> getAllWards() {
        return wardRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WardResponse getWardById(UUID id) {
        Ward ward = wardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ward not found with ID: " + id));
        return mapToResponse(ward);
    }

    @Transactional
    public WardResponse createWard(WardRequest request) {
        if (wardRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Ward already exists with code: " + request.code());
        }

        HealthCenter healthCenter = entityManager.find(HealthCenter.class, request.healthCenterId());
        if (healthCenter == null) {
            throw new ResourceNotFoundException("Health Center not found with ID: " + request.healthCenterId());
        }

        Ward ward = Ward.builder()
                .code(request.code())
                .name(request.name())
                .department(request.department())
                .floor(request.floor())
                .capacity(request.capacity())
                .status(request.status())
                .healthCenter(healthCenter)
                .build();

        Ward saved = wardRepository.save(ward);
        return mapToResponse(saved);
    }

    @Transactional
    public WardResponse updateWard(UUID id, WardRequest request) {
        Ward ward = wardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ward not found with ID: " + id));

        if (!ward.getCode().equalsIgnoreCase(request.code()) && wardRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Ward already exists with code: " + request.code());
        }

        HealthCenter healthCenter = entityManager.find(HealthCenter.class, request.healthCenterId());
        if (healthCenter == null) {
            throw new ResourceNotFoundException("Health Center not found with ID: " + request.healthCenterId());
        }

        ward.setCode(request.code());
        ward.setName(request.name());
        ward.setDepartment(request.department());
        ward.setFloor(request.floor());
        ward.setCapacity(request.capacity());
        ward.setStatus(request.status());
        ward.setHealthCenter(healthCenter);

        Ward updated = wardRepository.save(ward);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteWard(UUID id) {
        if (!wardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ward not found with ID: " + id);
        }
        wardRepository.deleteById(id);
    }

    private WardResponse mapToResponse(Ward ward) {
        return new WardResponse(
                ward.getId(),
                ward.getCode(),
                ward.getName(),
                ward.getDepartment(),
                ward.getFloor(),
                ward.getCapacity(),
                ward.getStatus(),
                ward.getHealthCenter().getId(),
                ward.getHealthCenter().getName()
        );
    }
}
