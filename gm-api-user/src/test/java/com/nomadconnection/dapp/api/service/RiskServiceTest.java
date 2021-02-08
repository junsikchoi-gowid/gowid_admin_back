package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskRepository;
import com.nomadconnection.dapp.core.domain.risk.Risk;
import com.nomadconnection.dapp.core.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RiskServiceTest extends AbstractSpringBootTest {

	@Autowired
	private RiskService riskService;

	@Autowired
	private UserService userService;

	@Autowired
	private RiskRepository riskRepository;

	@Test
	@Transactional
	void findRiskByUserAndDateLessThanEqual() {
		String now = CommonUtil.getNowYYYYMMDD();
		User user = userService.getUser(67L);
		Risk target = Risk.builder().user(user).date(now).build();

		riskRepository.save(target);
		Risk risk = riskService.findRiskByUserAndDateLessThanEqual(user, now);

		assertThat(risk.date()).isEqualTo(now);
	}
}