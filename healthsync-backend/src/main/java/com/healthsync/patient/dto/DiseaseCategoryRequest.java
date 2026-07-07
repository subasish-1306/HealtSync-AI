package com.healthsync.patient.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating or updating a Disease Category.
 */
public record DiseaseCategoryRequest(
    @NotBlank(message = "ICD-10 code is required")
    @Size(max = 20, message = "Code must be at most 20 characters")
    String code,

    @NotBlank(message = "Disease category name is required")
    @Size(max = 150, message = "Name must be at most 150 characters")
    String name,

    String description,

    @NotNull(message = "Priority is required")
    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 5, message = "Priority cannot exceed 5")
    Integer priority,

    @NotNull(message = "Communicable flag is required")
    Boolean communicable,

    @NotNull(message = "Seasonal flag is required")
    Boolean seasonal
) {}
