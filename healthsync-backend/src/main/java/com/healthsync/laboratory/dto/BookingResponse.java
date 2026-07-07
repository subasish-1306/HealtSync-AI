package com.healthsync.laboratory.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object representing Test Booking status details.
 */
public record BookingResponse(
    UUID id,
    UUID patientId,
    String patientName,
    UUID doctorId,
    String doctorName,
    UUID diagnosticTestId,
    String testName,
    LocalDate bookingDate,
    String status,
    String priority
) {}
