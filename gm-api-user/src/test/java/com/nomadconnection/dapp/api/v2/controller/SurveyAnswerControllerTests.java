package com.nomadconnection.dapp.api.v2.controller;


import com.nomadconnection.dapp.api.abstracts.AbstractWebMvcTest;
import com.nomadconnection.dapp.api.dto.AccountDto;
import com.nomadconnection.dapp.api.dto.SurveyDto;
import com.nomadconnection.dapp.api.service.SurveyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SurveyAnswerControllerTests extends AbstractWebMvcTest {

	@MockBean
	private SurveyService surveyService;

	private String token;

	@BeforeEach
	public void getToken() throws Exception {
		AccountDto account = AccountDto.builder().email("lhjang@gowid.com").password("wkdfogur1!").build();
		token = extractToken(login(account).andReturn());
	}

	SurveyDto buildFunnelsSurveys(String answer, String detail){
		final String DEFAULT = "DEFAULT";
		return SurveyDto.builder().title(DEFAULT).answer(answer).detail(detail).build();
	}

	@Test
	@DisplayName("설문조사주제로_유저의_설문조사_조회")
	void shouldSuccessWhenFindByTitle() throws Exception {
		SurveyDto survey = buildFunnelsSurveys("SNS", "페이스북");

		given(surveyService.findAnswerByTitle(67L , survey.getTitle()))
			.willReturn(Arrays.asList(buildFunnelsSurveys("KEYWORD", ""), survey));

		mockMvc.perform(
				get("/survey")
				.header("Authorization", "Bearer " + token)
					.characterEncoding("UTF-8")
				.content(json(survey))
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data[0].answer").exists());
	}


}