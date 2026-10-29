package com.cdac.egov.integration.gepnic.forms.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard envelope for REST API responses returned by the integration service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseForm<T> {

    @Builder.Default
    private boolean success = true;

    private String message;

    private T data;

    private String errorCode;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ApiResponseForm<T> ok(T data, String message) {
        return ApiResponseForm.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponseForm<T> error(String message, String errorCode) {
        return ApiResponseForm.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
