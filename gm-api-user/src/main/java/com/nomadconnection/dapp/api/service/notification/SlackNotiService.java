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

    public void sendSlackNotification(SlackNotiDto.NotiReq req, CustomUser user) {
        if (!slackNotiConfig.getEnable()) {
            return;
        }
        req.setText(req.getText().replace(Const.SLACK_NOTI_USER_EMAIL, user.email()));
        notiRpc.send(req);
        log.info("## send slack noti : [{}]", req.getText());

        Map<String, String> map = new HashMap<>();

        String[] msgs = req.getText().split("\n");
        for (String msg : msgs) {
            String[] values = msg.split(":");
            map.put(values[0].trim(), values[1].trim());
        }

        repoSlack.save(SlackNotification.builder().
                user(map.get("User")).
                screenName(map.get("LastScreenName")).
                cardIssuer(map.get("CardIssuer")).
                hopeLimit(map.get("hopelimit")).
                company(map.get("Company")).
                ceoType(map.get("Ceotype")).
                ceoAccount(map.get("#ofAccount")).
                totalBalance(map.get("TotalBalance")).
                calculatedLimit(map.get("Calculatedlimit"))
                .build()
        );
    }
}