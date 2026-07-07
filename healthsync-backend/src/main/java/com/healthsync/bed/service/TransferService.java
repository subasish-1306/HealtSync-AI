package com.healthsync.bed.service;

import com.healthsync.bed.dto.TransferRequest;
import com.healthsync.bed.dto.TransferResponse;
import com.healthsync.bed.entity.Bed;
import com.healthsync.bed.entity.BedAllocation;
import com.healthsync.bed.entity.BedTransfer;
import com.healthsync.bed.entity.Ward;
import com.healthsync.bed.repository.AllocationRepository;
import com.healthsync.bed.repository.BedRepository;
import com.healthsync.bed.repository.TransferRepository;
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
 * Service class handling business logic for Bed Transfers.
 */
@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;
    private final AllocationRepository allocationRepository;
    private final BedRepository bedRepository;
    private final ApplicationEventPublisher eventPublisher;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<TransferResponse> getAllTransfers() {
        return transferRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransferResponse transferPatient(TransferRequest request) {
        Patient patient = entityManager.find(Patient.class, request.patientId());
        if (patient == null) {
            throw new ResourceNotFoundException("Patient not found with ID: " + request.patientId());
        }

        BedAllocation activeAllocation = allocationRepository.findByPatientIdAndStatus(request.patientId(), "ADMITTED")
                .orElseThrow(() -> new IllegalArgumentException("No active admission record found for patient: " + request.patientId()));

        Bed fromBed = activeAllocation.getBed();
        Bed toBed = bedRepository.findById(request.toBedId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination Bed not found with ID: " + request.toBedId()));

        if (fromBed.getId().equals(toBed.getId())) {
            throw new IllegalArgumentException("Patient is already allocated to target bed: " + toBed.getBedNumber());
        }

        if (!"AVAILABLE".equalsIgnoreCase(toBed.getAvailabilityStatus())) {
            throw new IllegalArgumentException("Destination bed " + toBed.getBedNumber() + " is currently " + toBed.getAvailabilityStatus());
        }

        Ward targetWard = toBed.getWard();
        long activeCount = allocationRepository.countActiveAllocationsInWard(targetWard.getId());

        // Deduct 1 from activeCount if transferring within the same ward since the patient already occupies a slot there
        if (fromBed.getWard().getId().equals(targetWard.getId())) {
            activeCount--;
        }

        if (activeCount >= targetWard.getCapacity()) {
            eventPublisher.publishEvent(new WardCapacityReachedEvent(
                    targetWard.getId(),
                    targetWard.getCode(),
                    targetWard.getName(),
                    (int) activeCount,
                    targetWard.getCapacity(),
                    Instant.now()
            ));
            throw new IllegalArgumentException("Destination ward capacity limit (" + targetWard.getCapacity() + ") reached for ward " + targetWard.getName());
        }

        BedTransfer transfer = BedTransfer.builder()
                .patient(patient)
                .fromBed(fromBed)
                .toBed(toBed)
                .transferTime(request.transferTime())
                .reason(request.reason())
                .build();
        BedTransfer savedTransfer = transferRepository.save(transfer);

        activeAllocation.setStatus("DISCHARGED");
        activeAllocation.setDischargeDate(request.transferTime());
        allocationRepository.save(activeAllocation);

        fromBed.setAvailabilityStatus("AVAILABLE");
        bedRepository.save(fromBed);

        eventPublisher.publishEvent(new BedReleasedEvent(
                activeAllocation.getId(),
                patient.getId(),
                fromBed.getId(),
                fromBed.getWard().getId(),
                request.transferTime()
        ));

        toBed.setAvailabilityStatus("OCCUPIED");
        bedRepository.save(toBed);

        BedAllocation newAllocation = BedAllocation.builder()
                .patient(patient)
                .bed(toBed)
                .admissionDate(request.transferTime())
                .status("ADMITTED")
                .build();
        BedAllocation savedNewAllocation = allocationRepository.save(newAllocation);

        eventPublisher.publishEvent(new BedAllocatedEvent(
                savedNewAllocation.getId(),
                patient.getId(),
                toBed.getId(),
                targetWard.getId(),
                request.transferTime()
        ));

        return mapToResponse(savedTransfer);
    }

    private TransferResponse mapToResponse(BedTransfer transfer) {
        return new TransferResponse(
                transfer.getId(),
                transfer.getPatient().getId(),
                transfer.getPatient().getFullName(),
                transfer.getFromBed().getId(),
                transfer.getFromBed().getBedNumber(),
                transfer.getToBed().getId(),
                transfer.getToBed().getBedNumber(),
                transfer.getTransferTime(),
                transfer.getReason()
        );
    }
}
