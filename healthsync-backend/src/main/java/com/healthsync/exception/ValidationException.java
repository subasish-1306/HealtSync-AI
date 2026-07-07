package com.healthsync.exception;

import org.springframework.http.HttpStatus;
import java.util.Map;

/**
 * Exception carrying structured fields and error messages for invalid payload forms.
 */
public class ValidationException extends BusinessException {
    private final Map<String, String> errors;

    public ValidationException(String message, Map<String, String> errors) {
        super(message, HttpStatus.BAD_REQUEST);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
