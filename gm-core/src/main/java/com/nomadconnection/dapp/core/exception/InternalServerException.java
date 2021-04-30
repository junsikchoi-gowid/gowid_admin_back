package com.nomadconnection.dapp.core.exception;

import com.nomadconnection.dapp.core.exception.result.ResultType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 500 Internal Server Error(내부 서버 에러)
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
public class InternalServerException extends BaseException {

    private static final long serialVersionUID = 2074542044459042219L;

    public InternalServerException(ResultType resultType) {
        super(resultType);
    }

    public InternalServerException(ResultType resultType, String extMsg) {
        super(resultType, extMsg);
    }

}
