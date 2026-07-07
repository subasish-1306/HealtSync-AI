package com.healthsync.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Record representing the token refresh request payload.
 */
public record RefreshTokenRequest(
    @NotBlank(message = "Refresh token is required")
    String refreshToken
) {}
