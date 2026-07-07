package com.healthsync.bed.controller;

import com.healthsync.bed.dto.WardRequest;
import com.healthsync.bed.dto.WardResponse;
import com.healthsync.bed.service.WardService;
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
 * Controller exposing endpoints for Ward management.
 */
@RestController
@RequestMapping("/api/wards")
@RequiredArgsConstructor
@Tag(name = "Wards", description = "Endpoints for managing inpatient Wards")
public class WardController {

    private final WardService wardService;

    @GetMapping
    @Operation(summary = "Get all wards")
    public ResponseEntity<List<WardResponse>> getAllWards() {
        return ResponseEntity.ok(wardService.getAllWards());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ward by ID")
    public ResponseEntity<WardResponse> getWardById(@PathVariable UUID id) {
        return ResponseEntity.ok(wardService.getWardById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new ward")
    public ResponseEntity<WardResponse> createWard(@Valid @RequestBody WardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wardService.createWard(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update ward details")
    public ResponseEntity<WardResponse> updateWard(
            @PathVariable UUID id,
            @Valid @RequestBody WardRequest request
    ) {
        return ResponseEntity.ok(wardService.updateWard(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a ward")
    public ResponseEntity<Void> deleteWard(@PathVariable UUID id) {
        wardService.deleteWard(id);
        return ResponseEntity.noContent().build();
    }
}
