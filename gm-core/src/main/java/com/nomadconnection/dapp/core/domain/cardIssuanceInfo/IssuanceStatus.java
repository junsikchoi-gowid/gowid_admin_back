package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IssuanceStatus {
    UNISSUED("미발급"),
    INPROGRESS("신청중"),
    REJECT("발급거절"),
    COMPLETED("신청완료"),
    ISSUED("발급완료")
    ;

    private String status;

}
