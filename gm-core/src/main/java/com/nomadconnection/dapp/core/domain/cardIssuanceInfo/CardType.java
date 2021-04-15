package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CardType {
	GOWID("고위드 스타트업 카드", "DAAC6F", "000001", "01", "GOWID1"),
	KISED("창업진흥원 사업비 카드", "DDACGD", "000002", "06", "GOWID6");

	private final String name; // 카드이름
	private final String number; // 카드상품번호
	private final String code; // 법인카드신청구분코드
	private final String corpType; // 법인회원구분코드
	private final String recommender; // 권유자사번

}
