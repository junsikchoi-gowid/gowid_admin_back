package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.CardCompany;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandDto {
    @ApiModelProperty("이용약관(식별자)")
    public Long idx;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindAccount {

        @ApiModelProperty("이름")
        private String name;

        @ApiModelProperty("연락처")
        private String mdn;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyCard {

        @ApiModelProperty("카드회사이름 etc 1.현대 2.삼성 3.신한 4.롯데")
        private CardCompany companyCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswordPre {

        @ApiModelProperty("인증번호")
        private String code;

        @ApiModelProperty("email")
        private String email;

        @ApiModelProperty("비밀번호")
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswordAfter {
        @ApiModelProperty("이전 비밀번호")
        private String prePassword;

        @ApiModelProperty("이후 비밀번호")
        private String afterPassword;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Alarm {
        @ApiModelProperty("법인명")
        private String corpName;
        @ApiModelProperty("담당자")
        private String name;
        @ApiModelProperty("메일")
        private String email;
        @ApiModelProperty("벤처인증")
        private boolean ventureCertification;
        @ApiModelProperty("VC 투자 여부")
        private boolean vcInvestment;
    }
}
