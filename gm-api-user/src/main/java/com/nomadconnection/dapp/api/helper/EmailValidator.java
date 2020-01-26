package com.nomadconnection.dapp.api.helper;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class EmailValidator {

	//
	//	todo: check email regex pattern
	//
	private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

	public static boolean isValid(String email) {
		if (StringUtils.isEmpty(email)) {
			return false;
		}
		return PATTERN.matcher(email).matches();
	}
}
