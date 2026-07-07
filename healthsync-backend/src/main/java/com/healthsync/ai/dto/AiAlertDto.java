package com.healthsync.ai.dto;

/**
 * Data Transfer Object modeling warning triggers.
 */
public record AiAlertDto(
    String alertType,
    String message,
    String severity,
    String healthCenterName
) {}
