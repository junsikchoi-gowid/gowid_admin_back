package com.nomadconnection.dapp.api.v2.service.auth;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import com.nomadconnection.dapp.api.enums.VerifyCode;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.dto.response.ErrorResponse;
import com.nomadconnection.dapp.redis.enums.RedisKey;
import com.nomadconnection.dapp.redis.service.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthServiceTest extends AbstractSpringBootTest {

	@Autowired
	private AuthService authService;

	@Autowired
	private RedisService redisService;

	private String email;
	private ResponseEntity successResponse;

	@BeforeEach
	void init(){
		email = "lhjang@gowid.com";
		successResponse = ResponseEntity.ok().body(
			BusinessResponse.builder().build());
	}

	@Test
	@DisplayName("인증번호를_Redis에_저장하고_이메일을_보낸다")
	void sendVerificationCodeWhenResetPassword() {
		VerifyCode passwordResetVerifyCode = VerifyCode.PASSWORD_RESET;

		ResponseEntity responseEntity = authService.sendVerificationCode(email, passwordResetVerifyCode);

		assertEquals(successResponse, responseEntity);
	}

	@Test
	@DisplayName("유효한_인증번호와_비교한다")
	void verifyByValidCode() {
		sendVerificationCodeWhenResetPassword();
		String code = (String) redisService.getByKey(RedisKey.VERIFICATION_CODE, email);
		ResponseEntity responseEntity = authService.checkVerificationCode(email, code);

		assertEquals(successResponse, responseEntity);
	}

	@Test
	@DisplayName("유효하지않은_인증번호와_비교한다")
	void verifyByInvalidCode() {
		//given
		String invalidCode = String.format("%04d", new Random().nextInt(10000));

		//when
		ResponseEntity<ErrorResponse> responseEntity = authService.checkVerificationCode(email, invalidCode);

		//then
		assertEquals(ErrorCode.Mismatched.MISMATCHED_VERIFICATION_CODE.toString(), responseEntity.getBody().getError());
	}

	@Test
	@DisplayName("만료된_인증번호와_비교한다")
	void verifyByExpiredCode() throws InterruptedException {
		//given
		sendVerificationCodeWhenResetPassword();
		String code = (String) redisService.getByKey(RedisKey.VERIFICATION_CODE, email);
		redisService.setExpireSecondsAtValueOps(RedisKey.VERIFICATION_CODE, email, 1);
		Thread.sleep(1000L);

		//when
		ResponseEntity<ErrorResponse> responseEntity = authService.checkVerificationCode(email, code);

		//then
		assertEquals(ErrorCode.Mismatched.MISMATCHED_VERIFICATION_CODE.toString(), responseEntity.getBody().getError());
	}

	@Test
	@DisplayName("존재하지않는_이메일로_인증시_BadRequestException_발생")
	void verifyByNotExistEmail() {
		VerifyCode passwordResetVerifyCode = VerifyCode.PASSWORD_RESET;
		String email = "invalid@email.com";

		assertThrows(BadRequestException.class,
			() -> authService.sendVerificationCode(email, passwordResetVerifyCode)
		);
	}

}