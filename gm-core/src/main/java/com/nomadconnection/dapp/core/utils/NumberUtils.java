package com.nomadconnection.dapp.core.utils;

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
		DecimalFormat formatter = new DecimalFormat("###.#####");

		return formatter.format(number);
	}
}
