package com.nomadconnection.dapp.api.dto.gateway;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.*;
import org.springframework.http.HttpStatus;

@JsonInclude
@ToString
@Setter
@Getter
@Builder
@NoArgsConstructor
public class ApiResponse<T> {

    private ApiResult result;

    private T data;

    public ApiResponse(ApiResult result, T data) {
        this.data = data;
        this.result = result;
    }

    public static <T> ApiResponse<T> OK() {
        return new ApiResponse<>(new ApiResult(HttpStatus.OK), null);
    }

    public static <T> ApiResponse<T> SUCCESS() {
        return new ApiResponse<>(new ApiResult(ErrorCode.Api.SUCCESS), null);
    }

    public static <T> ApiResponse<T> SUCCESS(T data) {
        return new ApiResponse<>(new ApiResult(ErrorCode.Api.SUCCESS), data);
    }

    public static <T> ApiResponse<T> OK(T data) {
        return new ApiResponse<>(new ApiResult(HttpStatus.OK), data);
    }

    public static <T> ApiResponse<T> ERROR(Throwable throwable, HttpStatus status) {
        return new ApiResponse<>(new ApiResult(throwable, status), null);
    }

    public static <T> ApiResponse<T> ERROR(String errorMessage, HttpStatus status) {
        return new ApiResponse<>(new ApiResult(errorMessage, status), null);
    }

    public ApiResult getResult() {
        return result;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude
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
