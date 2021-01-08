package com.nomadconnection.dapp.core.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DateUtilsTest {

	@Test
	void isBetweenDate() {
		LocalDate baseDate = LocalDate.of(2021, 1, 8);

		LocalDate beforeStartDate = LocalDate.of(2021, 1, 7);
		LocalDate equalsStartDate = baseDate;
		LocalDate afterStartDate = LocalDate.of(2021, 1, 15);

		LocalDate endDate = LocalDate.of(2021, 1, 31);

		boolean isBetween = DateUtils.isBetweenDate(baseDate, beforeStartDate, endDate);
		boolean isEqualsAfter = DateUtils.isBetweenDate(baseDate, equalsStartDate, endDate);
		boolean isNotBetween = DateUtils.isBetweenDate(baseDate, afterStartDate, endDate);

		assertThat(isBetween).isTrue();
		assertThat(isEqualsAfter).isTrue();
		assertThat(isNotBetween).isFalse();
	}
}