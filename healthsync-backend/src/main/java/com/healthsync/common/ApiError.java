package com.healthsync.common;

import java.time.Instant;
import java.util.Map;

/**
 * Standardized record encapsulating client-facing API error properties.
 */
public record ApiError(
    int status,
    String error,
    String message,
    String path,
    Map<String, String> details,
    Instant timestamp
) {
    public ApiError(int status, String error, String message, String path) {
        this(status, error, message, path, null, Instant.now());
    }

    public ApiError(int status, String error, String message, String path, Map<String, String> details) {
        this(status, error, message, path, details, Instant.now());
    }
}
