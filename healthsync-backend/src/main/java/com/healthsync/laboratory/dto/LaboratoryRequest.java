package com.healthsync.laboratory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Data Transfer Object for creating or updating a Laboratory.
 */
public record LaboratoryRequest(
    @NotBlank(message = "Laboratory name is required")
    String name,

    @NotNull(message = "Health Center ID is required")
    UUID healthCenterId,

    @NotBlank(message = "Status is required")
    String status,

    @NotBlank(message = "Working hours description is required")
    String workingHours,

    @NotNull(message = "Equipment count is required")
    @Min(value = 0, message = "Equipment count cannot be negative")
    Integer equipmentCount
) {}
