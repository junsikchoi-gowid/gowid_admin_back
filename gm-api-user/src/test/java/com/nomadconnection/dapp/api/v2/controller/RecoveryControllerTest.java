package com.nomadconnection.dapp.api.v2.controller;

import com.nomadconnection.dapp.api.abstracts.AbstractWebMvcTest;
import com.nomadconnection.dapp.api.v2.enums.ScrapingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RecoveryControllerTest extends AbstractWebMvcTest {

	String token;

	@BeforeEach
	public String getToken() throws Exception {
		String email = "lhjang@gowid.com";
		String password = "wkdfogur1!";
		token = getToken(email, password);
		return email;
	}

	@Test
	@DisplayName("수동으로_스크래핑후_200응답_받는다")
	void scrapCorpRegistration() throws Exception {
		Long userIdx = 67L;

		mockMvc.perform(
			post("/recovery/scrap/" + userIdx)
				.param("scrapingType", String.valueOf(ScrapingType.FINANCIAL_STATEMENTS))
				.param("resClosingStandards", "12")
				.header("Authorization", "Bearer " + token)
				.characterEncoding("UTF-8")
		)
			.andDo(print())
			.andExpect(status().isOk());
	}

}