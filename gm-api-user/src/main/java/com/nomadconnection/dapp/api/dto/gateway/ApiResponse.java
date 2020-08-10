package com.nomadconnection.dapp.api.dto.gateway;

import lombok.*;
import org.springframework.http.HttpStatus;

@ToString
@Setter
@Getter
@Builder
@NoArgsConstructor
public class ApiResponse<T> {

    private T data;

    private ApiResult result;

    public ApiResponse(T data, ApiResult result) {
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

    public ApiResult getResult() {
        return result;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApiResult {

        private String code;
        private String desc;

        ApiResult(Throwable throwable, HttpStatus status) {
            this(throwable.getMessage(), status);
        }

        ApiResult(String desc, HttpStatus status) {
            code = String.valueOf(status.value());
            this.desc = desc;
        }

    }

}
