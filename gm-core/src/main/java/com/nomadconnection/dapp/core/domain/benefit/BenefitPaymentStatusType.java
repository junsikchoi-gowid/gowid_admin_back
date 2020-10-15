package com.nomadconnection.dapp.core.domain.benefit;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Benefit 결제 상태 Enum
 */
@Getter
@AllArgsConstructor
public enum BenefitPaymentStatusType {

    SUCCESS("SUCCESS"),     // 성공
    FAILED("FAILED"),       // 실패
    CANCELD("CANCELD")      // 결제 취소
    ;

    private String value;
}
