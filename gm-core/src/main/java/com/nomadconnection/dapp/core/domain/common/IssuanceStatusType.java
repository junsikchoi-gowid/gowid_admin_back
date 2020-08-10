package com.nomadconnection.dapp.core.domain.common;

import lombok.Getter;

@Getter
public enum IssuanceStatusType {
    SUCCESS,
    FAILED;

    private String code;
}
