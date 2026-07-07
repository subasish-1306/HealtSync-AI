package com.healthsync.laboratory.controller;

import com.healthsync.laboratory.dto.ResultRequest;
import com.healthsync.laboratory.dto.ResultResponse;
import com.healthsync.laboratory.service.TestResultService;
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
 * REST controller exposing endpoints for laboratory diagnostic results management.
 */
@RestController
@RequestMapping("/api/test-results")
@RequiredArgsConstructor
@Tag(name = "Test Results", description = "Endpoints for managing diagnostic test results")
public class TestResultController {

    private final TestResultService testResultService;

    @GetMapping
    @Operation(summary = "Get all completed results")
    public ResponseEntity<List<ResultResponse>> getAllResults() {
        return ResponseEntity.ok(testResultService.getAllResults());
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get results by Booking ID")
    public ResponseEntity<ResultResponse> getResultByBooking(@PathVariable UUID bookingId) {
        return ResponseEntity.ok(testResultService.getResultByBooking(bookingId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get result details by ID")
    public ResponseEntity<ResultResponse> getResultById(@PathVariable UUID id) {
        return ResponseEntity.ok(testResultService.getResultById(id));
    }

    @PostMapping
    @Operation(summary = "Upload test result metrics")
    public ResponseEntity<ResultResponse> createResult(@Valid @RequestBody ResultRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(testResultService.createResult(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update test result remarks")
    public ResponseEntity<ResultResponse> updateResult(
            @PathVariable UUID id,
            @Valid @RequestBody ResultRequest request
    ) {
        return ResponseEntity.ok(testResultService.updateResult(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a test result")
    public ResponseEntity<Void> deleteResult(@PathVariable UUID id) {
        testResultService.deleteResult(id);
        return ResponseEntity.noContent().build();
    }
}
