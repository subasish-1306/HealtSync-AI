package com.healthsync.laboratory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object for creating or updating Test Bookings.
 */
public record BookingRequest(
    @NotNull(message = "Patient ID is required")
    UUID patientId,

    @NotNull(message = "Referral Doctor ID is required")
    UUID doctorId,

    @NotNull(message = "Diagnostic Test ID is required")
    UUID diagnosticTestId,

    @NotNull(message = "Booking date is required")
    LocalDate bookingDate,

    @NotBlank(message = "Status is required")
    String status,

    @NotBlank(message = "Priority is required")
    String priority
) {}
