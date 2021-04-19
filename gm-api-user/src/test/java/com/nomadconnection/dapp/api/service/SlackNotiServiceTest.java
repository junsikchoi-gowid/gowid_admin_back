package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import com.nomadconnection.dapp.api.service.notification.SlackNotiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("dev")
public class SlackNotiServiceTest extends AbstractSpringBootTest {

    @Autowired
    SlackNotiService slackNotiService;

    @Test
    @DisplayName("창진원 슬랙 테스트")
    void kisedSlackTest() {
        slackNotiService.sendSlackNotification("테스트", slackNotiService.getSlackKisedUrl());
    }
}
