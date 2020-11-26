package com.nomadconnection.dapp.api.v2.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum CorpRegistration {
	;
	@Getter
	@AllArgsConstructor
	public enum Issue{
		MULTIPLE_RESULT("0", "발행/열람 실패(검색결과 다수의 법인이 나올 경우)"),
		SUCCESS("1", "발행/열람 성공"),
		SEARCH("2", "상호조회"),
		FAILED("3", "결과처리 실패 (모듈 수정후 원문Data 재조회 필요)");

		private String code;
		private String desc;
	}
	@Getter
	@AllArgsConstructor
	public enum InquiryType{
		NAME("0", "상호"),
		REGISTERED_NO("1", "등기번호"),
		ENROLL_NO("2", "등록번호"),
		ROMAN("3", "로마자");
		private String code;
		private String desc;
	}
}
