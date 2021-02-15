package com.nomadconnection.dapp.api.v2.controller.admin;

import com.nomadconnection.dapp.api.abstracts.AbstractWebMvcTest;
import com.nomadconnection.dapp.api.dto.AccountDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminControllerTests extends AbstractWebMvcTest {

	private String token;

	@BeforeEach
	public void getToken() throws Exception {
		AccountDto account = AccountDto.builder().email("lhjang@gowid.com").password("wkdfogur1!").build();
		token = extractToken(login(account).andReturn());
	}

	@Test
	@Transactional
	@DisplayName("한도재심사_요청한_목록을_조회한다")
	public void shouldGetLimitRecalculationList() throws Exception {

		mockMvc.perform(
			get("/admin/v2/limit/recalculation")
				.header("Authorization", "Bearer " + token)
				.characterEncoding("UTF-8")
		).andDo(print())
		.andExpect(status().isOk());
	}

	@Test
	@Transactional
	@DisplayName("한도재심사_요청건을_조회한다")
	public void shouldGetLimitRecalculation() throws Exception {
		Long idxCorp = 0L;

		mockMvc.perform(
			get("/admin/v2/limit/recalculation/" + idxCorp)
				.header("Authorization", "Bearer " + token)
				.characterEncoding("UTF-8")
		).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@Transactional
	@DisplayName("법인의_한도를_재심사하고_저장한다")
	public void recalculateLimit() throws Exception {
		Long idxCorp = 0L;

		mockMvc.perform(
			post("/admin/v2/limit/recalculation/" + idxCorp)
				.header("Authorization", "Bearer " + token)
				.characterEncoding("UTF-8")
		).andDo(print())
			.andExpect(status().isOk());
	}


}