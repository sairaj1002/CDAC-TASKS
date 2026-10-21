package com.company.enterprise.core.exception;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.company.enterprise.core.response.ApiError;
import com.company.enterprise.core.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleNotFound(
            ResourceNotFoundException exception, WebRequest request) {
        return failure(HttpStatus.NOT_FOUND, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleBusinessException(
            BusinessException exception, WebRequest request) {
        return failure(HttpStatus.BAD_REQUEST, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleValidationException(
            MethodArgumentNotValidException exception, WebRequest request) {
        Map<String, String> errors = new TreeMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        return failure(HttpStatus.BAD_REQUEST, "Request validation failed", request, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleInvalidRequestBody(
            HttpMessageNotReadableException exception, WebRequest request) {
        return failure(HttpStatus.BAD_REQUEST, "Request body is missing or invalid", request, Map.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ApiError>> handleUnexpectedException(
            Exception exception, WebRequest request) {
        LOGGER.error("Unhandled request failure", exception);
        return failure(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected server error occurred",
                request,
                Map.of());
    }

    private ResponseEntity<ApiResponse<ApiError>> failure(
            HttpStatus status, String message, WebRequest request, Map<String, String> validationErrors) {
        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getDescription(false).replace("uri=", ""),
                validationErrors);
        return ResponseEntity.status(status).body(ApiResponse.failure(message, error));
    }
}
