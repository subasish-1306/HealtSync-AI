package com.healthsync.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when the client request is invalid or malformed.
 */
public class BadRequestException extends BusinessException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
