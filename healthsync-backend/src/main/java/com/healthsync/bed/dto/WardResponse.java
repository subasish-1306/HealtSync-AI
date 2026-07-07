package com.healthsync.bed.dto;

import java.util.UUID;

/**
 * Data Transfer Object representing Ward response details.
 */
public record WardResponse(
    UUID id,
    String code,
    String name,
    String department,
    Integer floor,
    Integer capacity,
    String status,
    UUID healthCenterId,
    String healthCenterName
) {}
