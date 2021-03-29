package com.nomadconnection.dapp.api.v2.dto;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AdminDto {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateUserDto {
        @ApiModelProperty("이름")
        private String userName;

        @ApiModelProperty("휴대폰 번호")
        private String phone;

        @ApiModelProperty("회사명")
        private String corpName;

        @ApiModelProperty("직책")
        private String position;

        @ApiModelProperty("이메일")
        private String email;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CorpDto {

        @ApiModelProperty(value = "법인명(상호)", example = "주식회사 고위드(GOWID Inc)")
        public String resCompanyNm;

        @ApiModelProperty(value = "사업자등록번호", example = "261-81-25793")
        public String resCompanyIdentityNo;

        @ApiModelProperty(value = "사업자종류", example = "법인사업자")
        public String resBusinessmanType;

        @ApiModelProperty(value = "대표자", example = "김항기")
        public String resUserNm;

        @ApiModelProperty(value = "사업장 소재지", example = "서울특별시 강남구 도산대로 317, 14층(신사동, 호림아트센터 1빌딩)")
        public String resUserAddr;

        @ApiModelProperty(value = "업태", example = "응용 소프트웨어 개발 및 공급업|전자상거래 소매업|전자상거래 소매 중개업|컴퓨터 및 사무용 기계ㆍ장비 임대업|경영컨설팅")
        public String resBusinessItems;

        @ApiModelProperty(value = "종목", example = "정보통신업|도매 및 소매업|도매 및 소매업|사업시설 관리, 사업지원 및 임대 서비스업|서비스")
        public String resBusinessTypes;

        @ApiModelProperty(value = "개업일", example = "20150210")
        public String resOpenDate;

        @ApiModelProperty(value = "사업자등록일", example = "20150213")
        public String resRegisterDate;

        @ApiModelProperty(value = "법인등록일(Gowid)", example = "9999-99-99 99:99:99")
        public LocalDateTime corpRegisterDate;

        public static CorpDto from(Corp corp) {
            CorpDto corpDto = CorpDto.builder()
                .resBusinessItems(corp.resBusinessItems())
                .resBusinessTypes(corp.resBusinessTypes())
                .resBusinessmanType(corp.resBusinessmanType())
                .resCompanyIdentityNo(corp.resCompanyIdentityNo())
                .resCompanyNm(corp.resCompanyNm())
                .resOpenDate(corp.resOpenDate())
                .resRegisterDate(corp.resRegisterDate())
                .resUserAddr(corp.resUserAddr())
                .resUserNm(corp.resUserNm())
                .corpRegisterDate(corp.getCreatedAt())
                .build();
            return corpDto;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateIssuanceStatusDto {
        @ApiModelProperty("신청 상태")
        public IssuanceStatus issuanceStatus;
    }
}
