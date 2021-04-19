package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface AdminCustomRepository {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class SearchRiskResultDto {
        @ApiModelProperty("법인ID")
        private Long idxCorp;

        @ApiModelProperty("법인명")
        private String idxCorpName;

        @ApiModelProperty("변경 잔고")
        private Double cardLimitNow;

        @ApiModelProperty("승인 한도")
        private Double confirmedLimit;

        @ApiModelProperty("부여 한도")
        private Double cardLimit;

        @ApiModelProperty("법인 등급")
        private String grade;

        @ApiModelProperty("최신잔고")
        private Double balance;

        @ApiModelProperty("기준잔고")
        private Double cashBalance;

        @ApiModelProperty("현재잔고")
        private Double currentBalance;

        @ApiModelProperty("cardRestartCount")
        private Integer cardRestartCount;

        @ApiModelProperty("긴급중지")
        private Boolean emergencyStop;

        @ApiModelProperty("카드발급여부")
        private IssuanceStatus cardIssuance;

        @ApiModelProperty("카드발급여부")
        private Boolean cardAvailable;

        @ApiModelProperty("updatedAt")
        private LocalDateTime updatedAt;

        @ApiModelProperty("errCode")
        private String errCode;

        @ApiModelProperty("pause")
        private Boolean pause;

        @ApiModelProperty("cardCompany")
        private CardCompany cardCompany;

        @ApiModelProperty("전략seg")
        private String cardType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class RiskOriginal {
        @ApiModelProperty("법인명")
        private String resCompanyNm;

        @ApiModelProperty("법인 idx")
        public String idxCorp;

        @ApiModelProperty("기준일")
        private String baseDate;

        @ApiModelProperty("타입")
        private String cardType;

        @ApiModelProperty("카드사")
        private CardCompany cardCompany;

        @ApiModelProperty("등급")
        private String grade;

        @ApiModelProperty("승인여부")
        private IssuanceStatus cardIssuance;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class SearchRiskResultV2Dto {
        @ApiModelProperty("법인ID")
        private Long idxCorp;

        @ApiModelProperty("법인명")
        private String idxCorpName;

        @ApiModelProperty("변경 잔고")
        private Double cardLimitNow;

        @ApiModelProperty("승인 한도")
        private Double confirmedLimit;

        @ApiModelProperty("부여 한도")
        private Double cardLimit;

        @ApiModelProperty("법인 등급")
        private String grade;

        @ApiModelProperty("최신잔고")
        private Double balance;

        @ApiModelProperty("기준잔고")
        private Double cashBalance;

        @ApiModelProperty("현재잔고")
        private Double currentBalance;

        @ApiModelProperty("유지일")
        private Integer cardRestartCount;

        @ApiModelProperty("긴급중지")
        private Boolean emergencyStop;

        @ApiModelProperty("카드발급여부")
        private IssuanceStatus cardIssuance;

        @ApiModelProperty("카드유효여부")
        private Boolean cardAvailable;

        @ApiModelProperty("updatedAt")
        private LocalDateTime updatedAt;

        @ApiModelProperty("errCode")
        private String errCode;

        @ApiModelProperty("pause")
        private Boolean pause;

        @ApiModelProperty("cardCompany")
        private CardCompany cardCompany;

        @ApiModelProperty("전략seg")
        private String cardType;

        @ApiModelProperty("45일 평균")
        private Double dma45;

        @ApiModelProperty("45일 중간값")
        private Double dmm45;

        @ApiModelProperty("전송가능여부")
        private Boolean transFlag;

        private String hopeLimit;

        private Double realtimeLimit;

        private String baseDate;
    }

    Page<SearchRiskResultV2Dto> riskList(RiskOriginal risk, Long idxUser, Pageable pageable);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class RiskTransDto {
        @ApiModelProperty("법인ID")
        private Long idxCorp;

        @ApiModelProperty("법인명")
        private String idxCorpName;

        @ApiModelProperty("변경 잔고")
        private Double cardLimitNow;

        @ApiModelProperty("승인 한도")
        private Double confirmedLimit;

        @ApiModelProperty("부여 한도")
        private Double cardLimit;

        @ApiModelProperty("법인 등급")
        private String grade;

        @ApiModelProperty("최신잔고")
        private Double balance;

        @ApiModelProperty("기준잔고")
        private Double cashBalance;

        @ApiModelProperty("현재잔고")
        private Double currentBalance;

        @ApiModelProperty("cardRestartCount")
        private Integer cardRestartCount;

        @ApiModelProperty("긴급중지")
        private Boolean emergencyStop;

        @ApiModelProperty("카드발급여부")
        private IssuanceStatus cardIssuance;

        @ApiModelProperty("카드발급여부")
        private Boolean cardAvailable;

        @ApiModelProperty("updatedAt")
        private LocalDateTime updatedAt;

        @ApiModelProperty("errCode")
        private String errCode;

        @ApiModelProperty("pause")
        private Boolean pause;

        @ApiModelProperty("cardCompany")
        private String cardCompany;
    }
}
