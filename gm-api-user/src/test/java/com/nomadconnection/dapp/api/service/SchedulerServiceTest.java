package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ActiveProfiles("dev")
public class SchedulerServiceTest extends AbstractSpringBootTest {

    @Autowired
    UserService userService;

    @Autowired
    CorpRepository repoCorp;

    private final int NOTICE_DAYS = 78;
    private final int EXPIRATION_DAYS = 85;

    @Test
    @DisplayName("만료법인 테스트")
    void resetExpiredCorp() {
        log.info("[ resetExpiredCorp ] scheduler start!");
        List<String> statusList = new ArrayList<>();
        statusList.add(IssuanceStatus.UNISSUED.toString());
        statusList.add(IssuanceStatus.INPROGRESS.toString());
        List<Corp> corps = repoCorp.findCorpByIssuanceStatus(statusList);
        LocalDateTime now = LocalDateTime.now();
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            log.info("hostName {}", hostName);
        } catch (Exception e) {
            log.error("[resetExpiredCorp] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
        }
        for (Corp corp : corps) {
            log.info("corp {}", corp.resCompanyNm());
            log.info("duration {}", Duration.between(corp.getCreatedAt(), now).toDays());
//            if (Duration.between(corp.getCreatedAt(), now).toDays() >= NOTICE_DAYS && !corp.user().isReset()) {
//                log.info("notice");
//            }
//            if (Duration.between(corp.getCreatedAt(), now).toDays() >= EXPIRATION_DAYS) {
            if (corp.resCompanyNm().contains("갓잇코리아")) {
                userService.initUserInfo(repoCorp.searchIdxUser(corp.idx()));
            }
        }
        log.info("[ resetExpiredCorp ] scheduler end!");
    }
}
