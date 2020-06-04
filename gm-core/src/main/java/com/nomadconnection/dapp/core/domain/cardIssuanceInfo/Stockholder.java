package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Embeddable;

@Data
@Accessors(fluent = true)
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Stockholder {

    private Boolean isStockHold25; // 25%이상의 지분을 보유한 개인여부
    private Boolean isStockholderPersonal; // 1대주주 개인여부
    private Boolean isStockholderList; // 1대주주 법인의 주주명부 보유여부
    private String stockholderName; // 주주이름(한글)
    private String stockholderEngName; // 주주이름(영문)
    private String stockholderBirth; // 생년월일 6자리
    private String stockholderNation; // 국적
    private String stockRate; // 지분율
}
