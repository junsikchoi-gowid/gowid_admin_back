package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReceiveType {
    POST("211"),
    EMAIL("233"),
    ALL("234"),
    ;

    private String code;
}
