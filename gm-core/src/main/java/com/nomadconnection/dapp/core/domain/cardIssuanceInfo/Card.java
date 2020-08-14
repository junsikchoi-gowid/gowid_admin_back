package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Accessors(fluent = true)
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    private String hopeLimit; // 희망한도
    private String calculatedLimit; // 계산한도
    private String grantLimit; // 부여한도
    private Long requestCount; // 신청수량
    private String addressBasic; // 기본주소
    private String addressDetail; // 상세주소
    private String zipCode; // 우편번호
    private String addressKey; // 도로명 key값

    private Long lotteGreenCount; // 롯데 그린카드
    private Long lotteBlackCount; // 롯데 블랙카드

    @Enumerated(EnumType.STRING)
    private ReceiveType receiveType; // 명세서 수령방법

    @Builder.Default
    private String cardName = "고위드 스타트업 카드"; // 카드명

    @Builder.Default
    private Boolean isUnsigned = true; // 무기명여부

    @Builder.Default
    private Boolean isOverseas = true; // 해외결제 가능여부

    @Builder.Default
    private Integer paymentDay = 15;
}
