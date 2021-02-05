package com.nomadconnection.dapp.api.service.notification;

import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.config.SlackNotiConfig;
import com.nomadconnection.dapp.api.dto.Notification.SlackNotiDto;
import com.nomadconnection.dapp.core.domain.notification.SlackNotification;
import com.nomadconnection.dapp.core.domain.repository.notification.SlackNotificationRepository;
import com.nomadconnection.dapp.core.security.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackNotiService {

    private final SlackNotiRpc notiRpc;
    private final SlackNotiConfig slackNotiConfig;
    private final SlackNotificationRepository repoSlack;

    public void sendSlackNotification(String text, String slackWebHookUrl) {
        if (!slackNotiConfig.getEnable()) {
            return;
        }
        try {
            SlackNotiDto.NotiReq req = new SlackNotiDto.NotiReq();
            req.setText(text);
            notiRpc.send(req , slackWebHookUrl);
            log.info("## send slack noti : [{}]", text);
        } catch (Exception e){
            log.error("Failed to send slack message. {}", e);
        }
    }

    public void sendSlackNotification(SlackNotiDto.NotiReq req, CustomUser user) {
        if (!slackNotiConfig.getEnable()) {
            return;
        }
        req.setText(req.getText().replace(Const.SLACK_NOTI_USER_EMAIL, user.email()));
        notiRpc.send(req, slackNotiConfig.getProgressUrl());
        log.info("## send slack noti : [{}]", req.getText());
    }

    public void saveProgress(String text){
        Map<String, String> map = new HashMap<>();

        String[] msgs = text.split("\n");
        for (String msg : msgs) {
            String[] values = msg.split(":");
            map.put(values[0].trim(), values[1].trim());
        }

        repoSlack.save(SlackNotification.builder().
            user(map.get("User")).
            screenName(map.get("Last Screen Name")).
            cardIssuer(map.get("Card Issuer")).
            hopeLimit(map.get("hope limit")).
            company(map.get("Company")).
            ceoType(map.get("Ceo type")).
            ceoAccount(map.get("#ofAccount")).
            totalBalance(map.get("Total Balance")).
            calculatedLimit(map.get("Calculated limit"))
            .build()
        );
    }

    public String getSlackRecoveryUrl(){
        return slackNotiConfig.getRecoveryUrl();
    }

    public String getSlackProgressUrl(){
        return slackNotiConfig.getProgressUrl();
    }

    public String getSlackSaasTrackerUrl(){
        return slackNotiConfig.getSaastrackerUrl();
    }

}