package com.healthsync.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication or authorization claims fail validation.
 */
public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
