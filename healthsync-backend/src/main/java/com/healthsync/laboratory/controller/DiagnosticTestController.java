package com.healthsync.laboratory.controller;

import com.healthsync.laboratory.dto.DiagnosticTestRequest;
import com.healthsync.laboratory.dto.DiagnosticTestResponse;
import com.healthsync.laboratory.service.DiagnosticTestService;
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
 * REST controller exposing endpoints for Laboratory Tests management.
 */
@RestController
@RequestMapping("/api/diagnostic-tests")
@RequiredArgsConstructor
@Tag(name = "Laboratory Tests", description = "Endpoints for managing Diagnostic/Laboratory Tests")
public class DiagnosticTestController {

    private final DiagnosticTestService diagnosticTestService;

    @GetMapping
    @Operation(summary = "Get all diagnostic tests")
    public ResponseEntity<List<DiagnosticTestResponse>> getAllTests() {
        return ResponseEntity.ok(diagnosticTestService.getAllTests());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get test by ID")
    public ResponseEntity<DiagnosticTestResponse> getTestById(@PathVariable UUID id) {
        return ResponseEntity.ok(diagnosticTestService.getTestById(id));
    }

    @PostMapping
    @Operation(summary = "Create a diagnostic test")
    public ResponseEntity<DiagnosticTestResponse> createTest(@Valid @RequestBody DiagnosticTestRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(diagnosticTestService.createTest(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update diagnostic test details")
    public ResponseEntity<DiagnosticTestResponse> updateTest(
            @PathVariable UUID id,
            @Valid @RequestBody DiagnosticTestRequest request
    ) {
        return ResponseEntity.ok(diagnosticTestService.updateTest(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a diagnostic test")
    public ResponseEntity<Void> deleteTest(@PathVariable UUID id) {
        diagnosticTestService.deleteTest(id);
        return ResponseEntity.noContent().build();
    }
}
