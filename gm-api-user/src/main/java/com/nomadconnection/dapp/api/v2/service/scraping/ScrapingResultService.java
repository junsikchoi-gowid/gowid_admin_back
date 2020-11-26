package com.nomadconnection.dapp.api.v2.service.scraping;

import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.v2.dto.ScrapingResponse;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

@Setter
@Getter
@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ScrapingResultService {

	private ScrapingResponse responseDto;

	public ScrapingResponse getApiResult(String str) throws ParseException {
		JSONObject[] result = new JSONObject[2];
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(str);

		return buildScrapingResponse(result, jsonObject);
	}

	public ScrapingResponse getApiResult(JSONObject jsonObject) {
		JSONObject[] result = new JSONObject[2];

		return buildScrapingResponse(result, jsonObject);
	}

	public ScrapingResponse buildScrapingResponse(JSONObject[] result, JSONObject jsonObject){
		result[0] = (JSONObject) jsonObject.get("result");
		result[1] = (JSONObject) jsonObject.get("data");

		responseDto = ScrapingResponse.builder()
			.scrapingResponse(result)
			.code(result[0].get("code").toString())
			.message(result[0].getOrDefault("message","").toString())
			.extraMessage(result[0].getOrDefault("extraMessage","").toString())
			.transactionId(result[0].getOrDefault("transactionId","").toString())
			.connectedId(result[1].getOrDefault("connectedId","").toString())
			.build();

		return responseDto;
	}

	public ApiResponse.ApiResult getCodeAndMessage(ScrapingResponse responseDto){
		return ApiResponse.ApiResult.builder().code(responseDto.getCode()).desc(responseDto.getMessage()).extraMessage(responseDto.getExtraMessage()).build();
	}

}
