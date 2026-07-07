package com.healthsync.notification.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object representing Notification details.
 */
public record NotificationResponse(
    UUID id,
    String message,
    String type,
    String status,
    UUID healthCenterId,
    String healthCenterName,
    UUID userId,
    String username,
    Instant timestamp
) {}
