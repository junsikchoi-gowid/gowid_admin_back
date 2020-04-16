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
        public Long idxCorp;

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

        @ApiModelProperty("pause")
        public Boolean pause;
    }

    Page<SearchRiskResultDto> riskList(SearchRiskDto risk, Long idxUser, Pageable pageable);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class CashResultDto {
        @ApiModelProperty("법인ID")
        public String idxCorp;

        @ApiModelProperty("법인명 ")
        public String resCompanyNm;

        @ApiModelProperty("입금 ")
        public Double resAccountIn;

        @ApiModelProperty("출금 ")
        public Double resAccountOut;

        @ApiModelProperty("순입출 ")
        public Double resAccountInOut;

        @ApiModelProperty("전일잔고 ")
        public Double befoBalance;

        @ApiModelProperty("Burn Rate ")
        public String BurnRate;

        @ApiModelProperty("RunWay ")
        public String RunWay;

        @ApiModelProperty("createdAt ")
        public LocalDateTime createdAt;

        @ApiModelProperty("errorCode ")
        public String errorCode;
    }

    Page<CashResultDto> cashList( String searchCorpName, String updateStatus, Long idxUser, Pageable pageable);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ScrapingResultDto {
        @ApiModelProperty("법인ID")
        public Long idxCorp;

        @ApiModelProperty("법인명 ")
        public String idxCorpName;

        @ApiModelProperty("성공계좌")
        public String successAccountCnt;

        @ApiModelProperty("총계좌개수")
        public String allAccountCnt;

        @ApiModelProperty("성공률")
        public Double successPercent;

        @ApiModelProperty("createdAt")
        public LocalDateTime createdAt;

        @ApiModelProperty("updatedAt")
        public LocalDateTime updatedAt;

        @ApiModelProperty("endFlag")
        public boolean endFlag;

        @ApiModelProperty("user")
        public Long idxUser;
    }

    Page<ScrapingResultDto> scrapingList(Pageable pageable);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ErrorSearchDto {
        @ApiModelProperty("법인명 ")
        public String corpName;

        @ApiModelProperty("에러메세지")
        private String errorMessage;

        @ApiModelProperty("에러코드 true/false")
        private String errorCode;

        @ApiModelProperty("금일여부 true/false")
        private String boolToday;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ErrorResultDto {
        @ApiModelProperty("법인ID")
        public Long idxCorp;

        @ApiModelProperty("법인명 ")
        public String idxCorpName;
    }

    Page<ErrorResultDto> errorList(ErrorResultDto risk, Long idxUser, Pageable pageable);


}
