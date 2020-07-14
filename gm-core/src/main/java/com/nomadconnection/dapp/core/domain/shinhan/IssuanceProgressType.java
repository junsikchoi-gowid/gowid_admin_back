package com.nomadconnection.dapp.core.domain.shinhan;

import lombok.Getter;

@Getter
public enum IssuanceProgressType {
    NOT_SIGNED,     // 전자서명전
    SIGNED,         // 서명완료
    RESUME;         // 재개

    private String code;
}
