package com.nomadconnection.dapp.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CardCompany {

	SHINHAN("신한", "1"),
	LOTTE("롯데", "2"),
	HYUNDAI("현대", "3"),
	SAMSUNG("삼성", "4"),
	;

	private String description;
	private String code;
}