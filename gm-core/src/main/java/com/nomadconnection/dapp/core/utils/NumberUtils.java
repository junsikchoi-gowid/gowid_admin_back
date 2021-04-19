package com.nomadconnection.dapp.core.utils;

import org.springframework.util.StringUtils;

import java.text.DecimalFormat;

public class NumberUtils {

	private static final String ZERO = "0";

	public static <T extends Number> String addComma(T number){
		DecimalFormat formatter = new DecimalFormat("###,###.####");
		if(number == null){
			return ZERO;
		}

		return formatter.format(number);
	}

	public static String doubleToString(double number){
		String result = "0";
		try {
			DecimalFormat formatter = new DecimalFormat("###.#####");
			result = formatter.format(number);
		} catch (NumberFormatException e){
			// do nothing
		}
		return result;
	}

	public static Long stringToLong(String value){
		Long longValue = 0L;
		try {
			if(!StringUtils.isEmpty(value)){
				longValue = Long.valueOf(value);
			}
		} catch (NumberFormatException e){
			// do nothing
		}
		return longValue;
	}

	public static String emptyStringToZero(String number){
		if(StringUtils.isEmpty(number)){
			return ZERO;
		}

		return number;
	}

}
