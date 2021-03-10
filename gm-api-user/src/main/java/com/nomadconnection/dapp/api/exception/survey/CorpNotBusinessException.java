package com.nomadconnection.dapp.api.exception.survey;

import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.Getter;

@Getter
public class CorpNotBusinessException extends BadRequestException {
    private String code;
    private String message;

    public CorpNotBusinessException(ErrorCode.Api errorCodeType) {
        super(errorCodeType);
        code = errorCodeType.getCode();
        message = errorCodeType.getDesc();
    }
}
