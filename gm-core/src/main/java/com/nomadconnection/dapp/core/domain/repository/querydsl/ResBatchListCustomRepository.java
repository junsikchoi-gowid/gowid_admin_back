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
    class ScrapAccountDto {
        @ApiModelProperty("법인 idx")
        public Long idxCorp;

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

        @ApiModelProperty("잔고")
        private Double resAccountBalance;

        @ApiModelProperty("예적금 구분")
        private String resAccountDeposit;

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
