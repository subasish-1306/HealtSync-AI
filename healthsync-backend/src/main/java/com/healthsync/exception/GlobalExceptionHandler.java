package com.healthsync.exception;

import com.healthsync.common.ApiError;
import com.healthsync.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * ControllerAdvice intercepting throwables to format uniform JSON error models.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        logger.error("BusinessException: {}", ex.getMessage());
        HttpStatus status = ex.getStatus();
        
        Map<String, String> details = null;
        if (ex instanceof ValidationException valEx) {
            details = valEx.getErrors();
        }
        
        ApiError apiError = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                details
        );
        return ResponseEntity.status(status).body(ApiResponse.error(apiError, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        logger.error("Validation error: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError apiError = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                "Validation failed",
                request.getRequestURI(),
                errors
        );
        return ResponseEntity.status(status).body(ApiResponse.error(apiError, "Validation failed"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        logger.error("Access denied error: {}", ex.getMessage());
        HttpStatus status = HttpStatus.FORBIDDEN;
        ApiError apiError = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                "Access is denied",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(ApiResponse.error(apiError, "Access denied"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled exception occurred", ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiError apiError = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                "An unexpected internal error occurred",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(ApiResponse.error(apiError, "Internal server error"));
    }
}
