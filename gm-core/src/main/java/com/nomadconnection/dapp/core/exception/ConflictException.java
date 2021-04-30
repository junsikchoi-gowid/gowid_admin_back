package com.nomadconnection.dapp.core.exception;

import com.nomadconnection.dapp.core.exception.result.ResultType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 409 Conflict(충돌) : 사용자의 요청의 서버의 상태와 충돌. (데이터 중복이나 정합성 문제 등)
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
public class ConflictException extends BaseException {
    public ConflictException(ResultType resultType) {
        super(resultType);
    }

    public ConflictException(ResultType resultType, String extraMessage) {
        super(resultType, extraMessage);
    }

    public ConflictException(String extraMessage) {
        super(ResultType.NOT_FOUND, extraMessage);
    }

}
