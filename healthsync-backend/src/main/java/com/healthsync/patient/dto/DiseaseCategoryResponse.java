package com.healthsync.patient.dto;

import java.util.UUID;

/**
 * Data Transfer Object representing disease category details.
 */
public record DiseaseCategoryResponse(
    UUID id,
    String code,
    String name,
    String description,
    int priority,
    boolean communicable,
    boolean seasonal
) {}
