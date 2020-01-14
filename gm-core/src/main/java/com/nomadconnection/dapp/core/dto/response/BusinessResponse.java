package com.nomadconnection.dapp.core.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class BusinessResponse {

    private ErrorInfo errorInfo;
    private Object data;



    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorInfo {

        @Builder.Default
        private boolean status = true;
        private String key;
        private String value;
    }
}
