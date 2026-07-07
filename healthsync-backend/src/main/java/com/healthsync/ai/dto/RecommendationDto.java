package com.healthsync.ai.dto;

/**
 * Data Transfer Object modeling capacity optimization proposals.
 */
public record RecommendationDto(
    String category,
    String action,
    String priority,
    String reason
) {}
