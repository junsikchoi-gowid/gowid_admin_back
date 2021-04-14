package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.Notification.SlackNotiDto;
import com.nomadconnection.dapp.api.service.notification.SlackNotiService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

import static com.nomadconnection.dapp.api.dto.Notification.SlackNotiDto.SignedUserNotiReq.getSlackSignedUserMessage;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@CrossOrigin(allowCredentials = "true")
@RequestMapping(SlackNotiController.URI.BASE)
@Api(tags = "Slack Notification")
public class SlackNotiController {

    public static class URI {
        public static final String BASE = "/slack-noti/v1";
        public static final String SEND = "/send";
        public static final String SEND_SIGNED = "/send/signed";
    }

    private final SlackNotiService slackNotiService;

    @ApiOperation(value = "Slack 알림 텍스트 발송")
    @PostMapping(URI.SEND)
    public ResponseEntity<Object> application(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestBody @Valid SlackNotiDto.NotiReq req,
            @RequestParam(defaultValue = "GOWID") CardType cardType) {
        slackNotiService.sendSlackNotification(req, user, cardType);
        if (CardType.GOWID.equals(cardType)) {
            slackNotiService.saveProgress(req.getText());
        }
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "회원가입 완료 Slack 알림 텍스트 발송")
    @PostMapping(URI.SEND_SIGNED)
    public ResponseEntity<Object> sendSignedUser(
        @ApiIgnore @CurrentUser CustomUser user,
        @RequestParam(defaultValue = "GOWID") CardType cardType) {
        slackNotiService.sendSlackNotification(getSlackSignedUserMessage(user),
            (CardType.GOWID.equals(cardType) ? slackNotiService.getSlackProgressUrl() : slackNotiService.getSlackKisedUrl()));
        return ResponseEntity.ok().build();
    }

}
