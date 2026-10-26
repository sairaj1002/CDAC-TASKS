package com.cdac.api;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cdac.scope.constant.DropdownConstant;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		exception.getBindingResult().getFieldErrors().forEach(error ->
				fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));
		exception.getBindingResult().getGlobalErrors().forEach(error ->
				fieldErrors.putIfAbsent("form", error.getDefaultMessage()));

		return response(HttpStatus.BAD_REQUEST, DropdownConstant.Messages.VALIDATION_FAILED, fieldErrors);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleUnreadableRequest(HttpMessageNotReadableException exception) {
		return response(HttpStatus.BAD_REQUEST, DropdownConstant.Messages.BODY_INVALID, Map.of());
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(EntityNotFoundException exception) {
		return response(HttpStatus.NOT_FOUND, exception.getMessage(), Map.of());
	}

	@ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
	public ResponseEntity<ApiErrorResponse> handleBusinessViolation(RuntimeException exception) {
		return response(HttpStatus.BAD_REQUEST, exception.getMessage(), Map.of());
	}

	@ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
	public ResponseEntity<ApiErrorResponse> handleResponseStatus(org.springframework.web.server.ResponseStatusException exception) {
		HttpStatus status = HttpStatus.resolve(exception.getStatusCode().value());
		if (status == null) {
			status = HttpStatus.BAD_REQUEST;
		}
		String message = exception.getReason() != null ? exception.getReason() : exception.getMessage();
		return response(status, message, Map.of());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception) {
		LOGGER.error("Unexpected API error.", exception);
		return response(
				HttpStatus.INTERNAL_SERVER_ERROR,
				DropdownConstant.Messages.UNEXPECTED_ERROR,
				Map.of());
	}

	private static ResponseEntity<ApiErrorResponse> response(
			HttpStatus status,
			String message,
			Map<String, String> fieldErrors) {
		return ResponseEntity.status(status)
				.body(new ApiErrorResponse(
						Instant.now(),
						status.value(),
						false,
						status.getReasonPhrase(),
						message,
						fieldErrors));
	}
}
