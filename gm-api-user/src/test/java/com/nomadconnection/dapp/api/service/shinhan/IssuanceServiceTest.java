package com.nomadconnection.dapp.api.service.shinhan;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import com.nomadconnection.dapp.api.exception.api.SystemException;
import com.nomadconnection.dapp.api.service.CardIssuanceInfoService;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IssuanceServiceTest extends AbstractSpringBootTest {

	@Autowired
	private IssuanceService issuanceService;

	@Autowired
	private CardIssuanceInfoService cardIssuanceInfoService;

	@Test
	@Transactional
	@DisplayName("신한카드 1200번 응답이 03으로 올때, 카드발급상태를 EXISTING으로 업데이트하고 예외를 발생시킨다")
	void should_ThrowException_And_Update_IssuanceStatus_EXISTING_When_1200_ReturnResponseCode03(){
		Corp corp = Corp.builder().idx(723L).build();

		assertThrows(SystemException.class, () -> issuanceService.handleError1200(corp, CardType.GOWID, "03", "현재 심사진행중인 자료가 존재합니다."));

		CardIssuanceInfo cardIssuanceInfo = cardIssuanceInfoService.findTopByCorp(corp, CardType.GOWID);
		assertThat(cardIssuanceInfo.issuanceStatus()).isEqualTo(IssuanceStatus.EXISTING);
	}

}