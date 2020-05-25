package com.nomadconnection.dapp.api.dto.gateway.shinhan.response;

import lombok.ToString;
import org.springframework.http.HttpStatus;

@ToString
public class ApiResponse<T> {

    private final T data;

    private final ApiResult result;

    private ApiResponse(T data, ApiResult result) {
        this.data = data;
        this.result = result;
    }

    public static <T> ApiResponse<T> OK(T data) {
        return new ApiResponse<>(data, null);
    }

    public static <T> ApiResponse<T> ERROR(Throwable throwable, HttpStatus status) {
        return new ApiResponse<>(null, new ApiResult(throwable, status));
    }

    public static <T> ApiResponse<T> ERROR(String errorMessage, HttpStatus status) {
        return new ApiResponse<>(null, new ApiResult(errorMessage, status));
    }

    public T getData() {
        return data;
    }

    public ApiResult getResult() {
        return result;
    }

}
