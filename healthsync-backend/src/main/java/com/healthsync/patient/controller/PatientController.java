package com.healthsync.patient.controller;

import com.healthsync.patient.dto.PatientRequest;
import com.healthsync.patient.dto.PatientResponse;
import com.healthsync.patient.service.PatientService;
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
 * Controller exposing endpoints for Patient registration and profile search.
 */
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Endpoints for managing Patient registry profiles")
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    @Operation(summary = "Search patients or list all matching parameters")
    public ResponseEntity<List<PatientResponse>> searchPatients(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(patientService.searchPatients(query, mobile, patientId, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/code/{patientId}")
    @Operation(summary = "Get patient by Patient ID code")
    public ResponseEntity<PatientResponse> getPatientByCode(@PathVariable String patientId) {
        return ResponseEntity.ok(patientService.getPatientByCode(patientId));
    }

    @PostMapping
    @Operation(summary = "Register a new patient")
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody PatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.createPatient(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update patient details")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable UUID id,
            @Valid @RequestBody PatientRequest request
    ) {
        return ResponseEntity.ok(patientService.updatePatient(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a patient record")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
