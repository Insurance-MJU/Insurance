package dto.response.common;

/**
 * 모든 API 응답의 공통 래퍼.
 * 웹 프레임워크 결정 후 JSON 직렬화 어노테이션 추가 예정.
 */
public record ApiResponse<T>(
    boolean success,
    String message,
    T data
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, null, data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
