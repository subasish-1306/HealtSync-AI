package com.healthsync.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Record representing the change password request payload.
 */
public record ChangePasswordRequest(
    @NotBlank(message = "Old password is required")
    String oldPassword,

    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 100, message = "New password must be at least 6 characters")
    String newPassword
) {}
