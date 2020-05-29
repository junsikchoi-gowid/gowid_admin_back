package com.nomadconnection.dapp.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class KcbDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private DataInfo data;
        private Result result;

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
        private String rqstCausCd;
        private String deviceId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Cert {
        private String smsCertNo;
        private String deviceId;
    }
}
