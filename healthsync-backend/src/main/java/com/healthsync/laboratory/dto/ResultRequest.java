package com.healthsync.laboratory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Data Transfer Object for creating or updating Test Results logs.
 */
public record ResultRequest(
    @NotNull(message = "Booking ID is required")
    UUID bookingId,

    @NotBlank(message = "Result description is required")
    String result,

    String remarks
) {}
