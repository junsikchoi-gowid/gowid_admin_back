package com.nomadconnection.dapp.api.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapingResponse {

	private JSONObject[] scrapingResponse;
	private String code;
	private String message;
	private String extraMessage;
	private String connectedId;
	private String transactionId;

}
