package com.nomadconnection.dapp.core.exception.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 결과값 enum
 * https://docs.google.com/spreadsheets/d/1tluQlIS4U8VQX2cx2SNVSUjNvtsgJuhp8RHnjZ18qpM/edit#gid=0
 */
@Getter
@AllArgsConstructor
public enum ResultType {

    SUCCESS("0000", "success"),
    PARTIAL_SUCCESS("0001", "partial success"),

    MISSING_REQUIRED_VALUE("1000", "missing required value"),
    INVALID_DATA("1001", "invalid data"),
    NOT_FOUND("1002", "not found"),
    DUPLICATED_REQUEST("1003", "duplicated request"),
    DUPLICATED_DATA("1004", "duplicated data"),

    AUTHENTICATION_FAILURE("2001", "authentication failure"),
    NO_PERMISSION("2002", "no permission"),
    EXPIRED("2003", "expired"),

    CODEF_ERROR("3100", "codef error"),
    SHINHAN_CARD_ERROR("3200", "shinhan card error"),
    LOTTE_CARD_ERROR("3300", "lotte card error"),

    INTERNAL_RPC_ERROR("5000", "internal rpc error"),

    SYSTEM_ERROR("6000", "system error");

    private final String code;
    private final String Desc;

}
