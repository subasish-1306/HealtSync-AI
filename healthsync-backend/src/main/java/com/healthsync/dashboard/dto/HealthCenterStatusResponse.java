package com.healthsync.dashboard.dto;

import java.util.UUID;

/**
 * Data Transfer Object modeling operational metrics for individual Health Centers.
 */
public record HealthCenterStatusResponse(
    UUID id,
    String name,
    String type,
    String districtName,
    String medicineStatus,
    String doctorAvailability,
    String bedOccupancy,
    long todayPatientCount,
    int healthScore,
    String riskLevel
) {}
