package com.company.enterprise.core.response;

public record ApiResponse<T>(boolean success, String message, T data) {

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static ApiResponse<ApiError> failure(String message, ApiError error) {
        return new ApiResponse<>(false, message, error);
    }
}
