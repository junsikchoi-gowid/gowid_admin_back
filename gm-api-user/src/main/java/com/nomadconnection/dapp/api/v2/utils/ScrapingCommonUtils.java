package com.nomadconnection.dapp.api.v2.utils;

import com.nomadconnection.dapp.api.exception.CodefApiException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.codef.io.helper.ResponseCode;
import com.nomadconnection.dapp.codef.io.helper.ScrapingMessageGroup;
import com.nomadconnection.dapp.core.utils.DateUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public final class ScrapingCommonUtils {

	public static String DEFAULT_CLOSING_STANDARDS_MONTH = "12";

	public static boolean isScrapingSuccess(String code){
		return ResponseCode.CF00000.getCode().equals(code);
	}

	public static void ifNotAvailableCorpRegistrationScrapingTime(String code, String extraMessage){
		final String notAvailableMessage = "서비스 시간이 종료되었습니다";
		boolean isNotAvailableTime = ResponseCode.CF12003.getCode().equals(code) && extraMessage.contains(notAvailableMessage);
		if(isNotAvailableTime) {
			throw new CodefApiException(ResponseCode.findByCode(code), ScrapingMessageGroup.GP00009);
		}
	}

	public static boolean isNotAvailableFinancialStatementsScrapingTime(String code){
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

	public static boolean isKisedCorp(String licenseNo){
		final String[] KISED_COPR_ARRAY = {"81", "86", "87" ,"88"};
		String licenseMiddleNo = licenseNo.split("-")[1];
		for (String kisedCorp : KISED_COPR_ARRAY) {
			if (kisedCorp.equals(licenseMiddleNo)) {
				return true;
			}
		}
		return false;
	}

	public static List<String> getFindClosingStandards(LocalDate date, String closingMonth) {
		List<String> yyyyMm = new ArrayList<>();
		int minusYear = 1;
		if(isBeforeApril(date) && "12".equals(closingMonth)){
			minusYear = 2;
		}

		for(int i = minusYear ; i < minusYear+2 ; i++){
			String year = String.valueOf(date.minusYears(i).getYear());
			yyyyMm.add(year + closingMonth);
		}

		return yyyyMm;
	}

	// 재무제표에서 신설법인 판단
	public static boolean isNewCorp(int closingMonth, LocalDate openDate) {
		LocalDate today = LocalDate.now();
		int year = closingMonth==12 ? today.getYear()-1 : today.getYear();
		LocalDate closingStandardsDate = LocalDate.of(year, closingMonth, today.getDayOfMonth());

		LocalDate preBaseStartDate = closingStandardsDate.plusMonths(1).withDayOfMonth(1);
		LocalDate preBaseEndDate = closingStandardsDate.plusMonths(4).with(TemporalAdjusters.lastDayOfMonth());
		boolean isPreSearchType = DateUtils.isBetweenDate(today, preBaseStartDate, preBaseEndDate);

		LocalDate startDate = LocalDate.of(today.getYear(), 01, 01);
		LocalDate endDate = today;
		if (isPreSearchType) {
			startDate = startDate.minusYears(1);
		}

		return DateUtils.isBetweenDate(openDate, startDate, endDate);
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

	public static boolean isCorporateBusiness(JSONObject jsonDataCorpLicense) {
		return GowidUtils.getEmptyStringToString(jsonDataCorpLicense, "resBusinessmanType").contains("법인사업자");
	}

	public static boolean isBeforeApril(LocalDate baseDate){
		LocalDate preBaseStartDate = LocalDate.now().withMonth(1).withDayOfMonth(1);
		LocalDate preBaseEndDate = LocalDate.now().withMonth(4).withDayOfMonth(30);
		boolean isBeforeApril = DateUtils.isBetweenDate(baseDate, preBaseStartDate, preBaseEndDate);

		return isBeforeApril;
	}

}
