package com.healthsync.dashboard.dto;

import java.util.UUID;

/**
 * Data Transfer Object modeling geo-locations with risk colors and operational scores.
 */
public record MapLocationResponse(
    UUID healthCenterId,
    String healthCenterName,
    double latitude,
    double longitude,
    String riskStatus,
    int healthScore
) {}
