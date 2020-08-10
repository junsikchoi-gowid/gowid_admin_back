package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CeoType {
    SINGLE("단일대표", "1", "1"),
    EACH("각기대표", "2", "3"),
    PUBLIC("공동대표", "3", "2"),
    ;

    private String description;
    private String shinhanCode;
    private String lotteCode;

    public static CeoType fromShinhan(String shinhanCode) {
        if (shinhanCode.equals("3")) {
            return PUBLIC;
        } else if (shinhanCode.equals("2")) {
            return EACH;
        } else if (shinhanCode.equals("1")) {
            return SINGLE;
        } else {
            return null;
        }
    }

    public static CeoType fromLotte(String lotteCode) {
        if (lotteCode.equals("3")) {
            return EACH;
        } else if (lotteCode.equals("2")) {
            return PUBLIC;
        } else if (lotteCode.equals("1")) {
            return SINGLE;
        } else {
            return null;
        }
    }
}
