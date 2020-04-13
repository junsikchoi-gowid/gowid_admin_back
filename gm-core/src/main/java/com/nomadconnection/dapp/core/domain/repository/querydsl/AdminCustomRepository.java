package com.nomadconnection.dapp.core.domain.repository.querydsl;

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
        @ApiModelProperty("법인명 ")
        public String idxCorpName;

        @ApiModelProperty("변경 잔고 ")
        public double cardLimitNow;

        @ApiModelProperty("부여 한도")
        public double cardLimit;

        @ApiModelProperty("법인 등급")
        public String grade;

        @ApiModelProperty("최신잔고")
        public double balance;

        @ApiModelProperty("현재잔고")
        public double currentBalance;

        @ApiModelProperty("cardRestartCount")
        public Integer cardRestartCount;

        @ApiModelProperty("긴급중지")
        public Boolean emergencyStop;

        @ApiModelProperty("카드발급여부")
        public Boolean cardIssuance;

        @ApiModelProperty("updatedAt")
        public LocalDateTime updatedAt;

        @ApiModelProperty("errCode")
        public String errCode;
    }

    Page<SearchRiskResultDto> riskList(SearchRiskDto risk, Long idxUser, Pageable pageable);
}
