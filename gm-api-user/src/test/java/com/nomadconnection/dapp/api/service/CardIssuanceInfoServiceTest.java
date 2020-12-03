package com.nomadconnection.dapp.api.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.nomadconnection.dapp.UserApiApplication;
import com.nomadconnection.dapp.api.abstracts.AbstractMockitoTest;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserApiApplication.class)
class CardIssuanceInfoServiceTest extends AbstractMockitoTest {

	@Autowired
	private CardIssuanceInfoService cardIssuanceInfoService2;

	@Autowired
	private UserService userService;

	@Autowired
	private CorpService corpService;

	@InjectMocks
	private CardIssuanceInfoService cardIssuanceInfoService;
	@Mock
	private CardIssuanceInfoRepository cardIssuanceInfoRepository;

	@Test
	@DisplayName("CardIssuanceInfo_진행상태_업데이트")
	void shouldGetCommonCodeByCode() {
		final IssuanceStatus issuanceStatus = IssuanceStatus.INPROGRESS;
		Corp corp = Corp.builder().idx(67L).build();
		CardIssuanceInfo expected = CardIssuanceInfo.builder().idx(1L).issuanceStatus(IssuanceStatus.UNISSUED).build();

		//given
		given(cardIssuanceInfoRepository.findByCorpAndDisabledFalseOrderByIdxDesc(corp)).willReturn(Optional.of(expected));

		//when
		CardIssuanceInfo result = cardIssuanceInfoService.updateIssuanceStatus(corp, issuanceStatus);

		//then
		verify(cardIssuanceInfoRepository, atLeastOnce()).findByCorpAndDisabledFalseOrderByIdxDesc(corp);
		assertEquals(issuanceStatus, result.issuanceStatus());
	}

	@Test
	@Transactional
	@DisplayName("cardIssuanceInfo_User로_찾아서_Corp_매핑")
	void saveCardIssuanceInfo() {
		User user = userService.getUser(67L);
		Corp corp = corpService.findByCorpIdx(241L);
		CardIssuanceInfo cardIssuanceInfo = CardIssuanceInfo.builder().user(user).build();
		cardIssuanceInfoService2.saveCardIssuanceInfo(cardIssuanceInfo);
		cardIssuanceInfoService2.updateCorpByUser(user, corp);

		CardIssuanceInfo issuanceInfo = cardIssuanceInfoService2.findTopByUser(user).orElseThrow(
			() -> new NotFoundException("Not Found CardIssuanceInfo")
		);

		assertEquals(corp.idx(), issuanceInfo.corp().idx());
		cardIssuanceInfoService2.deleteCardIssuanceInfo(cardIssuanceInfo);
	}


}