package com.healthsync.laboratory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Data Transfer Object for creating or updating Test Availabilities.
 */
public record TestAvailabilityRequest(
    @NotNull(message = "Laboratory ID is required")
    UUID laboratoryId,

    @NotNull(message = "Diagnostic Test ID is required")
    UUID diagnosticTestId,

    @NotNull(message = "Availability flag is required")
    Boolean isAvailable,

    @NotNull(message = "Turnaround time description is required")
    @Min(value = 1, message = "Turnaround time must be at least 1 hour")
    Integer turnaroundTimeHours
) {}
