package com.cdac.egov.wms.common.api;

import java.time.Instant;

import org.springframework.http.HttpStatus;

public record ApiSuccessResponse<T>(
		Instant timestamp,
		int status,
		String message,
		T data
) {

	public static <T> ApiSuccessResponse<T> of(HttpStatus status, String message, T data) {
		return new ApiSuccessResponse<>(Instant.now(), status.value(), message, data);
	}
}
