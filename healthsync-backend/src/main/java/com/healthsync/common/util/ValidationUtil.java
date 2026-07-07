package com.healthsync.common.util;

import com.healthsync.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Utility functions to trigger programmatic validation checks.
 */
public final class ValidationUtil {
    private ValidationUtil() {}

    private static final Validator VALIDATOR;

    static {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            VALIDATOR = factory.getValidator();
        }
    }

    public static <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(object);
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<T> violation : violations) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new ValidationException("Validation failed", errors);
        }
    }
}
