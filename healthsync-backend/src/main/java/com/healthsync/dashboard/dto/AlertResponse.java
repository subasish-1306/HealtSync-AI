package com.healthsync.dashboard.dto;

import java.util.List;

/**
 * Data Transfer Object modeling consolidated lists of critical warnings.
 */
public record AlertResponse(
    List<AlertDetail> medicineShortageAlerts,
    List<AlertDetail> doctorShortageAlerts,
    List<AlertDetail> bedFullAlerts,
    List<AlertDetail> criticalHealthCenterAlerts
) {
    public record AlertDetail(
        String healthCenterName,
        String message,
        String severity
    ) {}
}
