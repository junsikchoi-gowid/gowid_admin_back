package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import com.nomadconnection.dapp.api.dto.RiskDto;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1100;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_D1100Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1000Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1100Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1400Repository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.shinhan.D1000;
import com.nomadconnection.dapp.core.domain.shinhan.D1100;
import com.nomadconnection.dapp.core.domain.shinhan.D1400;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.utils.NumberUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

class AdminServiceTest extends AbstractSpringBootTest {

	@Autowired
	private AdminService adminService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CorpRepository corpRepository;

	@Autowired
	private CardIssuanceInfoRepository cardIssuanceInfoRepository;
	@Autowired
	private Lotte_D1100Repository lotteD1100Repository;
	@Autowired
	private D1000Repository d1000Repository;
	@Autowired
	private D1400Repository d1400Repository;
	@Autowired
	private D1100Repository d1100Repository;

	@Test
	void shouldBeUpdatedDepositAndGrantLimitWithShinhan() {
		double deposit = 55000000;
		String depositString = NumberUtils.doubleToString(deposit);
		User user = userRepository.findByAuthentication_EnabledAndEmail(true, "kan@gowid.com")
			.orElseThrow(() -> UserNotFoundException.builder().build());
		Corp corp = corpRepository.findByUser(user)
			.orElseThrow(() -> CorpNotRegisteredException.builder().build());
		RiskDto.RiskConfigDto dto = RiskDto.RiskConfigDto.builder().depositGuarantee(deposit).idxCorp(corp.idx()).build();
		user.cardCompany(CardCompany.SHINHAN);

		adminService.riskIdLevelChange(user.idx(), dto);

		CardIssuanceInfo cardIssuanceInfo = cardIssuanceInfoRepository.findByCorpAndCardType(corp, CardType.GOWID).get();
		D1000 d1000 = d1000Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(corp.idx()).get();
		D1400 d1400 = d1400Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(corp.idx()).get();
		D1100 d1100 = d1100Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(corp.idx()).get();

		assertThat(cardIssuanceInfo.card().grantLimit()).isEqualTo(depositString);
		assertThat(d1000.getD050()).isEqualTo(depositString);
		assertThat(d1400.getD014()).isEqualTo(depositString);
		assertThat(d1100.getD020()).isEqualTo(depositString);
	}

	@Test
	void shouldBeUpdatedDepositAndGrantLimitWithLotte() {
		double deposit = 55000000;
		String lotteDepositString = CommonUtil.divisionString(NumberUtils.doubleToString(deposit), 10000);
		User user = userRepository.findByAuthentication_EnabledAndEmail(true, "kan@gowid.com")
			.orElseThrow(() -> UserNotFoundException.builder().build());
		Corp corp = corpRepository.findByUser(user)
			.orElseThrow(() -> CorpNotRegisteredException.builder().build());
		RiskDto.RiskConfigDto dto = RiskDto.RiskConfigDto.builder().depositGuarantee(deposit).idxCorp(corp.idx()).build();
		user.cardCompany(CardCompany.LOTTE);

		adminService.riskIdLevelChange(user.idx(), dto);

		CardIssuanceInfo cardIssuanceInfo = cardIssuanceInfoRepository.findByCorpAndCardType(corp, CardType.GOWID).get();
		Lotte_D1100 d1000 = lotteD1100Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(corp.idx()).get();

		assertThat(cardIssuanceInfo.card().grantLimit()).isEqualTo(String.valueOf(deposit));
		assertThat(d1000.getAkLimAm()).isEqualTo(lotteDepositString);
	}

}
