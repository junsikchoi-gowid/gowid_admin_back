package com.nomadconnection.dapp.core.domain.card;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CardCompany {
	SHINHAN("0306"),
	LOTTE("0311"),
	;

	private String code;
}
