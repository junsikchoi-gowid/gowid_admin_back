package com.nomadconnection.dapp.core.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NumberUtilsTests {

	@Test
	public void addComma(){
		Long longValue = 20000000000L;
		Integer integerValue = 1200000000;
		Double doubleValue = 300000D;
		Double doubleDecimalPointValue = 400000.123;

		String convertWithLong = NumberUtils.addComma(longValue);
		String convertWithInteger = NumberUtils.addComma(integerValue);
		String convertWithDouble = NumberUtils.addComma(doubleValue);
		String convertWithDoubleDecimalPoint = NumberUtils.addComma(doubleDecimalPointValue);

		assertThat(convertWithLong).isEqualTo("20,000,000,000");
		assertThat(convertWithInteger).isEqualTo("1,200,000,000");
		assertThat(convertWithDouble).isEqualTo("300,000");
		assertThat(convertWithDoubleDecimalPoint).isEqualTo("400,000.123");
	}

}