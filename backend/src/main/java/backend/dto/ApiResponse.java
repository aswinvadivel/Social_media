package backend.dto;

import java.time.Instant;

public record ApiResponse<T>(
	boolean success,
	T data,
	String error,
	Integer statusCode,
	Instant timestamp,
	Pagination pagination
) {

	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, data, null, null, null, null);
	}

	public static <T> ApiResponse<T> success(T data, Pagination pagination) {
		return new ApiResponse<>(true, data, null, null, null, pagination);
	}

	public static <T> ApiResponse<T> error(String message, int statusCode) {
		return new ApiResponse<>(false, null, message, statusCode, Instant.now(), null);
	}
}
