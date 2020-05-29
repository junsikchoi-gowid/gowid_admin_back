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
        private String userName;
        private String dateOfBirth;
        private String genderCode;
        private String phoneKind;
        private String phoneNo;
        private String deviceId;

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
