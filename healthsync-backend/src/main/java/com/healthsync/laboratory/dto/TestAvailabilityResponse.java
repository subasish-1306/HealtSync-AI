package com.healthsync.laboratory.dto;

import java.util.UUID;

/**
 * Data Transfer Object representing Test Availability response metrics.
 */
public record TestAvailabilityResponse(
    UUID id,
    UUID laboratoryId,
    String laboratoryName,
    UUID diagnosticTestId,
    String testName,
    Boolean isAvailable,
    Integer turnaroundTimeHours
) {}
