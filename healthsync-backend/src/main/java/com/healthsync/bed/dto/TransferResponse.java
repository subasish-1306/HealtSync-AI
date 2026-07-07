package com.healthsync.bed.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object representing patient transfer details.
 */
public record TransferResponse(
    UUID id,
    UUID patientId,
    String patientName,
    UUID fromBedId,
    String fromBedNumber,
    UUID toBedId,
    String toBedNumber,
    Instant transferTime,
    String reason
) {}
