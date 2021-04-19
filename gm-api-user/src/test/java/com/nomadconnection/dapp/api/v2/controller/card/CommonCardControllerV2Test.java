package com.nomadconnection.dapp.api.v2.controller.card;

import com.nomadconnection.dapp.api.abstracts.AbstractWebMvcTest;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceDepth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CommonCardControllerV2Test extends AbstractWebMvcTest {

	@Test
	@DisplayName("현재 로그인된 회원이 갖고있는 카드발급정보를 모두 조회한다")
	void shouldGetAllCardInfos() throws Exception {
		mockMvc.perform(
			get(CommonCardControllerV2.URI.BASE + CommonCardControllerV2.URI.CARDS)
				.header("Authorization", "Bearer " + getToken())
				.characterEncoding("UTF-8")
		)
			.andDo(print())
			.andExpect(jsonPath("$[0].cardType").exists())
			.andExpect(jsonPath("$[1].cardType").exists())
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(status().isOk());
	}

	@Test
	@Transactional
	@DisplayName("화면진행_Depth를_저장한다")
	void updateIssuanceDepth() throws Exception {
		mockMvc.perform(
			patch(CommonCardControllerV2.URI.BASE + CommonCardControllerV2.URI.ISSUANCE_DEPTH)
				.header("Authorization", "Bearer " + getToken())
				.characterEncoding("UTF-8")
			.param("depthKey", IssuanceDepth.CORP_CHECK_PROJECT_ID.toString())
			.param("cardType", CardType.KISED.toString())
		)
			.andDo(print())
			.andExpect(jsonPath("$.cardIssuanceInfoIdx").exists())
			.andExpect(jsonPath("$.cardType").exists())
			.andExpect(jsonPath("$.depthKey").exists())
			.andExpect(status().isOk());
	}
}
