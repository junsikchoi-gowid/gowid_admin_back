package com.nomadconnection.dapp.api.dto.Notification;

import com.nomadconnection.dapp.api.dto.SaasTrackerDto;
import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.v2.enums.ScrapingType;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.util.StringUtils;

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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaasTrackerNotiReq {
        private String title;
        private String company;
        private String name;
        private String email;
        private String message;

        /**
         * SaaS Tracker 사용 신청
         *
         * @param req
         * @return
         */
        public static String getSlackSaasTrackerUsageRequestMessage(SaasTrackerDto.SaasTrackerUsageReq req){
            StringBuffer message = new StringBuffer();
            message.append(">    - 회사명: ").append(req.getCompanyName()).append("\n")
                .append(">    - 담당자명: ").append(req.getManagerName()).append("\n")
                .append(">    - 이메일: ").append(req.getManagerEmail());

            return SaasTrackerNotiReq.builder()
                .title("*새로운 회사가 사용 신청했어요!*")
                .message(message.toString())
                .build()
                .toString();
        }

        /**
         * SaaS Tracker 제보하기 알림
         *
         * @param corp
         * @param req
         * @return
         */
        public static String getSlackSaasTrackerMessage(Corp corp, SaasTrackerDto.SaasTrackerReportsReq req){
            StringBuffer message = new StringBuffer();
            switch(req.getReportType()) {
                case 1:         // 항목 미표시
                    message.append(">    - SaaS 이름: ").append(req.getSaasName()).append("\n")
                            .append(">    - 결제 수단: ").append(req.getPaymentMethod() == 1 ? "신용카드" : "계좌이체").append("\n")
                            .append(">    - 최근 결제 금액: ").append(req.getPaymentPrice());
                    break;
                case 2:         // 정보 불일치
                    message.append(">    ").append(req.getIssue());
                    break;
                case 3:         // 무료 이용중인 항목
                    message.append(">    - SaaS 이름: ").append(req.getSaasName()).append("\n")
                            .append(">    - 무료 사용 만료일: ").append(req.getExperationDate()).append("\n")
                            .append(">    - 만료 알림 여부: ").append(req.getActiveExperationAlert() ? "알림" : "미알림");
                    break;
                default:
                    break;
            }

            return SaasTrackerNotiReq.builder()
                    .title("*정보가 정확하지 않아요!*")
                    .company(corp.resCompanyNm())
                    .name(corp.user().name())
                    .email(corp.user().email())
                    .message(message.toString())
                    .build()
                    .toString();
        }

        /**
         * SaaS Tracker 준비 알림
         *
         * @param corp
         * @return
         */
        public static String getSlackSaasTrackerMessage(Corp corp){
            return SaasTrackerNotiReq.builder()
                    .title("*새로운 회사가 결제수단 연동을 완료했습니다!*")
                    .company(corp.resCompanyNm())
                    .name(corp.user().name())
                    .email(corp.user().email())
                    .build()
                    .toString();
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(">").append(title).append("\n").append(">").append("\n");

            if(!StringUtils.isEmpty(company)) {
                sb.append(">• 회사명 : ").append(company).append("\n");
            }
            if(!StringUtils.isEmpty(name)) {
                sb.append(">• 사용자 : ").append(name).append("(").append(email).append(")").append("\n");
            }
            if(!StringUtils.isEmpty(message)) {
                sb.append(">• 내용 : ").append("\n").append(message);
            }
            return sb.toString();
        }
    }

}
