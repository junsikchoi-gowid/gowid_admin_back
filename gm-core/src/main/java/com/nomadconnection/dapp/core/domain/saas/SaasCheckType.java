package com.nomadconnection.dapp.core.domain.saas;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum SaasCheckType {
	NEED_CANCEL(1L),		// 1: 해지필요, (Free-Trial 시) 만료예정
	NEW(2L),				// 2: 신규
	RE_REGISTRATION(3L),	// 3: 재등록
	STRANGE(4L),			// 4: 이상결제
	FREE_CHANGE(5L),		// 5: 무료사용에서 유료로 전환
	INCREASED(6L),		// 6: 급등
	FREE_EXPIRATION(7L),	// 7: 무료 사용 만료
	DUPLICATE(8L),		// 8: 중복결제
	;

	private Long code;
	public static SaasCheckType getType(Long code) {
		return Arrays.stream(SaasCheckType.values()).filter(saasCheckType -> saasCheckType.code == code)
			.findFirst().orElse(null);
	}
}
