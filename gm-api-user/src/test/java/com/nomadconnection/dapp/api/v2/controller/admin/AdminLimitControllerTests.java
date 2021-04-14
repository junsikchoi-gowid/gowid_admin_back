package com.nomadconnection.dapp.api.v2.controller.admin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomadconnection.dapp.api.abstracts.AbstractWebMvcTest;
import com.nomadconnection.dapp.api.dto.AccountDto;
import com.nomadconnection.dapp.core.domain.limit.ContactType;
import com.nomadconnection.dapp.core.domain.repository.limit.LimitRecalculationHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.limit.LimitRecalculationRepository;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminLimitControllerTests extends AbstractWebMvcTest {

	private String token;
	private Long idxCorp;

	private String body;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private LimitRecalculationRepository limitRecalculationRepository;

	@Autowired
	private LimitRecalculationHistoryRepository limitRecalculationHistoryRepository;

	@BeforeEach
	public String getToken() throws Exception {
		AccountDto account = AccountDto.builder().email("lhjang@gowid.com").password("wkdfogur1!").build();
		token = extractToken(login(account).andReturn());
		return null;
	}

	@BeforeEach
	public void tearDown(){
		limitRecalculationHistoryRepository.deleteAll();
		limitRecalculationRepository.deleteAll();
	}

	@BeforeEach
	public void setUp() throws JsonProcessingException {
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		idxCorp = 901L;

		body = objectMapper.writeValueAsString(
			LimitRecalculationRequestDto.builder()
				.cardLimit(1500000L)
				.hopeLimit(1000000L)
				.currentUsedAmount(2000000L)
				.companyName("(주)GOWID")
				.accountInfo("농협").contactType(ContactType.BOTH)
				.contents("plz limit~"));
	}

	@Test
	@Transactional
	@DisplayName("한도재심사_요청한_목록을_조회한다")
	public void shouldGetLimitRecalculationList() throws Exception {

		mockMvc.perform(
			get("/admin/v2/limit/recalculation")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.characterEncoding("UTF-8")
		).andDo(print())
		.andExpect(status().isOk());
	}

	@Test
	@Transactional
	@DisplayName("법인의_한도재심사_요청한_목록을_조회한다")
	public void shouldGetLimitRecalculation() throws Exception {
		requestRecalculateLimit();

		mockMvc.perform(
			get("/admin/v2/limit/recalculation/" + idxCorp)
				.header("Authorization", "Bearer " + token)
		).andDo(print())
		.andExpect(status().isOk());
	}

	@Test
	@Transactional
	@DisplayName("법인의_한도를_재심사를_저장하고_이메일을보낸다")
	public void requestRecalculateLimit() throws Exception {

		mockMvc.perform(
			post("/admin/v2/limit/recalculation")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(body)
		).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@Transactional
	@DisplayName("심사중인_법인이_재심사요청을_하면_예외를받는다")
	public void shouldGetExceptionWhenUnderReview() throws Exception {
		requestRecalculateLimit();

		mockMvc.perform(
			post("/admin/v2/limit/recalculation")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(body)
		).andDo(print())
			.andExpect(status().is4xxClientError());
	}

}