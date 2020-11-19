package com.nomadconnection.dapp.api.exception;

import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.Getter;

@Getter
public class CorpAlreadyExistException extends AlreadyExistException{

    private String code;
    private String message;

    public CorpAlreadyExistException(ErrorCode.Api errorCodeType) {
        super(errorCodeType);
        code = errorCodeType.getCode();
        message = errorCodeType.getDesc();
    }
}
