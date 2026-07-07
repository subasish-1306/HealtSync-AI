package com.healthsync.common.event;

import java.util.UUID;

/**
 * Event published upon patient registration or update.
 */
public record PatientAnalyticsEvent(
    UUID id,
    String patientId,
    String fullName,
    String gender,
    int age,
    UUID districtId,
    UUID healthCenterId,
    String status
) {}
