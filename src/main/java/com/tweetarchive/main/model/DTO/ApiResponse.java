package com.tweetarchive.main.model.DTO;

public record ApiResponse<T>(
        boolean success,
        T data,
        ApiError<T> error) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> fail(String code, String message, T details) {
        return new ApiResponse<>(false, null, new ApiError<T>(code, message, details));
    }
}