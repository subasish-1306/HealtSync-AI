package com.healthsync.bed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Data Transfer Object for creating or updating a Bed.
 */
public record BedRequest(
    @NotBlank(message = "Bed number is required")
    String bedNumber,

    @NotBlank(message = "Bed type is required")
    String bedType,

    @NotBlank(message = "Availability status is required")
    String availabilityStatus,

    @NotBlank(message = "Cleaning status is required")
    String cleaningStatus,

    @NotBlank(message = "Maintenance status is required")
    String maintenanceStatus,

    @NotNull(message = "Ward ID is required")
    UUID wardId
) {}
