package com.cdac.egov.integration.gepnic.exception;

import com.cdac.egov.integration.gepnic.forms.common.ApiResponseForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized exception interceptor. Ensures security by never leaking stack traces or cryptographic keys.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseForm<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("Form validation failed: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseForm.<Map<String, String>>builder()
                        .success(false)
                        .message("Validation failed for one or more fields")
                        .errorCode("VALIDATION_ERROR")
                        .data(errors)
                        .build());
    }

    @ExceptionHandler(GePNICCryptoException.class)
    public ResponseEntity<ApiResponseForm<Void>> handleCryptoException(GePNICCryptoException ex) {
        log.error("Cryptographic operation failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseForm.error("Cryptographic error occurred during payload processing", "CRYPTO_ERROR"));
    }

    @ExceptionHandler(GePNICApiException.class)
    public ResponseEntity<ApiResponseForm<Void>> handleApiException(GePNICApiException ex) {
        log.error("GePNIC upstream API error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponseForm.error(ex.getMessage(), "UPSTREAM_API_ERROR"));
    }

    @ExceptionHandler(GePNICException.class)
    public ResponseEntity<ApiResponseForm<Void>> handleGenericGePNICException(GePNICException ex) {
        log.error("GePNIC integration service error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseForm.error(ex.getMessage(), "INTEGRATION_ERROR"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseForm<Void>> handleUnexpectedException(Exception ex) {
        log.error("Unexpected internal server error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseForm.error("An unexpected internal server error occurred", "INTERNAL_ERROR"));
    }
}
