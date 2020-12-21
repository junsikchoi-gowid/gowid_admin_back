package com.nomadconnection.dapp.api.dto.gateway;

import com.nomadconnection.dapp.core.dto.response.ErrorCode;
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

    public static <T> ApiResponse<T> OK() {
        return new ApiResponse<>(null, new ApiResult(HttpStatus.OK));
    }

    public static <T> ApiResponse<T> SUCCESS() {
        return new ApiResponse<>(null, new ApiResult(ErrorCode.Api.SUCCESS));
    }

    public static <T> ApiResponse<T> SUCCESS(T data) {
        return new ApiResponse<>(data, new ApiResult(ErrorCode.Api.SUCCESS));
    }

    public static <T> ApiResponse<T> OK(T data) {
        return new ApiResponse<>(data, new ApiResult(HttpStatus.OK));
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
        private String extraMessage;

        ApiResult(Throwable throwable, HttpStatus status) {
            this(throwable.getMessage(), status);
        }

        ApiResult(String desc, HttpStatus status) {
            code = String.valueOf(status.value());
            this.desc = desc;
        }

        ApiResult(HttpStatus status) {
            code = String.valueOf(status.value());
            desc = status.getReasonPhrase();
        }

        ApiResult(ErrorCode.Api codeType, String extraMessage) {
            code = codeType.getCode();
            desc = codeType.getDesc();
            this.extraMessage = extraMessage;
        }

        ApiResult(ErrorCode.Api codeType) {
            code = codeType.getCode();
            desc = codeType.getDesc();
        }

    }

}
