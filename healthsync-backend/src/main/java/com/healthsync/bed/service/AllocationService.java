package com.healthsync.bed.service;

import com.healthsync.bed.dto.AllocationRequest;
import com.healthsync.bed.dto.AllocationResponse;
import com.healthsync.bed.entity.Bed;
import com.healthsync.bed.entity.BedAllocation;
import com.healthsync.bed.entity.Ward;
import com.healthsync.bed.repository.AllocationRepository;
import com.healthsync.bed.repository.BedRepository;
import com.healthsync.common.event.BedAllocatedEvent;
import com.healthsync.common.event.BedReleasedEvent;
import com.healthsync.common.event.WardCapacityReachedEvent;
import com.healthsync.exception.ResourceNotFoundException;
import com.healthsync.patient.entity.Patient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class handling business logic for Bed Allocations.
 */
@Service
@RequiredArgsConstructor
public class AllocationService {

    private final AllocationRepository allocationRepository;
    private final BedRepository bedRepository;
    private final ApplicationEventPublisher eventPublisher;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<AllocationResponse> getAllAllocations() {
        return allocationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AllocationResponse getAllocationById(UUID id) {
        BedAllocation allocation = allocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bed Allocation not found with ID: " + id));
        return mapToResponse(allocation);
    }

    @Transactional
    public AllocationResponse createAllocation(AllocationRequest request) {
        Patient patient = entityManager.find(Patient.class, request.patientId());
        if (patient == null) {
            throw new ResourceNotFoundException("Patient not found with ID: " + request.patientId());
        }

        Bed bed = bedRepository.findById(request.bedId())
                .orElseThrow(() -> new ResourceNotFoundException("Bed not found with ID: " + request.bedId()));

        if (!"AVAILABLE".equalsIgnoreCase(bed.getAvailabilityStatus())) {
            throw new IllegalArgumentException("Bed " + bed.getBedNumber() + " is currently " + bed.getAvailabilityStatus());
        }

        allocationRepository.findByPatientIdAndStatus(request.patientId(), "ADMITTED").ifPresent(a -> {
            throw new IllegalArgumentException("Patient is already admitted to bed " + a.getBed().getBedNumber());
        });

        Ward ward = bed.getWard();
        long activeCount = allocationRepository.countActiveAllocationsInWard(ward.getId());

        if (activeCount >= ward.getCapacity()) {
            eventPublisher.publishEvent(new WardCapacityReachedEvent(
                    ward.getId(),
                    ward.getCode(),
                    ward.getName(),
                    (int) activeCount,
                    ward.getCapacity(),
                    Instant.now()
            ));
            throw new IllegalArgumentException("Ward capacity limit (" + ward.getCapacity() + ") reached for ward " + ward.getName());
        }

        bed.setAvailabilityStatus("OCCUPIED");
        bedRepository.save(bed);

        BedAllocation allocation = BedAllocation.builder()
                .patient(patient)
                .bed(bed)
                .admissionDate(request.admissionDate())
                .dischargeDate(request.dischargeDate())
                .status("ADMITTED")
                .build();

        BedAllocation saved = allocationRepository.save(allocation);

        eventPublisher.publishEvent(new BedAllocatedEvent(
                saved.getId(),
                patient.getId(),
                bed.getId(),
                ward.getId(),
                saved.getAdmissionDate()
        ));

        return mapToResponse(saved);
    }

    @Transactional
    public AllocationResponse dischargePatient(UUID id, Instant dischargeTime) {
        BedAllocation allocation = allocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bed Allocation not found with ID: " + id));

        if (!"ADMITTED".equalsIgnoreCase(allocation.getStatus())) {
            throw new IllegalArgumentException("Allocation is already " + allocation.getStatus());
        }

        Bed bed = allocation.getBed();
        bed.setAvailabilityStatus("AVAILABLE");
        bedRepository.save(bed);

        allocation.setStatus("DISCHARGED");
        allocation.setDischargeDate(dischargeTime);
        BedAllocation saved = allocationRepository.save(allocation);

        eventPublisher.publishEvent(new BedReleasedEvent(
                saved.getId(),
                allocation.getPatient().getId(),
                bed.getId(),
                bed.getWard().getId(),
                saved.getDischargeDate()
        ));

        return mapToResponse(saved);
    }

    private AllocationResponse mapToResponse(BedAllocation allocation) {
        return new AllocationResponse(
                allocation.getId(),
                allocation.getPatient().getId(),
                allocation.getPatient().getFullName(),
                allocation.getPatient().getPatientId(),
                allocation.getBed().getId(),
                allocation.getBed().getBedNumber(),
                allocation.getBed().getWard().getId(),
                allocation.getBed().getWard().getName(),
                allocation.getAdmissionDate(),
                allocation.getDischargeDate(),
                allocation.getStatus()
        );
    }
}
