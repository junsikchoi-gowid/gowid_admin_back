package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.abstracts.AbstractMockitoTest;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

class CardIssuanceInfoServiceTest extends AbstractMockitoTest {
	@InjectMocks
	private CardIssuanceInfoService cardIssuanceInfoService;
	@Mock
	private CardIssuanceInfoRepository cardIssuanceInfoRepository;

	@Test
	@DisplayName("CardIssuanceInfo_진행상태_업데이트")
	void shouldGetCommonCodeByCode() throws Exception {
		final IssuanceStatus issuanceStatus = IssuanceStatus.INPROGRESS;
		Corp corp = Corp.builder().idx(67L).build();
		CardIssuanceInfo expected = CardIssuanceInfo.builder().idx(1L).issuanceStatus(IssuanceStatus.UNISSUED).build();

		//given
		given(cardIssuanceInfoRepository.findByCorp(corp)).willReturn(Optional.of(expected));

		//when
		CardIssuanceInfo result = cardIssuanceInfoService.updateIssuanceStatus(corp, issuanceStatus).getData();

		//then
		verify(cardIssuanceInfoRepository, atLeastOnce()).findByCorp(corp);
		assertEquals(issuanceStatus, result.issuanceStatus());
	}
}