package com.nomadconnection.dapp.api.exception.api;

import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnauthorizedException extends RuntimeException {
    private String code;
    private String desc;

    public UnauthorizedException(ErrorCode.Api errorCodeType) {
        code = errorCodeType.getCode();
        desc = errorCodeType.getDesc();
    }

    public UnauthorizedException(ErrorCode.Api errorCodeType, String addString) {
        code = errorCodeType.getCode();
        desc = errorCodeType.getDesc() + " - " + addString;
    }

}
