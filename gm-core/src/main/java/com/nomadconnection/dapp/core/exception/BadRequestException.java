package com.nomadconnection.dapp.core.exception;

import com.nomadconnection.dapp.core.exception.result.ResultType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 400 Bad Request(잘못된 요청)
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
public class BadRequestException extends BaseException {

    public BadRequestException(ResultType resultType) {
        super(resultType);
    }

    public BadRequestException(ResultType resultType, String extMsg) {
        super(resultType, extMsg);
    }

}
