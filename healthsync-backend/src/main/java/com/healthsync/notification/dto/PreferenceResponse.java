package com.healthsync.notification.dto;

import java.util.UUID;

/**
 * Data Transfer Object representing user notification preference details.
 */
public record PreferenceResponse(
    UUID id,
    UUID userId,
    String notificationType,
    String channel,
    Boolean enabled
) {}
