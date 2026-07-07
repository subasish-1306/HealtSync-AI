package com.healthsync.common.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when a bed allocation is released.
 */
public record BedReleasedEvent(
    UUID allocationId,
    UUID patientId,
    UUID bedId,
    UUID wardId,
    Instant dischargeDate
) {}
