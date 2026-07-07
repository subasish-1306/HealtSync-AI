package com.healthsync.auth.dto;

import java.util.UUID;

/**
 * Record representing the user profile response payload.
 */
public record UserResponse(
    UUID id,
    String username,
    String email,
    String fullName,
    String role,
    String status
) {}
