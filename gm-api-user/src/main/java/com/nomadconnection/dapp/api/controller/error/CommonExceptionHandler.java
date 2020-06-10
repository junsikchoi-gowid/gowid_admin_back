package com.nomadconnection.dapp.api.controller.error;

import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.dto.response.ErrorResponse;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ControllerAdvice
@ResponseBody
public class CommonExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse onUsernameNotFoundException(UsernameNotFoundException e) {
        return ErrorResponse.from(ErrorCode.Regular.USERNAME_NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Stream<FieldError> ps = e.getBindingResult().getFieldErrors().parallelStream();
        Stream<ErrorResponse.FieldError> stream = ps.map(
                error -> ErrorResponse.FieldError.builder()
                        .field(error.getField())
                        .value((String) error.getRejectedValue())
                        .reason(error.getDefaultMessage())
                        .build()
        );
        return ErrorResponse.from(ErrorCode.Regular.METHOD_ARGUMENT_NOT_VALID, stream.collect(Collectors.toList()));
    }

    @ExceptionHandler(PropertyReferenceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse onPropertyReferenceException(PropertyReferenceException e) {
        return ErrorResponse.from(
                ErrorCode.Regular.PROPERTY_REFERENCE_ERROR,
                Collections.singletonList(
                        ErrorResponse.FieldError.builder()
                                .field("propertyName")
                                .value(e.getPropertyName())
                                .reason(e.getMessage())
                                .build()
                )
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse onConstraintViolationException(ConstraintViolationException e) {
        return ErrorResponse.from(ErrorCode.Regular.CONSTRAINT_VIOLATION);
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ErrorResponse onIOException(IOException e) {
        return ErrorResponse.from(ErrorCode.Regular.IO_EXCEPTION, e.getMessage());
    }
}
