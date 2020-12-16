package com.nomadconnection.dapp.api.v2.controller;

import com.nomadconnection.dapp.api.abstracts.AbstractWebMvcTest;
import com.nomadconnection.dapp.api.enums.VerifyCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends AbstractWebMvcTest {

	@Test
	void shouldGet400WithInvalidEmail() throws Exception {
		String email = "invalid@email.com";
		VerifyCode verifyCode = VerifyCode.PASSWORD_RESET;

		mockMvc.perform(
			get("/auth/v2/send").param("email", email).param("type", verifyCode.toString())
				.characterEncoding("UTF-8")
		)
		.andDo(print())
		.andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}
}