package com.healthsync.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Data Transfer Object for creating or updating a user notification preference.
 */
public record PreferenceRequest(
    @NotNull(message = "User ID is required")
    UUID userId,

    @NotBlank(message = "Notification type is required")
    String notificationType,

    @NotBlank(message = "Channel is required")
    String channel,

    @NotNull(message = "Enabled flag is required")
    Boolean enabled
) {}
