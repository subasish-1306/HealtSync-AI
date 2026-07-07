package com.healthsync.dashboard.dto;

/**
 * Data Transfer Object modeling consolidated aggregate counts for HealthSync AI dashboard metrics.
 */
public record DashboardSummaryResponse(
    long totalHealthCenters,
    long totalDoctors,
    long totalPatientsToday,
    long availableBeds,
    long occupiedBeds,
    long medicineLowStockCount,
    long criticalAlertsCount,
    int overallHealthScore
) {}
