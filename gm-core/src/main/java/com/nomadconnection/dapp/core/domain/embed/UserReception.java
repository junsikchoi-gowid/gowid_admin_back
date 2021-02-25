package com.nomadconnection.dapp.core.domain.embed;

import lombok.*;

import javax.persistence.Embeddable;

@Getter
@Setter
@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReception {
    @Builder.Default
    private Boolean isSendSms = false; // sms 수신 동의여부

    @Builder.Default
    private Boolean isSendEmail = false; // Email 수신 동의여부
}
