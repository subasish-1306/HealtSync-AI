package com.healthsync.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Record representing the payload for user login requests.
 */
public record LoginRequest(
    @NotBlank(message = "Username is required")
    String username,

    @NotBlank(message = "Password is required")
    String password
) {}
