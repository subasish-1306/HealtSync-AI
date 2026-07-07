package com.healthsync.bed.controller;

import com.healthsync.bed.dto.BedRequest;
import com.healthsync.bed.dto.BedResponse;
import com.healthsync.bed.service.BedService;
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
 * Controller exposing endpoints for Bed management.
 */
@RestController
@RequestMapping("/api/beds")
@RequiredArgsConstructor
@Tag(name = "Beds", description = "Endpoints for managing inpatient Beds")
public class BedController {

    private final BedService bedService;

    @GetMapping
    @Operation(summary = "Get all beds")
    public ResponseEntity<List<BedResponse>> getAllBeds() {
        return ResponseEntity.ok(bedService.getAllBeds());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get bed by ID")
    public ResponseEntity<BedResponse> getBedById(@PathVariable UUID id) {
        return ResponseEntity.ok(bedService.getBedById(id));
    }

    @GetMapping("/ward/{wardId}")
    @Operation(summary = "Get beds by Ward ID")
    public ResponseEntity<List<BedResponse>> getBedsByWardId(@PathVariable UUID wardId) {
        return ResponseEntity.ok(bedService.getBedsByWardId(wardId));
    }

    @PostMapping
    @Operation(summary = "Create a new bed")
    public ResponseEntity<BedResponse> createBed(@Valid @RequestBody BedRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bedService.createBed(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update bed details")
    public ResponseEntity<BedResponse> updateBed(
            @PathVariable UUID id,
            @Valid @RequestBody BedRequest request
    ) {
        return ResponseEntity.ok(bedService.updateBed(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a bed")
    public ResponseEntity<Void> deleteBed(@PathVariable UUID id) {
        bedService.deleteBed(id);
        return ResponseEntity.noContent().build();
    }
}
