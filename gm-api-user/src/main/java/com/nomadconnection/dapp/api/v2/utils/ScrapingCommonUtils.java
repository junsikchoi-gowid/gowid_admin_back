package com.nomadconnection.dapp.api.v2.utils;

import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.codef.io.helper.ResponseCode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public final class ScrapingCommonUtils {

	public static String DEFAULT_CLOSING_STANDARDS_MONTH = "12";

	public static boolean isScrapingSuccess(String code){
		return ResponseCode.CF00000.getCode().equals(code);
	}

	public static boolean isNotAvailableScrapingTime(String code){
		return ResponseCode.CF12041.getCode().equals(code);
	}

	public static String ifNullReplaceObject(List<?> listResStockList, int i, String strReturn) {
		if( listResStockList.size() > i ){
			return listResStockList.get(i).toString().isEmpty() ? strReturn : listResStockList.get(i).toString();
		}
		return strReturn;
	}

	public static boolean isNonProfitCorp(String licenseNo){
		final String NON_PROFIT_CORP = "82";
		String licenseMiddleNo = licenseNo.split("-")[1];
		return NON_PROFIT_CORP.equals(licenseMiddleNo);
	}

	public static List<String> getFindClosingStandards(String Mm) {
		List<String> returnYyyyMm = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy");
		Date date = new Date();
		cal.setTime(date);
		cal.add(Calendar.YEAR, -1);
		returnYyyyMm.add(df.format(cal.getTime()) + Mm);
		cal.add(Calendar.YEAR, -1);
		returnYyyyMm.add(df.format(cal.getTime()) + Mm);

		return returnYyyyMm;
	}

	public static boolean isLimitedCompany(JSONObject jsonDataCorpRegister) {
		JSONArray resRegisterEntriesList = (JSONArray) jsonDataCorpRegister.get("resRegisterEntriesList");
		JSONObject resRegisterEntry = (JSONObject) resRegisterEntriesList.get(0);
		JSONArray jsonArrayResCEOList = (JSONArray) resRegisterEntry.get("resCEOList");
		JSONArray jsonArrayResCompanyNmList = (JSONArray) resRegisterEntry.get("resCompanyNmList");

		for (Object item : jsonArrayResCEOList) {
			JSONObject obj = (JSONObject) item;
			if (GowidUtils.getEmptyStringToString(obj, "resPosition").contains("업무집행자")) {
				return true;
			}
		}
		for (Object item : jsonArrayResCompanyNmList) {
			JSONObject obj = (JSONObject) item;
			if (GowidUtils.getEmptyStringToString(obj, "resCompanyNm").contains("유한책임회사")) {
				return true;
			}
		}
		return false;
	}

}
