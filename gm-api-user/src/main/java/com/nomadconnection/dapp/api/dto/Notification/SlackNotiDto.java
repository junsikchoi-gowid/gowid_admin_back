package com.nomadconnection.dapp.api.dto.Notification;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}
