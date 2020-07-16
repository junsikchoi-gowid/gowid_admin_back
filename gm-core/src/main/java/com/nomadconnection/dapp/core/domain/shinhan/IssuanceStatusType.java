package com.nomadconnection.dapp.core.domain.shinhan;

import lombok.Getter;

@Getter
public enum IssuanceStatusType {
    SUCCESS,
    FAILED;

    private String code;
}
