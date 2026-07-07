package com.healthsync.ai.dto;

import java.util.UUID;

/**
 * Data Transfer Object representing AI Health Center evaluation score.
 */
public record HealthScoreResponse(
    UUID healthCenterId,
    String healthCenterName,
    int score,
    String status,
    String breakdown
) {}
