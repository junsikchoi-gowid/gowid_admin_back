package com.nomadconnection.dapp.api.service.notification;

import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.config.SlackNotiConfig;
import com.nomadconnection.dapp.api.dto.Notification.SlackNotiDto;
import com.nomadconnection.dapp.core.security.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackNotiService {

    private final SlackNotiRpc notiRpc;
    private final SlackNotiConfig slackNotiConfig;

    public void sendSlackNotification(SlackNotiDto.NotiReq req, CustomUser user) {
        if (!slackNotiConfig.getEnable()) {
            return;
        }
        req.setText(req.getText().replace(Const.SLACK_NOTI_USER_EMAIL, user.email()));
        notiRpc.send(req);
        log.info("## send slack noti : [{}]", req.getText());
    }
}
