package com.nomadconnection.dapp.core.domain.repository.querydsl;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface CorpCustomRepository {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class SearchCorpDto {
        @ApiModelProperty("법인명")
        public String resCompanyNm;

        @ApiModelProperty("사업자등록번호")
        private String resCompanyIdentityNo;

        @ApiModelProperty("벤처인증 true/false")
        private String ventureCertification;

        @ApiModelProperty("투자유치 true/false")
        private String vcInvestment;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class SearchCorpResultDto {
        @ApiModelProperty("법인명 ")
        public String resCompanyNm;

        @ApiModelProperty("사업자등록번호")
        public String resConpanyIdentityNo;

        @ApiModelProperty("대표자")
        public String resUserNm;

        @ApiModelProperty("업태")
        public String resBusinessItems;

        @ApiModelProperty("종목")
        public String resBusinessTypes;

        @ApiModelProperty("createdAt")
        public LocalDateTime createdAt;

        @ApiModelProperty("대표이사 연대보증 여부")
        public Boolean ceoGuarantee;

        @ApiModelProperty("보증금")
        public double depositGuarantee;

        @ApiModelProperty("카드발급여부")
        public Boolean cardIssuance;

        @ApiModelProperty("벤처인증")
        public Boolean ventureCertification;

        @ApiModelProperty("투자유치")
        public Boolean vcInvestment;
    }

    Page<SearchCorpResultDto> corpList(SearchCorpDto dto, Long idxUser, Pageable pageable);


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

        @ApiModelProperty("진행계좌")
        public String processAccountCnt;

        @ApiModelProperty("총계좌개수")
        public String allAccountCnt;

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
}
