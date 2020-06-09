package com.nomadconnection.dapp.api.dto.shinhan.gateway.response;

import lombok.ToString;
import org.springframework.http.HttpStatus;

@ToString
public class ApiResponse {

    private final Object data;

    private final ApiResult result;

    private ApiResponse(Object data, ApiResult result) {
        this.data = data;
        this.result = result;
    }

    public static  ApiResponse OK(Object data) {
        return new ApiResponse(data, null);
    }

    public static  ApiResponse ERROR(Throwable throwable, HttpStatus status) {
        return new ApiResponse(null, new ApiResult(throwable, status));
    }

    public static  ApiResponse ERROR(String errorMessage, HttpStatus status) {
        return new ApiResponse(null, new ApiResult(errorMessage, status));
    }

    public Object getData() {
        return data;
    }

    public ApiResult getResult() {
        return result;
    }

}
