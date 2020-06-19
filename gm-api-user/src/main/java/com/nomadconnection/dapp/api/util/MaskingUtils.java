package com.nomadconnection.dapp.api.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MaskingUtils {

	public static String maskingBankAccountNumber(String bankAccountNumber) {
		StringBuilder stringBuilder = new StringBuilder(bankAccountNumber);
		stringBuilder.setCharAt(5, '*');
		stringBuilder.setCharAt(6, '*');
		stringBuilder.setCharAt(7, '*');
		stringBuilder.setCharAt(8, '*');
		stringBuilder.setCharAt(9, '*');
		return stringBuilder.toString();
	}
}
