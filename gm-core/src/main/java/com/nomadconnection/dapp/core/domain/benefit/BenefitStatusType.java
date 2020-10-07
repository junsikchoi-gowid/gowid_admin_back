package com.nomadconnection.dapp.core.domain.benefit;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BenefitStatusType {

    // TODO: 베네핏 결제에 따른 결과는 여러가지가 있을 수 있음.
    // 결제 성공, 결제 실패, 주문 성공, 주문 실패, etc...

    SUCCESS("SUCCESS"),    // 성공
    FAILED("FAILED");     // 실패

    private String value;
}
