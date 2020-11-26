package com.nomadconnection.dapp.api.v2.utils;

import com.nomadconnection.dapp.api.helper.GowidUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 전문 Json
 * */

@Slf4j
@RequiredArgsConstructor
public class FullTextJsonParser {

	public static List saveJSONArray1(JSONArray jsonArray) {
		List<String> str = new ArrayList<>();
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			JSONArray jsonArrayResChangeDateList = (JSONArray) obj.get("resChangeDateList");
			JSONArray jsonArrayResRegistrationDateList = (JSONArray) obj.get("resRegistrationDateList");

			str.add(GowidUtils.getEmptyStringToString(obj, "resCompanyNm"));
			str.add(saveResChangeDateList(jsonArrayResChangeDateList));
			str.add(saveResRegistrationDateList(jsonArrayResRegistrationDateList));
		});
		return str;
	}

	public static List saveJSONArray2(JSONArray jsonArray) {
		List<String> str = new ArrayList<>();
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			JSONArray jsonArrayResChangeDateList = (JSONArray) obj.get("resChangeDateList");
			JSONArray jsonArrayResRegistrationDateList = (JSONArray) obj.get("resRegistrationDateList");

			str.add(GowidUtils.getEmptyStringToString(obj, "resUserAddr"));
			str.add(saveResChangeDateList(jsonArrayResChangeDateList));
			str.add(saveResRegistrationDateList(jsonArrayResRegistrationDateList));
		});
		return str;
	}

	public static List saveJSONArray3(JSONArray jsonArray) {
		List<String> str = new ArrayList<>();
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			JSONArray jsonArrayResChangeDateList = (JSONArray) obj.get("resChangeDateList");
			JSONArray jsonArrayResRegistrationDateList = (JSONArray) obj.get("resRegistrationDateList");
			str.add(saveResChangeDateList(jsonArrayResChangeDateList));
			str.add(saveResRegistrationDateList(jsonArrayResRegistrationDateList));
		});
		return str;
	}

	public static List saveJSONArray4(JSONArray jsonArray) {
		List<String> str = new ArrayList<>();
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			JSONArray jsonArrayResChangeDateList = (JSONArray) obj.get("resChangeDateList");
			JSONArray jsonArrayResRegistrationDateList = (JSONArray) obj.get("resRegistrationDateList");

			str.add(GowidUtils.getEmptyStringToString(obj, "resOneStockAmt"));
			str.add(saveResChangeDateList(jsonArrayResChangeDateList));
			str.add(saveResRegistrationDateList(jsonArrayResRegistrationDateList));
		});
		return str;
	}

	public static List saveJSONArray5(JSONArray jsonArray) {
		List<String> str = new ArrayList<>();
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			JSONArray jsonArrayResChangeDateList = (JSONArray) obj.get("resChangeDateList");
			JSONArray jsonArrayResRegistrationDateList = (JSONArray) obj.get("resRegistrationDateList");

			str.add(GowidUtils.getEmptyStringToString(obj, "resTCntStockIssue"));
			str.add(saveResChangeDateList(jsonArrayResChangeDateList));
			str.add(saveResRegistrationDateList(jsonArrayResRegistrationDateList));
		});
		return str;
	}

	public static List<Object> saveJSONArray6(JSONArray jsonArray) {
		List<Object> returnObj = new ArrayList<>();
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			JSONArray jsonArrayResStockItemList = (JSONArray) obj.get("resStockItemList");
			JSONArray jsonArrayResChangeDateList = (JSONArray) obj.get("resChangeDateList");
			JSONArray jsonArrayResRegistrationDateList = (JSONArray) obj.get("resRegistrationDateList");

			returnObj.add(GowidUtils.getEmptyStringToString(obj, "resTCntIssuedStock")); // 발행주식의 총수
			returnObj.add(GowidUtils.getEmptyStringToString(obj, "resCapital")); // 총액정보
			returnObj.add(jsonArrayResStockItemList); //주식 리스트
			returnObj.add(saveResChangeDateList(jsonArrayResChangeDateList)); // 변경일자
			returnObj.add(saveResRegistrationDateList(jsonArrayResRegistrationDateList)); //등기일자
		});
		return returnObj;
	}

	public static String saveJSONArray20(JSONArray jsonArray) {
		AtomicReference<String> str = new AtomicReference<>();
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			str.set(GowidUtils.getEmptyStringToString(obj, "resCorpEstablishDate"));
		});
		return str.get();
	}

	public static List<String> getJSONArrayCeo(JSONArray jsonArrayResCEOList) {
		List<String> str = new ArrayList<>();
		jsonArrayResCEOList.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			if(GowidUtils.getEmptyStringToString(obj, "resPosition").charAt(0) != '!' ){

				if(GowidUtils.getEmptyStringToString(obj, "resPosition").equals("공동대표이사")) {
					str.add("공동대표");
				}else{
					str.add(GowidUtils.getEmptyStringToString(obj, "resPosition"));
				}
				str.add(GowidUtils.getEmptyStringToString(obj, "resUserNm"));
				str.add(GowidUtils.getEmptyStringToString(obj, "resUserIdentiyNo"));
				str.add(GowidUtils.getEmptyStringToString(obj, "resUserAddr"));
			}
		});
		return str;
	}

	public static String getJSONArrayCeoType(JSONArray jsonArray) {
		String typeValue = "1";

		// 1: 단일대표 2: 개별대표 3: 공동대표
		int Co_representative_cnt = 0;
		int Each_representative_cnt = 0;
		for (Object item : jsonArray) {
			JSONObject obj = (JSONObject) item;
			log.debug("resPosition = [{}]", GowidUtils.getEmptyStringToString(obj, "resPosition"));
			if (GowidUtils.getEmptyStringToString(obj, "resPosition").equals("공동대표이사")) {
				Co_representative_cnt++;
				if (Co_representative_cnt > 1) {
					typeValue = "3";
					break;
				}
			} else if (GowidUtils.getEmptyStringToString(obj, "resPosition").equals("대표이사")) {
				Each_representative_cnt++;
				if (Each_representative_cnt > 1) {
					typeValue = "2";
					break;
				}
			}
		}
		return typeValue;
	}

	public static String saveResChangeDateList(JSONArray jsonArrayResChangeDateList) {
		AtomicReference<String> str = new AtomicReference<>();
		jsonArrayResChangeDateList.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			str.set(GowidUtils.getEmptyStringToString(obj, "resChangeDate"));
		});
		return str.get();
	}

	public static String saveResRegistrationDateList(JSONArray jsonArrayResRegistrationDateList) {
		AtomicReference<String> str = new AtomicReference<>();

		jsonArrayResRegistrationDateList.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			str.set(GowidUtils.getEmptyStringToString(obj, "resRegistrationDate"));
		});
		return str.get();
	}

	public static String getResIssueYn(JSONObject[] response){
		JSONObject jsonDataCorpRegister = response[1];
		return jsonDataCorpRegister.get("resIssueYN").toString();
	}

	public static String responseReplace(String response) {
		int iLimit = StringUtils.countOccurrencesOf( response ,"resTypeStockContentItemList");
		for(int i = 1 ; i < iLimit ; i++){
			String strMatch2 = " \\]\n      \\}, \\{\n        \"resNumber\" : \"" + i +"\",\n        \"resTypeStockContentItemList\" : \\[";
			response = response.replaceAll(strMatch2,",");
		}
		return response;
	}
}
