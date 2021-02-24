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
        @ApiModelProperty("카드사")
        public CardCompany cardCompany;

        @ApiModelProperty("마지막 신청 단계")
        private IssuanceDepth issuanceDepth;

        @ApiModelProperty("신청 상태")
        public IssuanceStatus issuanceStatus;

        @ApiModelProperty("희망한도")
        private String hopeLimit;

        @ApiModelProperty("실제 카드한도")
        private String grantLimit;

        @ApiModelProperty("카드 신청 매수")
        private Long requestCount;

        @ApiModelProperty("롯데 비교통그린카드")
        private Long lotteGreenCount;

        @ApiModelProperty("롯데 비교통블랙카드")
        private Long lotteBlackCount;

        @ApiModelProperty("롯데 교통그린카드")
        private Long lotteGreenTrafficCount;

        @ApiModelProperty("롯데 교통블랙카드")
        private Long lotteBlackTrafficCount;

        @ApiModelProperty("롯데 하이패스")
        private Long lotteHiPassCount;

        @ApiModelProperty("신청완료일")
        private LocalDateTime applyDate;

        @ApiModelProperty("심사완료일")
        private LocalDateTime decisionDate;
    }

    CardIssuanceInfoDto issuanceInfo(Long idxCardIssuanceInfo);
}
