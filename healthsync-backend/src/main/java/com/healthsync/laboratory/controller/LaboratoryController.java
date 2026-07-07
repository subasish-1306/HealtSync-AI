package com.healthsync.laboratory.controller;

import com.healthsync.laboratory.dto.LaboratoryRequest;
import com.healthsync.laboratory.dto.LaboratoryResponse;
import com.healthsync.laboratory.service.LaboratoryService;
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
 * REST controller exposing endpoints for Laboratory management.
 */
@RestController
@RequestMapping("/api/laboratories")
@RequiredArgsConstructor
@Tag(name = "Laboratories", description = "Endpoints for managing Laboratories")
public class LaboratoryController {

    private final LaboratoryService laboratoryService;

    @GetMapping
    @Operation(summary = "Get all laboratories")
    public ResponseEntity<List<LaboratoryResponse>> getAllLaboratories() {
        return ResponseEntity.ok(laboratoryService.getAllLaboratories());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get laboratory by ID")
    public ResponseEntity<LaboratoryResponse> getLaboratoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(laboratoryService.getLaboratoryById(id));
    }

    @PostMapping
    @Operation(summary = "Create a laboratory")
    public ResponseEntity<LaboratoryResponse> createLaboratory(@Valid @RequestBody LaboratoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(laboratoryService.createLaboratory(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update laboratory details")
    public ResponseEntity<LaboratoryResponse> updateLaboratory(
            @PathVariable UUID id,
            @Valid @RequestBody LaboratoryRequest request
    ) {
        return ResponseEntity.ok(laboratoryService.updateLaboratory(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a laboratory")
    public ResponseEntity<Void> deleteLaboratory(@PathVariable UUID id) {
        laboratoryService.deleteLaboratory(id);
        return ResponseEntity.noContent().build();
    }
}
