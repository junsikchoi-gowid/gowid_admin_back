package com.nomadconnection.dapp.api.v2.service.scraping;

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

	private String code;
	private String message;
	private String extraMessage;
	private String connectedId;
	private String transactionId;

	public JSONObject[] getApiResult(String str) throws ParseException {
		JSONObject[] result = new JSONObject[2];
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(str);

		setScrapingResponse(result, jsonObject);
		return result;
	}

	public JSONObject[] getApiResult(JSONObject jsonObject) {
		JSONObject[] result = new JSONObject[2];

		setScrapingResponse(result, jsonObject);
		return result;
	}

	public void setScrapingResponse(JSONObject[] result, JSONObject jsonObject){
		result[0] = (JSONObject) jsonObject.get("result");
		result[1] = (JSONObject) jsonObject.get("data");

		setCode(result[0].get("code").toString());
		setMessage(result[0].getOrDefault("message","").toString());
		setExtraMessage(result[0].getOrDefault("extraMessage","").toString());
		setTransactionId(result[0].getOrDefault("transactionId","").toString());
		setConnectedId(result[1].getOrDefault("connectedId", "").toString());
	}

}
