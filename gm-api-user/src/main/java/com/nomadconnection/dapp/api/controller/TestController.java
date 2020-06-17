package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.core.dto.ImageConvertDto;
import com.nomadconnection.dapp.core.utils.ImageConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

	private final ImageConverter converter;

	@PostMapping
	public String test(){
		try {
			ImageConvertDto param =
					ImageConvertDto.builder()
							.mrdType(1520)
							.data(corpRegisterJsonData())
							.build();

			return converter.convertJsonToImage(param);
		} catch (Exception e){
			e.printStackTrace();
		}
		return "fail";
	}

	public JSONObject corpRegisterJsonData() throws JSONException {
		JSONObject obj = new JSONObject();
		JSONObject result = new JSONObject();
		JSONObject lists = new JSONObject();

		obj.put("result", result);
		result.put("lists", lists);
		lists.put("resIssueNo", "0105-512-0305-962");
		//{"result":{"lists":{"resIssueNo":"0105-512-0305-962"}}}
		return obj;
	}

}
