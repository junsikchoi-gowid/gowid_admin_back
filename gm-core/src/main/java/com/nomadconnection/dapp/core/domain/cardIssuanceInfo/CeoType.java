package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CeoType {
    SINGLE("단일대표", "1", "1"),
    EACH("각자대표", "2", "3"),
    PUBLIC("공동대표", "3", "2"),
    ;

    private String description;
    private String shinhanCode;
    private String lotteCode;

    public static CeoType fromShinhan(String shinhanCode) {
        if ("3".equals(shinhanCode)) {
            return PUBLIC;
        } else if ("2".equals(shinhanCode)) {
            return EACH;
        } else if ("1".equals(shinhanCode)) {
            return SINGLE;
        } else {
            return null;
        }
    }

    public static CeoType fromLotte(String lotteCode) {
        if ("3".equals(lotteCode)) {
            return EACH;
        } else if ("2".equals(lotteCode)) {
            return PUBLIC;
        } else if ("1".equals(lotteCode)) {
            return SINGLE;
        } else {
            return null;
        }
    }

    public static String convertShinhanToLotte(String shinhanCode) {
        String lotteCode = "";

        switch (shinhanCode) {
            case "1":
                lotteCode = "1";
                break;
            case "2":
                lotteCode = "3";
                break;
            case "3":
                lotteCode = "2";
                break;
        }

        return lotteCode;
    }
}
