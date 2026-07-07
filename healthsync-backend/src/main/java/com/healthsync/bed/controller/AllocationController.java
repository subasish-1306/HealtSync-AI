package com.healthsync.bed.controller;

import com.healthsync.bed.dto.AllocationRequest;
import com.healthsync.bed.dto.AllocationResponse;
import com.healthsync.bed.service.AllocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Controller exposing endpoints for Bed Allocation.
 */
@RestController
@RequestMapping("/api/allocations")
@RequiredArgsConstructor
@Tag(name = "Bed Allocations", description = "Endpoints for managing inpatient Admissions and Stay releases")
public class AllocationController {

    private final AllocationService allocationService;

    @GetMapping
    @Operation(summary = "Get all allocations")
    public ResponseEntity<List<AllocationResponse>> getAllAllocations() {
        return ResponseEntity.ok(allocationService.getAllAllocations());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get allocation details by ID")
    public ResponseEntity<AllocationResponse> getAllocationById(@PathVariable UUID id) {
        return ResponseEntity.ok(allocationService.getAllocationById(id));
    }

    @PostMapping
    @Operation(summary = "Admit patient / Allocate bed")
    public ResponseEntity<AllocationResponse> createAllocation(@Valid @RequestBody AllocationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(allocationService.createAllocation(request));
    }

    @PutMapping("/{id}/discharge")
    @Operation(summary = "Discharge patient / Release bed")
    public ResponseEntity<AllocationResponse> dischargePatient(
            @PathVariable UUID id,
            @RequestParam(required = false) Instant dischargeTime
    ) {
        Instant time = dischargeTime != null ? dischargeTime : Instant.now();
        return ResponseEntity.ok(allocationService.dischargePatient(id, time));
    }
}
