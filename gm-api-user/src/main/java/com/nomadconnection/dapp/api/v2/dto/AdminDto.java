package com.nomadconnection.dapp.api.v2.dto;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        @ApiModelProperty("법인명(상호)")
        public String resCompanyNm;

        @ApiModelProperty("사업자등록번호")
        public String resCompanyIdentityNo;

        @ApiModelProperty("사업자종류")
        public String resBusinessmanType;

        @ApiModelProperty("대표자")
        public String resUserNm;

        @ApiModelProperty("사업장 소재지")
        public String resUserAddr;

        @ApiModelProperty("업태")
        public String resBusinessItems;

        @ApiModelProperty("종목")
        public String resBusinessTypes;

        @ApiModelProperty("개업일")
        public String resOpenDate;

        @ApiModelProperty("사업자등록일")
        public String resRegisterDate;

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
