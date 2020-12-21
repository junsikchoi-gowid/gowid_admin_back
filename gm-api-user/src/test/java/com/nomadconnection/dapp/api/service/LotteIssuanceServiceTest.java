package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import com.nomadconnection.dapp.api.service.lotte.LotteIssuanceService;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

@Slf4j
public class LotteIssuanceServiceTest extends AbstractSpringBootTest {

    @Autowired
    private LotteIssuanceService lotteIssuanceService;

    @Autowired
    private CorpService corpService;

    @Test
    @Order(1)
    @DisplayName("롯데카드 이메일 테스트")
   void sendReceiptEmail() {
        log.info("start");
        Corp corp = corpService.findByCorpIdx(535L);
        // 테스트진행시 sendReceiptEmail : private -> public 변경 필요
        lotteIssuanceService.sendReceiptEmail(corp);
        log.info("end");
    }

}
