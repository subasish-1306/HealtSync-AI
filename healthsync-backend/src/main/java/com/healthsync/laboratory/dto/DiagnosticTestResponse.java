package com.healthsync.laboratory.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Data Transfer Object representing Diagnostic Test response details.
 */
public record DiagnosticTestResponse(
    UUID id,
    String testName,
    String code,
    String description,
    String normalRange,
    BigDecimal baseCost,
    String department,
    Integer duration,
    Boolean isAvailable
) {}
