package com.nomadconnection.dapp.api.helper;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class GowidUtils {

	//
	//	todo: check mdn regex pattern
	//
	private static final Pattern PATTERN = Pattern.compile("^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$");

	public static boolean isValid(String mdn) {
		if (StringUtils.isEmpty(mdn)) {
			return false;
		}
		return PATTERN.matcher(mdn).matches();
	}

	public static Double doubleTypeGet(String str){
		Double dReturn = 0.0;

		if(str != null && !str.isEmpty()){
			dReturn = Double.parseDouble(str);
		}

		return dReturn;
	}
}
