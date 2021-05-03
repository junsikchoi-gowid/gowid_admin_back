package com.nomadconnection.dapp.api.service.shinhan;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.dto.shinhan.DataPart1600;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
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

		CardIssuanceInfo cardIssuanceInfo = resumeService.updateIssuanceStatus(req);

		assertThat(cardIssuanceInfo.issuanceStatus()).isEqualTo(IssuanceStatus.ISSUED);
	}

}