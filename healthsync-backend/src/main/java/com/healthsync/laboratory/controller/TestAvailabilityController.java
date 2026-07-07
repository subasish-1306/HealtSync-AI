package com.healthsync.laboratory.controller;

import com.healthsync.laboratory.dto.TestAvailabilityRequest;
import com.healthsync.laboratory.dto.TestAvailabilityResponse;
import com.healthsync.laboratory.service.TestAvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller exposing endpoints for Test Availability inside laboratories.
 */
@RestController
@RequestMapping("/api/test-availabilities")
@RequiredArgsConstructor
@Tag(name = "Test Availabilities", description = "Endpoints for managing test availability in labs")
public class TestAvailabilityController {

    private final TestAvailabilityService testAvailabilityService;

    @GetMapping
    @Operation(summary = "Get all test availabilities")
    public ResponseEntity<List<TestAvailabilityResponse>> getAllAvailabilities() {
        return ResponseEntity.ok(testAvailabilityService.getAllAvailabilities());
    }

    @GetMapping("/laboratory/{labId}")
    @Operation(summary = "Get availabilities by Laboratory ID")
    public ResponseEntity<List<TestAvailabilityResponse>> getAvailabilitiesByLab(@PathVariable UUID labId) {
        return ResponseEntity.ok(testAvailabilityService.getAvailabilitiesByLab(labId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get availability record by ID")
    public ResponseEntity<TestAvailabilityResponse> getAvailabilityById(@PathVariable UUID id) {
        return ResponseEntity.ok(testAvailabilityService.getAvailabilityById(id));
    }

    @PostMapping
    @Operation(summary = "Define test availability in a lab")
    public ResponseEntity<TestAvailabilityResponse> createAvailability(@Valid @RequestBody TestAvailabilityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(testAvailabilityService.createAvailability(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update test availability details")
    public ResponseEntity<TestAvailabilityResponse> updateAvailability(
            @PathVariable UUID id,
            @Valid @RequestBody TestAvailabilityRequest request
    ) {
        return ResponseEntity.ok(testAvailabilityService.updateAvailability(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an availability record")
    public ResponseEntity<Void> deleteAvailability(@PathVariable UUID id) {
        testAvailabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}
