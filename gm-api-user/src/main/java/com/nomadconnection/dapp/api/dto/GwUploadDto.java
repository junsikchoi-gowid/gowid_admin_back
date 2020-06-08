package com.nomadconnection.dapp.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

public class GwUploadDto {

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private KcbDto.Response.DataInfo data;
        private KcbDto.Response.Result result;

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
            private String uploadedPath;
            private String fileName;
            private String size;
        }
    }
}
