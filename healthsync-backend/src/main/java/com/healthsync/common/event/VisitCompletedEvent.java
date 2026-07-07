package com.healthsync.common.event;

import java.util.UUID;

/**
 * Event published upon patient visit completion.
 */
public record VisitCompletedEvent(
    UUID visitId,
    UUID patientId,
    String patientCode,
    String gender,
    int age,
    UUID healthCenterId,
    String department,
    int visitDuration,
    int waitingTime,
    String ageGroup,
    String diseaseCategoryCode,
    boolean isCommunicable
) {}
