package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import com.nomadconnection.dapp.api.dto.FinancialConsumersResponseDto;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

class FinancialConsumersServiceTest extends AbstractSpringBootTest {

	@Autowired
	private FinancialConsumersService financialConsumersService;
	@Autowired
	private UserService userService;

	@Test
	@Transactional
	@DisplayName("상시근로 5인이상 여부 필드가 업데이트 된다")
	public void shouldUpdateOverFiveEmployees(){
		User user = userService.findByEmail("backend-test@gowid.com");
		boolean overFiveEmployees = true;

		FinancialConsumersResponseDto responseDto
			= financialConsumersService.updateOverFiveEmployees(user, CardType.GOWID, overFiveEmployees);

		assertThat(responseDto.getOverFiveEmployees()).isEqualTo(overFiveEmployees);
	}

}
