package com.healthsync.common.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when a ward reaches its maximum capacity.
 */
public record WardCapacityReachedEvent(
    UUID wardId,
    String wardCode,
    String wardName,
    int currentOccupiedCount,
    int capacity,
    Instant timestamp
) {}
