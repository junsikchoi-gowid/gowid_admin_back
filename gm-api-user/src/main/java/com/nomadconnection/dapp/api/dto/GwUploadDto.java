package com.nomadconnection.dapp.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

public class GwUploadDto {

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private List<DataInfo> data;
        private Result result;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Result {
            private int code;
            private String desc;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class DataInfo {
            private String uploadedPath;
            private String fileName;
            private Long size;
        }
    }
}
