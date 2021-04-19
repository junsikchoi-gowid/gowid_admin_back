package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShinhanConsumerEmployeesType {
	UNDER_FIVE_EMPLOYEES("2"),
	OVER_FIVE_EMPLOYEES("3");

	private final String code;

	public static ShinhanConsumerEmployeesType from(boolean overFiveEmployees){
		ShinhanConsumerEmployeesType employeesType = OVER_FIVE_EMPLOYEES;
		if(!overFiveEmployees){
			employeesType = UNDER_FIVE_EMPLOYEES;
		}

		return employeesType;
	}

}
