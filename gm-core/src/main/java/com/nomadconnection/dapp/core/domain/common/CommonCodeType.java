package com.nomadconnection.dapp.core.domain.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Getter
@AllArgsConstructor
public enum CommonCodeType {
	BANK_1("은행 코드"),
	CARD_1("카드사 코드"),
	BUSINESS_1("법인업종 코드"),
	GOWIDCARDS("제휴회사 코드"),
	REG_OFFICE("등기소"),
	REG_OFFICE_TYPE("법인구분"),
	SHINHAN_DRIVER_LOCAL_CODE("신한 운전면허 지역코드"),
	CONCENT_TYPE("이용약관"),
	LOTTE_LISTED_EXCHANGE("롯데 상장거래소 코드"),
	RISK_GRADE("리스크 등급"),
	STOCK("증권사 코드"),
	NT("공공기관 코드"),
	IMAGE_FLAG("이미지 생성여부")
	;

	private String description;

	public static CommonCodeType getCommonCodeType(String code) throws NoSuchElementException {
		return Arrays.stream(CommonCodeType.values())
			.filter(commonCodeType -> commonCodeType.toString().equals(code))
			.findFirst()
			.orElseThrow(() -> new NoSuchElementException("Not Found CommonCodeType"));
	}
}
