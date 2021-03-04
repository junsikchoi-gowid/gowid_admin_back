package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceDepth;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
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
        @ApiModelProperty("idx ")
        public Long idx;

        @ApiModelProperty("법인명 ")
        public String resCompanyNm;

        @ApiModelProperty("사업자등록번호")
        public String resCompanyIdentityNo;

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

        @ApiModelProperty("에러건수")
        public Boolean boolError;

        @ApiModelProperty("에러건수")
        public Boolean boolPauseStop;

        @ApiModelProperty("카드사")
        public CardCompany cardCompany;

        @ApiModelProperty("마지막 신청 단계")
        private IssuanceDepth issuanceDepth;

        @ApiModelProperty("담당자")
        private String userName;

        @ApiModelProperty("이메일주소")
        private String email;

        @ApiModelProperty("법인등록일")
        private String registerDate;

        @ApiModelProperty("신청완료일")
        private LocalDateTime applyDate;

        @ApiModelProperty("심사완료일")
        private LocalDateTime decisionDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class SearchCorpListDto {

        @ApiModelProperty("IDX")
        private Long idx;

        @ApiModelProperty("법인명")
        private String resCompanyNm;

        @ApiModelProperty("사업자등록번호")
        private String resCompanyIdentityNo;

        @ApiModelProperty("대표")
        private String ceoName;

        @ApiModelProperty("카드")
        private CardCompany cardCompany;

        @ApiModelProperty("발급")
        private Boolean cardIssuance;

        @ApiModelProperty("발급상태")
        private IssuanceStatus issuanceStatus;

        @ApiModelProperty("마지막 신청 단계")
        private IssuanceDepth issuanceDepth;

        @ApiModelProperty("담당자")
        private String userName;

        @ApiModelProperty("이메일주소")
        private String email;

        @ApiModelProperty("법인등록일")
        private String registerDate;

        @ApiModelProperty("신청완료일")
        private LocalDateTime applyDate;

        @ApiModelProperty("심사완료일")
        private LocalDateTime decisionDate;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class CorpListDto {

        @ApiModelProperty(value = "idxUser", example = "1")
        public Long idxUser;

        @ApiModelProperty(value = "idxCorp", example = "1")
        public Long idxCorp;

        @ApiModelProperty(value = "idxCardIssuanceInfo", example = "1")
        public Long idxCardIssuanceInfo;

        @ApiModelProperty(value = "법인명", example = "고위드")
        private String resCompanyNm;

        @ApiModelProperty(value = "사업자등록번호", example = "1111-1111-1111")
        private String resCompanyIdentityNo;

        @ApiModelProperty(value = "담당자", example = "류제용")
        private String userName;

        @ApiModelProperty(value = "카드사", example = "SHINHAN")
        private CardCompany cardCompany;

        @ApiModelProperty(value = "희망한도", example = "10000000")
        private String hopeLimit;

        @ApiModelProperty(value = "실제카드한도", example = "10000000")
        private String grantLimit;

        @ApiModelProperty(value = "신청 상태", example = "ISSUED")
        public IssuanceStatus issuanceStatus;

        @ApiModelProperty(value = "마지막 신청 단계", example = "SIGN_SIGNATURE")
        private IssuanceDepth issuanceDepth;

        @ApiModelProperty(value = "인증서등록일", example = "9999-99-99 99:99:99")
        private LocalDateTime certRegisterDate;

        @ApiModelProperty(value = "신청완료일", example = "9999-99-99 99:99:99")
        private LocalDateTime applyDate;

        @ApiModelProperty(value = "심사완료일", example = "9999-99-99 99:99:99")
        private LocalDateTime decisionDate;
    }

    Page<SearchCorpResultDto> corpList(SearchCorpDto dto, Long idxUser, Pageable pageable);

    Page<SearchCorpResultDto> adminCorpList(SearchCorpListDto dto, Long idxUser, Pageable pageable);

    Page<CorpListDto> adminCorpListV2(CorpListDto dto, Pageable pageable);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ScrapCorpDto {
        @ApiModelProperty("법인 idx")
        public String idxCorp;

        @ApiModelProperty("법인명")
        public String corpName;

        @ApiModelProperty("타입A/B")
        public String typeAB;

        @ApiModelProperty("카드사")
        public CardCompany cardCompany;

        @ApiModelProperty("등급")
        public String grade;

        @ApiModelProperty("업데이트일자")
        private String updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ScrapCorpListDto {
        @ApiModelProperty("idx ")
        public Long idx;

        @ApiModelProperty("법인 idx")
        public Long idxCorp;

        @ApiModelProperty("법인명")
        private String corpName;

        @ApiModelProperty("타입")
        private String cardType;

        @ApiModelProperty("카드")
        private CardCompany cardCompany;

        @ApiModelProperty("등급")
        private String grade;

        @ApiModelProperty("전체계좌")
        private Long total;

        @ApiModelProperty("진행계좌")
        private Long progress;

        @ApiModelProperty("종료여부")
        private Boolean endFlag;

        @ApiModelProperty("업데이트시작")
        private LocalDateTime createdAt;

        @ApiModelProperty("업데이트완료")
        private LocalDateTime updatedAt;
    }

    Page<ScrapCorpListDto> scrapCorpList(ScrapCorpDto dto, Pageable pageable);
}
