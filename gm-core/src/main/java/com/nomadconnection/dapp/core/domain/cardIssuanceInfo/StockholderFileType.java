package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StockholderFileType {
    BASIC("주주명부"),
    MAJOR("1대 주주명부"),
    ;

    private String description;
}
