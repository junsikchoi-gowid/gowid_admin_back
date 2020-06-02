package com.nomadconnection.dapp.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

public class KcbDto {

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private DataInfo data;
        private Result result;
        private String deviceId;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Result {
            private String code;
            private String desc;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class DataInfo {
            private String code;
            private String desc;
            private String reqCount;
            private String sendTime;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Authentication {

        @ApiModelProperty("성명")
        @NotEmpty
        private String userName;

        @ApiModelProperty("생년월일(yyMMdd)")
        @NotEmpty
        private String dateOfBirth;

        @ApiModelProperty("성별(1:남자, 2:여자)")
        @NotEmpty
        private String genderCode;

        @ApiModelProperty("통신사(SKT:01, KT:02, LG U+:03, SKT알뜰폰:04, KT알뜰폰:05, LG알뜰폰:06)")
        @NotEmpty
        private String phoneKind;

        @ApiModelProperty("휴대폰번호")
        @NotEmpty
        private String phoneNo;

        @Builder.Default
        private String deviceId = null;

        @Builder.Default
        private String rqstCausCd = "00";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Cert {

        @ApiModelProperty("인증번호")
        @NotEmpty
        private String smsCertNo;

        @ApiModelProperty("휴대폰인증 고유아이디")
        @NotEmpty
        private String deviceId;
    }
}
