package com.misyakuji.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private Integer code;   // HTTP 状态码
    private String message; // 错误信息
    private T data;         // 数据

    public static <T> ApiResponse<T> of(Integer code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return of(HttpStatus.OK.value(), "操作成功", data);
    }

    public static <T> ApiResponse<T> fail(HttpStatus status, String message) {
        return of(status.value(), message, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return of(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null);
    }

    public static <T> ApiResponse<T> error(Integer code, String message) {
        return of(code, message, null);
    }

    public boolean isSuccess() {
        return HttpStatus.OK.value() == this.code;
    }
}