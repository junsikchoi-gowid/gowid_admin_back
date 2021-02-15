package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import com.nomadconnection.dapp.api.service.shinhan.IssuanceService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("dev")
public class EmailServiceTest extends AbstractSpringBootTest {

	@Autowired
	private EmailService emailService;

	@Autowired
	private IssuanceService issuanceService;

	@Test
	@DisplayName("웰컴메일 테스트")
	void sendWelcomeEmail() {
		log.info("start");
		emailService.sendWelcomeEmail("2618125793", "10");
		log.info("end");
	}

	@Test
	@DisplayName("초기화메일 테스트")
	void sendResetEmail() {
		log.info("start");
		emailService.sendResetEmail("2618125793");
		log.info("end");
	}

	@Test
	@DisplayName("접수메일 테스트")
	void sendReceiptEmail() {
		log.info("start");
//		issuanceService.sendReceiptEmail(466L);
		log.info("end");
	}


}
