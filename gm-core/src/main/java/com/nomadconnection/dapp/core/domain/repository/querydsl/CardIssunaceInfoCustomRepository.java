package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceDepth;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public interface CardIssunaceInfoCustomRepository {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class CardIssuanceInfoDto {
        @ApiModelProperty(value = "카드사", example = "SHINHAN")
        public CardCompany cardCompany;

        @ApiModelProperty(value = "마지막 신청 단계", example = "SIGN_SIGNATURE")
        private IssuanceDepth issuanceDepth;

        @ApiModelProperty(value = "신청 상태", example = "ISSUED")
        public IssuanceStatus issuanceStatus;

        @ApiModelProperty(value = "희망한도", example = "10000000")
        private String hopeLimit;

        @ApiModelProperty(value = "실제 카드한도", example = "10000000")
        private String grantLimit;

        @ApiModelProperty(value = "카드 신청 매수", example = "1")
        private Long requestCount;

        @ApiModelProperty(value = "롯데 비교통그린카드", example = "1")
        private Long lotteGreenCount;

        @ApiModelProperty(value = "롯데 비교통블랙카드", example = "1")
        private Long lotteBlackCount;

        @ApiModelProperty(value = "롯데 교통그린카드", example = "1")
        private Long lotteGreenTrafficCount;

        @ApiModelProperty(value = "롯데 교통블랙카드", example = "1")
        private Long lotteBlackTrafficCount;

        @ApiModelProperty(value = "롯데 하이패스", example = "1")
        private Long lotteHiPassCount;

        @ApiModelProperty(value = "신청완료일", example = "9999-99-99 99:99:99")
        private LocalDateTime applyDate;

        @ApiModelProperty(value = "심사완료일", example = "9999-99-99 99:99:99")
        private LocalDateTime decisionDate;
    }

    CardIssuanceInfoDto issuanceInfo(Long idxCardIssuanceInfo);
}
