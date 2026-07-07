package com.healthsync.bed.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Data Transfer Object for creating or updating a Ward.
 */
public record WardRequest(
    @NotBlank(message = "Ward code is required")
    String code,

    @NotBlank(message = "Ward name is required")
    String name,

    @NotBlank(message = "Department is required")
    String department,

    @NotNull(message = "Floor is required")
    Integer floor,

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    Integer capacity,

    @NotBlank(message = "Status is required")
    String status,

    @NotNull(message = "Health Center ID is required")
    UUID healthCenterId
) {}
