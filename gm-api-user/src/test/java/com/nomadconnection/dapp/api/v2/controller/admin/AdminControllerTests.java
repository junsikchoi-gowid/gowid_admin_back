package com.nomadconnection.dapp.api.v2.controller.admin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomadconnection.dapp.api.abstracts.AbstractWebMvcTest;
import com.nomadconnection.dapp.api.dto.AccountDto;
import com.nomadconnection.dapp.core.domain.limit.ContactType;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationDto.LimitRecalculationDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminControllerTests extends AbstractWebMvcTest {

	private String token;
	private Long idxCorp;

	private String body;
	private LocalDate date;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	public void getToken() throws Exception {
		AccountDto account = AccountDto.builder().email("lhjang@gowid.com").password("wkdfogur1!").build();
		token = extractToken(login(account).andReturn());
	}

	@BeforeEach
	public void setUp() throws JsonProcessingException {
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		idxCorp = 27L;
		date = LocalDate.of(2021,02,16);

		body = objectMapper.writeValueAsString(
			LimitRecalculationDetail.builder()
				.accountInfo("농협").contactType(ContactType.ANYTHING)
				.contents("plz").currentUsedAmount(2000000L).date(date));
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
	@DisplayName("해당날짜에_한도재심사_요청한_목록을_조회한다")
	public void shouldGetLimitRecalculation() throws Exception {
		requestRecalculateLimit();

		mockMvc.perform(
			get("/admin/v2/limit/recalculation/" + idxCorp)
				.header("Authorization", "Bearer " + token)
				.param("date", date.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
		).andDo(print())
		.andExpect(status().isOk());
	}

	@Test
	@Transactional
	@DisplayName("법인의_한도를_재심사를_요청하고_저장한다")
	public void requestRecalculateLimit() throws Exception {

		mockMvc.perform(
			post("/admin/v2/limit/recalculation/" + idxCorp)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(body)
		).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@Transactional
	@DisplayName("같은날짜_같은법인의_재심사요청이_오면_예외를받는다")
	public void shouldGetExceptionWhenRequestOnSameDate() throws Exception {
		requestRecalculateLimit();

		mockMvc.perform(
			post("/admin/v2/limit/recalculation/" + idxCorp)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(body)
		).andDo(print())
			.andExpect(status().is4xxClientError());
	}

}