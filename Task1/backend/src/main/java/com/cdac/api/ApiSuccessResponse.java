package com.cdac.api;

import java.time.Instant;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiSuccessResponse<T> {

	private final Instant timestamp;
	private final int status;
	private final boolean success;
	private final String message;
	private final T data;

	public static <T> ApiSuccessResponse<T> of(HttpStatus status, String message, T data) {
		return new ApiSuccessResponse<>(Instant.now(), status.value(), true, message, data);
	}
}
