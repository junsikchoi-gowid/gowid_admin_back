package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.card.CardCompany;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ResBatchListCustomRepository {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ErrorSearchDto {
        @ApiModelProperty("법인 idx")
        public Long idxCorp;

        @ApiModelProperty("법인명")
        private String corpName;

        @ApiModelProperty("transactionId")
        private String transactionId;

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
        @ApiModelProperty("법인 idx")
        public Long idxCorp;

        @ApiModelProperty("발생시간")
        private LocalDateTime updatedAt;

        @ApiModelProperty("법인명")
        private String corpName;

        @ApiModelProperty("은행")
        private String bankName;

        @ApiModelProperty("계좌번호")
        private String account;

        @ApiModelProperty("에러메세지")
        private String errMessage;

        @ApiModelProperty("에러코드")
        private String errCode;

        @ApiModelProperty("transactionId")
        private String transactionId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class batchListDto {
        @ApiModelProperty("법인 idx")
        private Long idxCorp;

        @ApiModelProperty("법인명")
        private String corpName;

        @ApiModelProperty("타입")
        private String cardType;

        @ApiModelProperty("카드사")
        private CardCompany cardCompany;

        @ApiModelProperty("등급")
        private String grade;

        @ApiModelProperty("업데이트")
        private String updateDate;
    }

    Page<ErrorResultDto> errorList(ErrorSearchDto dto, Pageable pageable);



    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ScrapAccountDto {
        @ApiModelProperty("법인 idx")
        public String idxCorp;

        @ApiModelProperty("법인명")
        public String corpName;

        @ApiModelProperty("은행")
        public String bankName;

        @ApiModelProperty("계좌종류")
        public String accountType;

        @ApiModelProperty("스크래핑결과")
        public String errorYn;

        @ApiModelProperty("업데이트일자")
        private String updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ScrapAccountListDto {
        @ApiModelProperty("법인ID")
        private Long idxCorp;

        @ApiModelProperty("법인명")
        private String idxCorpName;

        @ApiModelProperty("은행코드")
        private String bankCode;

        @ApiModelProperty("은행명")
        private String bankName;

        @ApiModelProperty("계좌 종류")
        private String accountType;

        @ApiModelProperty("계좌 종류")
        private String accountTypeName;

        @ApiModelProperty("계좌번호")
        private String resAccount;

        @ApiModelProperty("계좌번호 View")
        private String resAccountDisplay;

        @ApiModelProperty("스크래핑 결과")
        private String errorMessage;

        @ApiModelProperty("transaction ID")
        private String transactionId;

        @ApiModelProperty("updatedAt")
        private LocalDateTime updatedAt;
    }

    Page<ScrapAccountListDto> scrapAccountList(ScrapAccountDto scrapAccountDto, Pageable pageable);
}
