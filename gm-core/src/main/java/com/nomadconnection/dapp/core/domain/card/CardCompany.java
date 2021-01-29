package com.nomadconnection.dapp.core.domain.card;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CardCompany {
	SHINHAN("0306", "신한카드", "00000"),
	LOTTE("0311", "롯데카드", "000"),
	;

	private String code;
	private String name;
	private String stockRate;

	public static boolean isLotte(CardCompany cardCompany){
		return LOTTE.equals(cardCompany);
	}

	public static boolean isShinhan(CardCompany cardCompany){
		return SHINHAN.equals(cardCompany);
	}

	public static String getStockRate(CardCompany cardCompany) {
		if (CardCompany.isShinhan(cardCompany)) {
			return SHINHAN.stockRate;
		} else if (CardCompany.isLotte(cardCompany)) {
			return LOTTE.stockRate;
		}
		return "";
	}
}
