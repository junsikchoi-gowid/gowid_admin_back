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

	@Test
	void doubleToString() {
		double value = 20000000;
		Double wrapperValue = 20000000d;

		String doubleString = NumberUtils.doubleToString(value);
		String wrapperDoubleString = NumberUtils.doubleToString(wrapperValue);

		assertThat(doubleString).isEqualTo("20000000");
		assertThat(doubleString).isNotEqualTo("2.0E7");
		assertThat(wrapperDoubleString).isEqualTo("20000000");
		assertThat(wrapperDoubleString).isNotEqualTo(wrapperValue.toString());
	}

	@Test
	void stringToLong() {
		String value1 = "0";
		String value2 = "20000";
		String value3 = "text";

		Long convertedValue1 = NumberUtils.stringToLong(value1);
		Long convertedValue2 = NumberUtils.stringToLong(value2);
		Long convertedValue3 = NumberUtils.stringToLong(value3);

		assertThat(convertedValue1).isEqualTo(0L);
		assertThat(convertedValue2).isEqualTo(20000L);
		assertThat(convertedValue3).isEqualTo(0L);
	}

}