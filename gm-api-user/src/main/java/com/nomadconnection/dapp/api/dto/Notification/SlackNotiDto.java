package com.nomadconnection.dapp.api.dto.Notification;

import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;

public class SlackNotiDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotiReq {

        @ApiModelProperty("Slack 에 전송할 텍스트")
        @NotEmpty(message = "email is empty")
        private String text;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecoveryNotiReq {
        private String email;
        private String cardIssuer;
        private String company;
        private Long idxCorp;
        private String code;
        private String message;
        private String extraMessage;


        public static String getSlackRecoveryMessage(Corp corp, ApiResponse.ApiResult response){
            return RecoveryNotiReq.builder()
                .email(corp.user().email())
                .cardIssuer(corp.user().cardCompany().getName())
                .company(corp.resCompanyNm())
                .code(response.getCode())
                .message(response.getDesc())
                .extraMessage(response.getExtraMessage())
                .idxCorp(corp.idx())
                .build()
                .toString();
        }

        @Override
        public String toString() {
            return new StringBuilder()
                .append("User : ").append(email).append("\n")
                .append("Card Issuer : ").append(cardIssuer).append("\n")
                .append("Company : ").append(company).append("\n")
                .append("IdxCorp : ").append(idxCorp).append("\n")
                .append("Code : ").append(code).append("\n")
                .append("Message : ").append(message).append("\n")
                .append("Extra Message : ").append(extraMessage).append("\n")
                .toString();
        }

    }



}
