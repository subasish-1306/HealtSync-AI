package com.healthsync.laboratory.service;

import com.healthsync.district.entity.HealthCenter;
import com.healthsync.exception.ResourceNotFoundException;
import com.healthsync.laboratory.dto.LaboratoryRequest;
import com.healthsync.laboratory.dto.LaboratoryResponse;
import com.healthsync.laboratory.entity.Laboratory;
import com.healthsync.laboratory.repository.LaboratoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class handling business logic for Laboratories.
 */
@Service
@RequiredArgsConstructor
public class LaboratoryService {

    private final LaboratoryRepository laboratoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<LaboratoryResponse> getAllLaboratories() {
        return laboratoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LaboratoryResponse getLaboratoryById(UUID id) {
        Laboratory lab = laboratoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found with ID: " + id));
        return mapToResponse(lab);
    }

    @Transactional
    public LaboratoryResponse createLaboratory(LaboratoryRequest request) {
        HealthCenter center = entityManager.find(HealthCenter.class, request.healthCenterId());
        if (center == null) {
            throw new ResourceNotFoundException("Health Center not found with ID: " + request.healthCenterId());
        }

        Laboratory lab = Laboratory.builder()
                .name(request.name())
                .healthCenter(center)
                .status(request.status())
                .workingHours(request.workingHours())
                .equipmentCount(request.equipmentCount())
                .build();

        return mapToResponse(laboratoryRepository.save(lab));
    }

    @Transactional
    public LaboratoryResponse updateLaboratory(UUID id, LaboratoryRequest request) {
        Laboratory lab = laboratoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found with ID: " + id));

        HealthCenter center = entityManager.find(HealthCenter.class, request.healthCenterId());
        if (center == null) {
            throw new ResourceNotFoundException("Health Center not found with ID: " + request.healthCenterId());
        }

        lab.setName(request.name());
        lab.setHealthCenter(center);
        lab.setStatus(request.status());
        lab.setWorkingHours(request.workingHours());
        lab.setEquipmentCount(request.equipmentCount());

        return mapToResponse(laboratoryRepository.save(lab));
    }

    @Transactional
    public void deleteLaboratory(UUID id) {
        if (!laboratoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Laboratory not found with ID: " + id);
        }
        laboratoryRepository.deleteById(id);
    }

    private LaboratoryResponse mapToResponse(Laboratory lab) {
        return new LaboratoryResponse(
                lab.getId(),
                lab.getName(),
                lab.getHealthCenter().getId(),
                lab.getHealthCenter().getName(),
                lab.getStatus(),
                lab.getWorkingHours(),
                lab.getEquipmentCount()
        );
    }
}
