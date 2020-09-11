package com.nomadconnection.dapp.core.domain.card;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CardCompany {
	SHINHAN("0306", "신한카드"),
	LOTTE("0311", "롯데카드"),
	;

	private String code;
	private String name;

	public static boolean isLotte(CardCompany cardCompany){
		return LOTTE.equals(cardCompany);
	}

	public static boolean isShinhan(CardCompany cardCompany){
		return SHINHAN.equals(cardCompany);
	}
}
