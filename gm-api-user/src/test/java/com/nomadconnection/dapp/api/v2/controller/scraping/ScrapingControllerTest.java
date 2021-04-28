package com.nomadconnection.dapp.api.v2.controller.scraping;

import com.nomadconnection.dapp.api.abstracts.AbstractWebMvcTest;
import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ScrapingControllerTest extends AbstractWebMvcTest {

	@Test
	@DisplayName("POST /account/register/corp 호출 시, 파라미터 validation 실패면 400응답을 받는다")
	@Transactional
	void Should_Get400_When_Invalid_resBusinessCode() throws Exception {
		ConnectedMngDto.CorpInfo corpInfo
			= ConnectedMngDto.CorpInfo.builder()
			.resBusinessCode(".").resCompanyEngNm("TEST").resClosingStandards("12").resCompanyPhoneNumber("010-1234-5678").cardType(CardType.GOWID)
			.build();
		String body = json(corpInfo);

		mockMvc.perform(
			post("/codef/v2/account/register/corp")
				.header("Authorization", "Bearer " + getToken())
				.content(body)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
		)
			.andDo(print())
			.andExpect(status().is4xxClientError());
	}

}