package com.healthsync.dashboard.dto;

import java.util.List;

/**
 * Data Transfer Object modeling historical trends across resource categories.
 */
public record AnalyticsResponse(
    List<TrendPoint> medicineConsumptionTrend,
    List<TrendPoint> patientFootfallTrend,
    List<TrendPoint> doctorAttendanceTrend,
    List<TrendPoint> bedOccupancyTrend
) {
    public record TrendPoint(String date, long value) {}
}
