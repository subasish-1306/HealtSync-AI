package com.healthsync.laboratory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Data Transfer Object for creating or updating Diagnostic Tests.
 */
public record DiagnosticTestRequest(
    @NotBlank(message = "Test name is required")
    String testName,

    @NotBlank(message = "Test code is required")
    String code,

    String description,

    @NotBlank(message = "Normal range is required")
    String normalRange,

    @NotNull(message = "Base cost is required")
    @DecimalMin(value = "0.0", message = "Base cost cannot be negative")
    BigDecimal baseCost,

    @NotBlank(message = "Department is required")
    String department,

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    Integer duration,

    @NotNull(message = "Availability flag is required")
    Boolean isAvailable
) {}
