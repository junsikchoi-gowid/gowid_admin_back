package com.nomadconnection.dapp.api.controller.error;

import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.exception.CodefApiException;
import com.nomadconnection.dapp.api.exception.CorpAlreadyExistException;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.exception.api.SystemException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ApiResponse<?> handleSystemException(SystemException e) {

        return ApiResponse.builder()
                .result(ApiResponse.ApiResult.builder()
                        .code(e.getCode())
                        .desc(e.getDesc())
                        .build())
                .build();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiResponse<?> handleBadRequestException(BadRequestException e) {

        return ApiResponse.builder()
                .result(ApiResponse.ApiResult.builder()
                        .code(e.getCode())
                        .desc(e.getDesc())
                        .build())
                .build();
    }

    @ExceptionHandler(CodefApiException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ApiResponse<?> handleCodefApiException(CodefApiException e) {

        return ApiResponse.builder()
            .result(ApiResponse.ApiResult.builder()
                .code(e.getCode())
                .desc(e.getMessage())
                .build())
            .build();
    }

    @ExceptionHandler(CorpAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ApiResponse<?> handleCorpAlreadyExistException(CorpAlreadyExistException e) {

        return ApiResponse.builder()
                .result(ApiResponse.ApiResult.builder()
                        .code(e.getCode())
                        .desc(e.getMessage())
                        .build())
                .build();
    }

}
