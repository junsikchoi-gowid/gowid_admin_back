package com.nomadconnection.dapp.api.v2.utils;

import com.nomadconnection.dapp.api.abstracts.AbstractMockitoTest;
import com.nomadconnection.dapp.api.exception.CodefApiException;
import com.nomadconnection.dapp.api.v2.dto.ScrapingResponse;
import com.nomadconnection.dapp.api.v2.service.scraping.ScrapingResultService;
import com.nomadconnection.dapp.codef.io.helper.ResponseCode;
import com.nomadconnection.dapp.codef.io.helper.ScrapingMessageGroup;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static com.nomadconnection.dapp.api.v2.utils.ScrapingCommonUtils.ifNotAvailableCorpRegistrationScrapingTime;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

class ScrapingCommonUtilsTest extends AbstractMockitoTest {
	@Mock
	private ScrapingResultService scrapingResultService;

	@Test
	@DisplayName("이용가능시간_아닐때_CodefApiException_반환하고_GP00009_메세지를_얻는다")
	void shouldThrowCodefApiExceptionAndGetGP00009Message() throws Exception {
		String response = getNotAvailableTimeErrorResponse().toString();

		//given
		given(scrapingResultService.getApiResult(response))
			.willReturn(make(getNotAvailableTimeErrorResponse()));

		//when
		ScrapingResponse scrapingResponse = scrapingResultService.getApiResult(response);

		//then
		assertThrows(CodefApiException.class,
			() -> ifNotAvailableCorpRegistrationScrapingTime(scrapingResponse.getCode(), scrapingResponse.getExtraMessage())
			, ScrapingMessageGroup.GP00009.getMessage());
	}

	private JSONObject getNotAvailableTimeErrorResponse() {
		JSONObject scrapingResponse = new JSONObject();
		JSONObject result = new JSONObject();
		result.put("code", ResponseCode.CF12003.getCode());
		result.put("extraMessage", "서비스 시간이 종료되었습니다. 서비스 제공시간에 사용하여 주십시오.");
		result.put("message", "해당 기관 서버에서 오류가 발생하였습니다. (해당 기관 오류 메시지가 있습니다.)");
		result.put("transactionId", "a63650b480824f678587caf34d6efdaa");

		scrapingResponse.put("result", result);
		scrapingResponse.put("data", new JSONObject());
		return scrapingResponse;
	}

	private ScrapingResponse make(JSONObject scrapingResponse){
		JSONObject[] result = new JSONObject[2];
		result[0] = (org.json.simple.JSONObject) scrapingResponse.get("result");
		result[1] = (org.json.simple.JSONObject) scrapingResponse.get("data");

		return ScrapingResponse.builder()
			.scrapingResponse(result)
			.code(result[0].get("code").toString())
			.message(result[0].getOrDefault("message","").toString())
			.extraMessage(result[0].getOrDefault("extraMessage","").toString())
			.transactionId(result[0].getOrDefault("transactionId","").toString())
			.connectedId(result[1].getOrDefault("connectedId","").toString())
			.build();
	}
}