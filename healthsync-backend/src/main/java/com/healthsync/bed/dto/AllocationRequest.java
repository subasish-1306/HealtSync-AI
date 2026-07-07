package com.healthsync.bed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object for creating or updating a Bed Allocation.
 */
public record AllocationRequest(
    @NotNull(message = "Patient ID is required")
    UUID patientId,

    @NotNull(message = "Bed ID is required")
    UUID bedId,

    @NotNull(message = "Admission date is required")
    Instant admissionDate,

    Instant dischargeDate,

    @NotBlank(message = "Status is required")
    String status
) {}
