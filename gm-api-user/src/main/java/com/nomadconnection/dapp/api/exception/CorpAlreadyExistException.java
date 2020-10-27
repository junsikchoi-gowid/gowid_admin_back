package com.nomadconnection.dapp.api.exception;

import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class CorpAlreadyExistException extends RuntimeException{

    private final String code;
    private final String message;

    public CorpAlreadyExistException(ErrorCode.Api errorCodeType) {
        code = errorCodeType.getCode();
        message = errorCodeType.getDesc();
    }
}
