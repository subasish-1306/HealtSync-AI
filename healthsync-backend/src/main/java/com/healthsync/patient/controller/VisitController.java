package com.healthsync.patient.controller;

import com.healthsync.patient.dto.VisitRequest;
import com.healthsync.patient.dto.VisitResponse;
import com.healthsync.patient.service.VisitService;
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
 * Controller exposing endpoints for Patient Visit check-ins and clinical updates.
 */
@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
@Tag(name = "Patient Visits", description = "Endpoints for managing Patient clinical check-ins and footfall logs")
public class VisitController {

    private final VisitService visitService;

    @GetMapping
    @Operation(summary = "Search patient visits using parameters")
    public ResponseEntity<List<VisitResponse>> searchVisits(
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID healthCenterId,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Instant startDate,
            @RequestParam(required = false) Instant endDate
    ) {
        return ResponseEntity.ok(visitService.searchVisits(patientId, healthCenterId, department, startDate, endDate));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get visit by ID")
    public ResponseEntity<VisitResponse> getVisitById(@PathVariable UUID id) {
        return ResponseEntity.ok(visitService.getVisitById(id));
    }

    @PostMapping
    @Operation(summary = "Log a new patient visit check-in")
    public ResponseEntity<VisitResponse> createVisit(@Valid @RequestBody VisitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(visitService.createVisit(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update visit records")
    public ResponseEntity<VisitResponse> updateVisit(
            @PathVariable UUID id,
            @Valid @RequestBody VisitRequest request
    ) {
        return ResponseEntity.ok(visitService.updateVisit(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a patient visit log")
    public ResponseEntity<Void> deleteVisit(@PathVariable UUID id) {
        visitService.deleteVisit(id);
        return ResponseEntity.noContent().build();
    }
}
