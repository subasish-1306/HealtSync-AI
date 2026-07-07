package com.healthsync.bed.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object representing Bed Allocation stay details.
 */
public record AllocationResponse(
    UUID id,
    UUID patientId,
    String patientName,
    String patientCode,
    UUID bedId,
    String bedNumber,
    UUID wardId,
    String wardName,
    Instant admissionDate,
    Instant dischargeDate,
    String status
) {}
