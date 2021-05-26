package com.nomadconnection.dapp.core.domain.saas;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * SaaS 결제 유형
 */
@Getter
@AllArgsConstructor
public enum SaasPaymentType {
    UNCATEGORIZED(0),   // 0: 미분류
    MONTHLY(1),         // 1: 월결제
    YEARLY(2),          // 2: 연결제
    FREE_TRIAL(3),      // 3: 무료
    IRREGULAR(4),       // 4: 비정기
    QUARTER(5),         // 5: 분기결제
    ONE_TIME(6),        // 6: 일회성
    ;

    private int code;
    public static SaasPaymentType getType(int code) {
        return Arrays.stream(SaasPaymentType.values()).filter(saasPaymentType -> saasPaymentType.code == code)
            .findFirst().orElse(null);
    }
}