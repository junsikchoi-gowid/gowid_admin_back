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

import java.time.LocalDate;
import java.util.List;

import static com.nomadconnection.dapp.api.v2.utils.ScrapingCommonUtils.ifNotAvailableCorpRegistrationScrapingTime;
import static com.nomadconnection.dapp.api.v2.utils.ScrapingCommonUtils.isNewCorp;
import static org.assertj.core.api.Assertions.assertThat;
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

	@Test
	@DisplayName("입력받은날짜가_올해4월_이전이면_true를_리턴한다")
	void shouldReturnTrueIfEnteredDateIsBeforeApril(){
		LocalDate now = LocalDate.now();
		assertThat(ScrapingCommonUtils.isBeforeApril(now)).isTrue();
	}

	@Test
	@DisplayName("현재날짜가_4월이전이고_결산월이_12월이면_당해년기준_2,3년전_아니면_1,2년전을_반환한다")
	void shouldReturnClosingStandardsWhenClosingMonthIsDecember() {
		LocalDate beforeApril = LocalDate.of(2021, 01, 07);
		LocalDate afterApril = LocalDate.of(2021, 05, 25);
		String closingMonth = "12";

		List<String> beforeAprilAndClosingDecember = ScrapingCommonUtils.getFindClosingStandards(beforeApril, closingMonth);
		List<String> afterAprilAndClosingDecember = ScrapingCommonUtils.getFindClosingStandards(afterApril, closingMonth);

		// 결산월 12월
		assertThat(beforeAprilAndClosingDecember.get(0)).isEqualTo("201912");
		assertThat(beforeAprilAndClosingDecember.get(1)).isEqualTo("201812");
		assertThat(afterAprilAndClosingDecember.get(0)).isEqualTo("202012");
		assertThat(afterAprilAndClosingDecember.get(1)).isEqualTo("201912");
	}

	@Test
	@DisplayName("결산월이_12월이아니면_당해년기준_1,2년전을_반환한다")
	void shouldReturnClosingStandardsWhenClosingMonthIsNotDecember() {
		LocalDate beforeApril = LocalDate.of(2021, 01, 07);
		LocalDate afterApril = LocalDate.of(2021, 05, 25);
		String closingMonth = "09";

		List<String> beforeAprilAndClosingSeptember = ScrapingCommonUtils.getFindClosingStandards(beforeApril, closingMonth);
		List<String> afterAprilAndClosingSeptember = ScrapingCommonUtils.getFindClosingStandards(afterApril, closingMonth);

		// 결산월 9월
		assertThat(beforeAprilAndClosingSeptember.get(0)).isEqualTo("202009");
		assertThat(beforeAprilAndClosingSeptember.get(1)).isEqualTo("201909");
		assertThat(afterAprilAndClosingSeptember.get(0)).isEqualTo("202009");
		assertThat(afterAprilAndClosingSeptember.get(1)).isEqualTo("201909");
	}

	@Test
	@DisplayName("결산월_개업일을_입력받아_신설법인인지_판단한다")
	void shouldReturnTrueWhenNewCorp(){
		LocalDate openDateCorpA = LocalDate.of(2019, 2, 1);
		LocalDate openDateCorpB = LocalDate.of(2020, 2, 1);
		LocalDate openDateCorpC = LocalDate.of(2021, 1, 7);
		int closingMonth = 12;

		boolean isNewCorpA = isNewCorp(closingMonth, openDateCorpA);
		boolean isNewCorpB = isNewCorp(closingMonth, openDateCorpB);
		boolean isNewCorpC = isNewCorp(closingMonth, openDateCorpC);

		assertThat(isNewCorpA).isFalse();
		assertThat(isNewCorpB).isTrue();
		assertThat(isNewCorpC).isTrue();
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