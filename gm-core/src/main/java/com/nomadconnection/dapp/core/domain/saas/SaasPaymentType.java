package com.nomadconnection.dapp.core.domain.saas;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * SaaS 결제 유형 Type
 */
@Getter
@AllArgsConstructor
public enum SaasPaymentType {

	/**
	 * 0: 미분류
	 * 1: 월결제
	 * 2: 연결제
	 * 3: 무료
	 * 4: 비정기
	 * 5: 분기결제
	 * 6: 일회성
	 */
	UNCATEGORIZED(0),
	MONTHLY(1),
	YEARLY(2),
	FREE_TRIAL(3),
	IRREGULAR(4),
	QUARTER(5),
	ONE_TIME(6),
	;

	private int code;
	public static SaasPaymentType getType(int code) {
		return Arrays.stream(SaasPaymentType.values()).filter(saasPaymentType -> saasPaymentType.code == code)
			.findFirst().orElse(null);
	}
}