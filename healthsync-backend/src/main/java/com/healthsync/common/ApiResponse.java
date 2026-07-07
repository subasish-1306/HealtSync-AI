package com.healthsync.common;

import java.time.Instant;

/**
 * Standard API wrapper record for all REST endpoints.
 */
public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    ApiError error,
    Instant timestamp
) {
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, null, Instant.now());
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Request completed successfully");
    }

    public static <T> ApiResponse<T> error(ApiError error, String message) {
        return new ApiResponse<>(false, message, null, error, Instant.now());
    }

    public static <T> ApiResponse<T> error(ApiError error) {
        return error(error, "An error occurred");
    }
}
