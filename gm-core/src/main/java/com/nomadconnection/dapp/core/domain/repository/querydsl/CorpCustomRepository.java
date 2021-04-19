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
import java.util.List;

public interface CorpCustomRepository {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    // Todo: deprecate code 정리 후 SearchCorpListDto로 함수명 변경
    class SearchCorpListDtoV2 {
        @ApiModelProperty(value = "담당자", example = "류제용")
        private String userName;

        @ApiModelProperty(value = "사업자등록번호", example = "***-**-*****")
        private String resCompanyIdentityNo;

        @ApiModelProperty(value = "카드사", example = "SHINHAN")
        public CardCompany cardCompany;

        @ApiModelProperty(value = "법인명", example = "고위드")
        public String resCompanyNm;

        @ApiModelProperty(value = "마지막 신청 단계", example = "SIGN_SIGNATURE")
        private IssuanceDepth issuanceDepth;

        @ApiModelProperty(value = "신청 상태", example = "ISSUED")
        public List<IssuanceStatus> issuanceStatus;
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

        @ApiModelProperty(value = "사업자등록번호", example = "***-**-*****")
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

        @ApiModelProperty(value = "법인등록일(Gowid)", example = "9999-99-99 99:99:99")
        private LocalDateTime corpRegisterDate;

        @ApiModelProperty(value = "신청완료일", example = "9999-99-99 99:99:99")
        private LocalDateTime applyDate;

        @ApiModelProperty(value = "심사완료일", example = "9999-99-99 99:99:99")
        private LocalDateTime decisionDate;
    }

    Page<CorpListDto> adminCorpListV2(SearchCorpListDtoV2 dto, Pageable pageable);

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
