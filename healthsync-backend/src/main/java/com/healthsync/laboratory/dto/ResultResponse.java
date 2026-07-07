package com.healthsync.laboratory.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object representing Test Result response details.
 */
public record ResultResponse(
    UUID id,
    UUID bookingId,
    String result,
    Instant completedDate,
    String remarks
) {}
