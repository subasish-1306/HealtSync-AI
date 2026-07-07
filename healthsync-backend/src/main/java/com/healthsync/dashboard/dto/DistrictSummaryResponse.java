package com.healthsync.dashboard.dto;

import java.util.UUID;

/**
 * Data Transfer Object modeling district health metrics summaries.
 */
public record DistrictSummaryResponse(
    UUID districtId,
    String districtName,
    long totalPhcs,
    long totalChcs,
    double averageHealthScore,
    long criticalCentersCount
) {}
