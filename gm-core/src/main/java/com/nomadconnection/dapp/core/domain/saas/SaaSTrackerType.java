package com.nomadconnection.dapp.core.domain.saas;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Benefit 결제 상태 Enum
 */
@Getter
@AllArgsConstructor
public enum SaaSTrackerType {

    STATUS_REQUEST(0),
    STATUS_REQUEST_COMPLETE(1),
    STATUS_ALL_READY(2),

    STEP_INIT(0),
    STEP_HOMTAX_REG(1),
    STEP_ACCOUNT_REG(2),
    STEP_CARD_REG(3),
    STEP_ALL_COMPLETE(4),
    ;

    private Integer value;
}
