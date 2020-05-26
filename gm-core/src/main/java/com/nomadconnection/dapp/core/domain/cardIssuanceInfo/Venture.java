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
public class Venture {

    private Boolean isVerifiedVenture; // 벤처기업확인서 보유 여부
    private Boolean isVC; // 10억이상 VC투자 여부
    // private Long investor; todo: 벤처기업 테이블 생성시 추가 // 투자사
    private String investAmount; // 투자금액
}
