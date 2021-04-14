package com.nomadconnection.dapp.api.v2.service.card;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.core.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

class CommonCardServiceV2Test extends AbstractSpringBootTest {

	@Autowired
	private CommonCardServiceV2 commonCardServiceV2;

	@Autowired
	private UserService userService;

	@Test
	@DisplayName("신한카드로 희망한도 5000만원 초과 입력 시 계산한도로 한도가 부여된다")
	@Transactional
	public void shouldReturn5000WhenGrantLimitExceed5000(){
		String hopeLimit = "51000000";
		User user = userService.findByEmail("lhjang@gowid.com");
		CardIssuanceDto.HopeLimitReq dto = CardIssuanceDto.HopeLimitReq.builder().hopeLimit(hopeLimit).build();
		CardIssuanceDto.CardRes response = commonCardServiceV2.saveHopeLimit(user.idx(), dto);

		assertThat(response.getGrantAmount()).isEqualTo("50000000");
	}

	@Test
	@DisplayName("한도저장 시 신한카드로 5000만원 이하로 입력 시 입력받은 값으로 한도가 부여된다")
	@Transactional
	public void shouldReturnLowerBetweenHopeLimitAndCalculatedLimit(){
		String hopeLimit = "51000000";
		User user = userService.findByEmail("lhjang@gowid.com");
		CardIssuanceDto.HopeLimitReq dto = CardIssuanceDto.HopeLimitReq.builder().hopeLimit(hopeLimit).build();

		CardIssuanceDto.CardRes response = commonCardServiceV2.saveHopeLimit(user.idx(), dto);
		assertThat(response.getGrantAmount()).isEqualTo("50000000");
	}

	@Test
	@DisplayName("한도저장 시 신한카드로 5000만원 이하로 입력 시 희망한도/산출한도 중 낮은값을 부여한도로 산출한다")
	@Transactional
	public void shouldSuccessWhenHopeLimitAndShinhanCard(){
		String hopeLimit = "49000000";
		User user = userService.findByEmail("lhjang@gowid.com");
		CardIssuanceDto.HopeLimitReq dto = CardIssuanceDto.HopeLimitReq.builder().hopeLimit(hopeLimit).build();
		CardIssuanceDto.CardRes response = commonCardServiceV2.saveHopeLimit(user.idx(), dto);

		assertThat(response.getGrantAmount()).isEqualTo(hopeLimit);
	}

}