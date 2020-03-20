package com.nomadconnection.dapp.core.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class BusinessResponse {

    @Builder.Default
    private Normal normal = new Normal();
    private Integer size;
    private Object data;



    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Normal {

        @Builder.Default
        private boolean status = true;
        private String key;
        private String value;
    }
}
