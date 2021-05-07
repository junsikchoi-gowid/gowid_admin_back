package com.nomadconnection.dapp.core.domain.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResConCorpListStatus {
    NORMAL("정상"),
    ERROR("오류"),
    DELETE("삭제"),
    STOP("중지");

    private String status;
}