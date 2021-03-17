package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.UserApiApplication;
import com.nomadconnection.dapp.api.abstracts.AbstractMockitoTest;
import com.nomadconnection.dapp.api.v2.utils.FullTextJsonParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@Slf4j
@ActiveProfiles("dev")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserApiApplication.class)
public class FullTextJsonParserTests extends AbstractMockitoTest {

    @Test
    @Order(1)
    @DisplayName("한글추출 테스트")
    void getOnlyKorLan() {
        log.info("start");
        String result = FullTextJsonParser.getOnlyKorLan("남산성(NAM SAMUEL SANSUNG)");
        log.info("result {}", result);
        log.info("end");
    }
}
