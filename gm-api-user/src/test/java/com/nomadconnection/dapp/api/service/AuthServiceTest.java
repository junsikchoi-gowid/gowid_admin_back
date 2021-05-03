package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import com.nomadconnection.dapp.api.dto.AuthDto;
import com.nomadconnection.dapp.core.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

class AuthServiceTest extends AbstractSpringBootTest {

	@Autowired
	private AuthService authService;

	@Autowired
	private UserService userService;

	@Test
	@Transactional
	@DisplayName("사용자의 정보를 가져온다")
	void Should_GetLoginUserInfos() {
		String email = "backend-test@gowid.com";
		User user = userService.findByEmail(email);
		Long userIdx = user.idx();

		AuthDto.AuthInfo info = authService.info(userIdx);

		assertThat(info.getIdx()).isEqualTo(userIdx);
		assertThat(info.getEmail()).isEqualTo(email);
	}
}