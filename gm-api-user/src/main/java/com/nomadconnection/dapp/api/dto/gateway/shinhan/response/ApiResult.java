package com.nomadconnection.dapp.api.dto.gateway.shinhan.response;

import lombok.ToString;
import org.springframework.http.HttpStatus;

@ToString
public class ApiResult {

    private final int code;

    private final String desc;

    ApiResult(Throwable throwable, HttpStatus status) {
        this(throwable.getMessage(), status);
    }

    ApiResult(String desc, HttpStatus status) {
        this.code = status.value();
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
