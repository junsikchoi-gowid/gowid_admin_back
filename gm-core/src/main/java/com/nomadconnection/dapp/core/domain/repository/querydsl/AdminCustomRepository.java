package com.nomadconnection.dapp.core.domain.repository.querydsl;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface AdminCustomRepository {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class SearchRiskDto {
        @ApiModelProperty("법인 idx")
        public String idxCorp;

        @ApiModelProperty("법인명 order : user.corp.resCompanyNm")
        public String idxCorpName;

        @ApiModelProperty("법인 등급")
        private String grade;

        @ApiModelProperty("긴급중지")
        private String emergencyStop;

        @ApiModelProperty("카드발급여부")
        private String cardIssuance;

        @ApiModelProperty("updatedStatus")
        private String updatedStatus;

        @ApiModelProperty("일시정지")
        private String pause;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class SearchRiskResultDto {
        @ApiModelProperty("법인ID")
        private Long idxCorp;

        @ApiModelProperty("법인명 ")
        private String idxCorpName;

        @ApiModelProperty("변경 잔고 ")
        private double cardLimitNow;

        @ApiModelProperty("부여 한도")
        private double cardLimit;

        @ApiModelProperty("법인 등급")
        private String grade;

        @ApiModelProperty("최신잔고")
        private double balance;

        @ApiModelProperty("현재잔고")
        private double currentBalance;

        @ApiModelProperty("cardRestartCount")
        private Integer cardRestartCount;

        @ApiModelProperty("긴급중지")
        private Boolean emergencyStop;

        @ApiModelProperty("카드발급여부")
        private Boolean cardIssuance;

        @ApiModelProperty("updatedAt")
        private LocalDateTime updatedAt;

        @ApiModelProperty("errCode")
        private String errCode;

        @ApiModelProperty("pause")
        private Boolean pause;
    }

    Page<SearchRiskResultDto> riskList(SearchRiskDto risk, Long idxUser, Pageable pageable);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class CashResultDto {
        @ApiModelProperty("법인ID")
        private String idxCorp;

        @ApiModelProperty("법인명 ")
        private String resCompanyNm;

        @ApiModelProperty("입금 ")
        private Double resAccountIn;

        @ApiModelProperty("출금 ")
        private Double resAccountOut;

        @ApiModelProperty("순입출 ")
        private Double resAccountInOut;

        @ApiModelProperty("전일잔고 ")
        private Double befoBalance;

        @ApiModelProperty("Burn Rate ")
        private String BurnRate;

        @ApiModelProperty("RunWay ")
        private String RunWay;

        @ApiModelProperty("createdAt ")
        private LocalDateTime createdAt;

        @ApiModelProperty("errorCode ")
        private String errorCode;

        @ApiModelProperty("errStatus ")
        private String errStatus;
    }
}
