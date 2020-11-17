package com.nomadconnection.dapp.api.dto.Notification;

import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.v2.enums.ScrapingType;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.Optional;

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
    public static class SignedUserNotiReq {

        @ApiModelProperty("이름")
        private String name;
        @ApiModelProperty("이메일")
        private String email;

        public static String getSlackSignedUserMessage(CustomUser user){
            return SignedUserNotiReq.builder()
                    .name(user.name())
                    .email(user.email())
                    .build()
                    .toString();
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("[ 회원가입 ]").append("\n")
                    .append("이름 : ").append(name).append("\n")
                    .append("계정 : ").append(email).append("\n")
                    .toString();
        }

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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScrapingNotiReq {
        private String email;
        private String cardIssuer;
        private String company;
        private Long idxCorp;
        private String scrapingType;
        private String code;
        private String message;
        private String extraMessage;


        public static String getScrapingSlackMessage(User user, ApiResponse.ApiResult response, ScrapingType scrapingType){
            Corp corp = user.corp();
            boolean existCorp = Optional.ofNullable(corp).isPresent();
            if(existCorp){
                return ScrapingNotiReq.builder()
                    .email(user.email())
                    .cardIssuer(user.cardCompany().getName())
                    .scrapingType(scrapingType.getDesc())
                    .company(corp.resCompanyNm())
                    .idxCorp(corp.idx())
                    .code(response.getCode())
                    .message(response.getDesc())
                    .extraMessage(response.getExtraMessage())
                    .build()
                    .toString();
            }

            return ScrapingNotiReq.builder()
                .email(user.email())
                .cardIssuer(user.cardCompany().getName())
                .scrapingType(scrapingType.getDesc())
                .code(response.getCode())
                .message(response.getDesc())
                .extraMessage(response.getExtraMessage())
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
                .append("ScrapingType : ").append(scrapingType).append("\n")
                .append("Code : ").append(code).append("\n")
                .append("Message : ").append(message).append("\n")
                .append("Extra Message : ").append(extraMessage).append("\n")
                .toString();
        }

    }

}
