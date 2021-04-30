package com.nomadconnection.dapp.core.exception.handler;

import com.nomadconnection.dapp.core.exception.*;
import com.nomadconnection.dapp.core.exception.response.GowidResponse;
import com.nomadconnection.dapp.core.exception.result.ResultType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GowidExceptionHandler extends RuntimeException {

    @ExceptionHandler(InternalServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected GowidResponse internalServerException(InternalServerException e) {

        log.error(e.getMessage(), e);
        return new GowidResponse(new GowidResponse.ApiResult(e) );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected GowidResponse accessDeniedException(AccessDeniedException e) {

        log.error(e.getMessage(), e);
        return com.nomadconnection.dapp.core.exception.response.GowidResponse.builder()
                .result(new GowidResponse.ApiResult(ResultType.AUTHENTICATION_FAILURE, e.getMessage()))
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected GowidResponse handleSystemException(Exception e) {

        log.error(e.getMessage(), e);
        return com.nomadconnection.dapp.core.exception.response.GowidResponse.builder()
                .result(new GowidResponse.ApiResult(ResultType.SYSTEM_ERROR, e.getMessage()))
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public GowidResponse methodValidException(MethodArgumentNotValidException e) {

        log.error(e.getMessage(), e);
        return new GowidResponse(
                new GowidResponse.ApiResult(
                        ResultType.INVALID_DATA,
                        Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage()
                )
        );
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected GowidResponse handleBadRequestException(BadRequestException e) {

        log.error(e.getMessage(), e);
        return new GowidResponse(e);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected GowidResponse handleBadRequestException(ConflictException e) {

        log.error(e.getMessage(), e);
        return new GowidResponse(e);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected GowidResponse handleBadRequestException(NotFoundException e) {

        log.error(e.getMessage(), e);
        return new GowidResponse(e);
    }

    @ExceptionHandler(UnAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected GowidResponse handleUnAuthorizedException(UnAuthorizedException e) {

        log.error(e.getMessage(), e);
        return new GowidResponse(e);
    }

    @ExceptionHandler(DuplicatedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected GowidResponse handleBadRequestException(DuplicatedException e) {

        log.error(e.getMessage(), e);
        return new GowidResponse(e);
    }

}
