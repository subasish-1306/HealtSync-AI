package com.healthsync.patient.controller;

import com.healthsync.patient.dto.DiseaseCategoryRequest;
import com.healthsync.patient.dto.DiseaseCategoryResponse;
import com.healthsync.patient.service.DiseaseCategoryService;
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
 * Controller exposing endpoints for Disease Category operations.
 */
@RestController
@RequestMapping("/api/disease-categories")
@RequiredArgsConstructor
@Tag(name = "Disease Categories", description = "Endpoints for managing ICD-10 disease categories")
public class DiseaseCategoryController {

    private final DiseaseCategoryService diseaseCategoryService;

    @GetMapping
    @Operation(summary = "Get all disease categories")
    public ResponseEntity<List<DiseaseCategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(diseaseCategoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get disease category by ID")
    public ResponseEntity<DiseaseCategoryResponse> getCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(diseaseCategoryService.getCategoryById(id));
    }

    @PostMapping
    @Operation(summary = "Create a disease category")
    public ResponseEntity<DiseaseCategoryResponse> createCategory(@Valid @RequestBody DiseaseCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(diseaseCategoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update disease category details")
    public ResponseEntity<DiseaseCategoryResponse> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody DiseaseCategoryRequest request
    ) {
        return ResponseEntity.ok(diseaseCategoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a disease category")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        diseaseCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
