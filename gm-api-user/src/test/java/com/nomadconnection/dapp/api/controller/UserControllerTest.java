package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.abstracts.AbstractWebMvcTest;
import com.nomadconnection.dapp.api.dto.ConsentDto;
import com.nomadconnection.dapp.api.dto.UserDto;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends AbstractWebMvcTest {

	@Test
	@Transactional
	public void shouldSavedConsentAndReturn200() throws Exception {
		ConsentDto.RegDto consents = ConsentDto.RegDto.builder().idxConsent(4L).status(true).build();
		UserDto.RegisterUserConsent dto = UserDto.RegisterUserConsent.builder().cardType(CardType.KISED).consents(Arrays.asList(consents)).build();
		String body = json(dto);

		mockMvc.perform(
			post(UserController.URI.BASE + UserController.URI.REGISTRATION_CONSENT)
			.header("Authorization","Bearer " + getToken())
			.param("idxUser", "48")
			.contentType(MediaType.APPLICATION_JSON)
			.content(body)
		)
		.andDo(print())
		.andExpect(status().isOk());
	}


}