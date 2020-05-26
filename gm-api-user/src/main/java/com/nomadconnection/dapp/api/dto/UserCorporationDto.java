package com.nomadconnection.dapp.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class UserCorporationDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterCorporation {

        @ApiModelProperty("법인명(영문)")
        @NotEmpty
        private String engCorName;

        @ApiModelProperty("업종")
        @NotNull
        private Long businessType;

        @ApiModelProperty("사업장 전화번호")
        @NotEmpty
        private String corNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterVenture {

        @ApiModelProperty("벤처기업확인서 보유 여부")
        @NotNull
        private Boolean isVerifiedVenture;

        @ApiModelProperty("10억이상 VC투자 여부")
        @NotNull
        private Boolean isVC;

        @ApiModelProperty("투자사")
        private Long investor;

        @ApiModelProperty("누적투자금액")
        private String amount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterStockholder {

        @ApiModelProperty("25%이상의 지분을 보유한 개인여부")
        @NotNull
        private Boolean isHold25;

        @ApiModelProperty("1대주주 개인여부")
        private Boolean isPersonal;

        @ApiModelProperty("1대주주 법인의 주주명부 보유여부")
        private Long isStockholder;

        @ApiModelProperty("주주이름(한글)")
        @NotEmpty
        private String name;

        @ApiModelProperty("주주이름(영문)")
        @NotEmpty
        private String engName;

        @ApiModelProperty("생년월일 6자리")
        @NotEmpty
        private String birth;

        @ApiModelProperty("국적")
        @NotEmpty
        private String nation;

        @ApiModelProperty("지분율")
        @NotNull
        private Long rate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterCard {

        @ApiModelProperty("희망한도")
        @NotNull
        private String amount;

        @ApiModelProperty("신청수량")
        @NotNull
        private Long count;

        @ApiModelProperty("명세서 수령방법")
        private ReceiveType receiveType;

        @ApiModelProperty("수령주소")
        private String address;

        @ApiModelProperty("상세주소")
        private String detailAddr;

        @ApiModelProperty("우편번호")
        private String zipCode;

        public enum ReceiveType {
            POST,
            MOBILE,
            VISIT,
            ;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterAccount {

        @ApiModelProperty("은행명")
        @NotEmpty
        private String bank;

        @ApiModelProperty("계좌번호")
        @NotEmpty
        private String accountNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterCeo {

        @ApiModelProperty("국적(표준약어)")
        @NotNull
        private String nation;

        @ApiModelProperty("대표자(한글)")
        private String name;

        @ApiModelProperty("대표자(영문)")
        @NotEmpty
        private String engName;

        @ApiModelProperty("통신사")
        private String agency;

        @ApiModelProperty("휴대폰번호")
        @NotEmpty
        private String phoneNumber;

        @ApiModelProperty("신분증 종류")
        private IDType identityType;

        public enum IDType {
            RESIDENT,
            DRIVER,
            FOREIGN
            ;
        }
    }
}
