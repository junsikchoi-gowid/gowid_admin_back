package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.UserApiApplication;
import com.nomadconnection.dapp.api.abstracts.AbstractMockitoTest;
import com.nomadconnection.dapp.api.service.lotte.LotteIssuanceService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ActiveProfiles("dev")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserApiApplication.class)
public class LotteIssuanceServiceTest extends AbstractMockitoTest {

    @Autowired
    private LotteIssuanceService lotteIssuanceService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CorpService corpService;

    @Test
    @Order(1)
    @DisplayName("웰컴메일 테스트")
   void sendReceiptEmail() {
        log.info("start");
        emailService.sendWelcomeEmail("2618125793", "10");
        log.info("end");
    }

}
