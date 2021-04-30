package com.nomadconnection.dapp.core.exception;

import com.nomadconnection.dapp.core.exception.result.ResultType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaseException extends RuntimeException {

    public String code ;
    public String desc ;
    public String extraMessage ;

    public BaseException(ResultType resultType) {
        this.code = resultType.getCode();
        this.desc = resultType.getDesc();
    }

    public BaseException(ResultType resultType, String extraMessage) {
        this.code = resultType.getCode();
        this.desc = resultType.getDesc();
        this.extraMessage = extraMessage;
    }
}
