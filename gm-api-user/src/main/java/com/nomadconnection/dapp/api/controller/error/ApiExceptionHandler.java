package com.nomadconnection.dapp.api.controller.error;

import com.nomadconnection.dapp.api.dto.shinhan.gateway.response.ApiResponse;
import com.nomadconnection.dapp.api.exception.gateway.InternalErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(InternalErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ApiResponse<?> onAlreadyExistException(InternalErrorException e) {

        return ApiResponse.builder()
                .result(ApiResponse.ApiResult.builder()
                        .code(e.getCode())
                        .desc(e.getDesc())
                        .build())
                .build();
    }
}
