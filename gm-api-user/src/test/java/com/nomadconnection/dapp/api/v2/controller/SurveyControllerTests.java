package com.nomadconnection.dapp.api.v2.controller;


import com.nomadconnection.dapp.api.abstracts.AbstractWebMvcTest;
import com.nomadconnection.dapp.api.dto.AccountDto;
import com.nomadconnection.dapp.api.dto.SurveyDto;
import com.nomadconnection.dapp.api.service.SurveyService;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.common.SurveyType;
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

class SurveyControllerTests extends AbstractWebMvcTest {

	@MockBean
	private SurveyService surveyService;

	private String token;

	@BeforeEach
	public void getToken() throws Exception {
		AccountDto account = AccountDto.builder().email("lhjang@gowid.com").password("wkdfogur1!").build();
		token = extractToken(login(account).andReturn());
	}

	SurveyDto buildFunnelsSurveys(SurveyType surveyType, String detail ){
		return SurveyDto.builder().title(CommonCodeType.SURVEY_FUNNELS).answer(surveyType).detail(detail).build();
	}

	@Test
	@DisplayName("설문조사주제로_유저의_설문조사_조회")
	void shouldSuccessWhenFindByTitle() throws Exception {
		SurveyDto survey = buildFunnelsSurveys(SurveyType.SNS, "페이스북");

		given(surveyService.findByTitle(67L ,CommonCodeType.SURVEY_FUNNELS))
			.willReturn(Arrays.asList(buildFunnelsSurveys(SurveyType.DREAMPLUS, ""), survey));

		mockMvc.perform(
				get("/survey")
				.header("Authorization", "Bearer " + token)
					.characterEncoding("UTF-8")
				.content(json(survey))
//				.content(json(customUser))
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data[0].answer").exists());
	}

//	@Test
//	@DisplayName("유저_설문조사_저장")
//	void saveUserSurvey() {
//		webTestClient.post().uri("/survey").exchange()
//			.expectStatus().isOk();
//	}

}