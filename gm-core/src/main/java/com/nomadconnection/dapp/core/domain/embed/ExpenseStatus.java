package com.nomadconnection.dapp.core.domain.embed;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExpenseStatus {
    SETUP_COMPANY("미사용"),
    SETUP_MEMBER("튜토리얼STEP1 완료"),
    SETUP_CARD("튜토리얼STEP2 완료"),
    SETUP_REPORT_POLICY("튜토리얼STEP3 완료"),
    SETUP_PURPOSE("튜토리얼STEP4 완료"),
    SETUP_DONE("사용중")
    ;

    private String status;
}
