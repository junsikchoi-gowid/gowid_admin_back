package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CeoType {
    SINGLE("단일대표", "1"),
    EACH("각기대표", "2"),
    PUBLIC("공동대표", "3"),
    ;

    private String description;
    private String code;
}
