package com.nomadconnection.dapp.api.v2.enums;

import lombok.Getter;

@Getter
public enum ScrapingType {

	CREATE_ACCOUNT("인증서 등록")
	, ADD_ACCOUNT("인증서 추가")
	, CORP_LICENSE("사업자 등록증")
	, CORP_REGISTRATION("법인 등기부등본")
	, FINANCIAL_STATEMENTS("표준 재무제표")
	;

	private final String desc;

	ScrapingType(String desc) {
		this.desc = desc;
	}
}
