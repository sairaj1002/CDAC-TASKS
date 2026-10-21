package com.cdac.api;

import java.time.Instant;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiErrorResponse {

	private final Instant timestamp;
	private final int status;
	private final boolean success;
	private final String error;
	private final String message;
	private final Map<String, String> fieldErrors;
}
