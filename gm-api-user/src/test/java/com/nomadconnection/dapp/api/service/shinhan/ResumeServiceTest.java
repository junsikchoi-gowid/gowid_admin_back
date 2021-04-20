package com.nomadconnection.dapp.api.service.shinhan;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.dto.shinhan.DataPart1600;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

class ResumeServiceTest extends AbstractSpringBootTest {

	@Autowired
	private ResumeService resumeService;

	@Test
	@Transactional
	void updateIssuanceStatus() {
		CardIssuanceDto.ResumeReq req = new CardIssuanceDto.ResumeReq();
		req.setD001("20210419");
		req.setD002("20005");

		resumeService.updateIssuanceStatus(req);

	}
}