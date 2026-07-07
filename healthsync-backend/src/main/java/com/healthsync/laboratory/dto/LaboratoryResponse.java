package com.healthsync.laboratory.dto;

import java.util.UUID;

/**
 * Data Transfer Object representing Laboratory response metrics.
 */
public record LaboratoryResponse(
    UUID id,
    String name,
    UUID healthCenterId,
    String healthCenterName,
    String status,
    String workingHours,
    Integer equipmentCount
) {}
