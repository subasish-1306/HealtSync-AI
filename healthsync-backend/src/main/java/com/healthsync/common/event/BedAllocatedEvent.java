package com.healthsync.common.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when a bed is successfully allocated to a patient.
 */
public record BedAllocatedEvent(
    UUID allocationId,
    UUID patientId,
    UUID bedId,
    UUID wardId,
    Instant admissionDate
) {}
