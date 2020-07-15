package com.nomadconnection.dapp.core.domain.shinhan;

import lombok.Getter;

@Getter
public enum IssuanceStatusType {
    DEFAULT,    // 초기 상태(실행전)
    SUCCESS,
    FAILED;

    private String code;
}
