package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReceiveType {
    POST("211", "1"),
    EMAIL("233", "2"),
    ALL("234", "3"),
    ;

    private String shinhanCode;
    private String lotteCode;
}
