package com.nomadconnection.dapp.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonCodeType {
	BANK_1("은행 코드"),
	CARD_1("카드사 코드"),
	BUSINESS_1("법인업종 코드"),
	GOWIDCARDS("제휴회사 코드"),
	;

	private String description;
}
