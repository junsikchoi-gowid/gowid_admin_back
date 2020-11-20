package com.nomadconnection.dapp.api.v2.utils;

import com.nomadconnection.dapp.codef.io.helper.ResponseCode;

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

}
