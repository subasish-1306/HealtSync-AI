package com.healthsync.auth.dto;

/**
 * Record representing the authentication response payload.
 */
public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    String username,
    String role
) {}
