package com.healthsync.bed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object for creating a Bed Transfer stay record.
 */
public record TransferRequest(
    @NotNull(message = "Patient ID is required")
    UUID patientId,

    @NotNull(message = "Destination Bed ID is required")
    UUID toBedId,

    @NotNull(message = "Transfer time is required")
    Instant transferTime,

    @NotBlank(message = "Reason for transfer is required")
    String reason
) {}
