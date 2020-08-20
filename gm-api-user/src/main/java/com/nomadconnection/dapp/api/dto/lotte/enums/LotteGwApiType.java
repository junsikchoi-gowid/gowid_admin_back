package com.nomadconnection.dapp.api.dto.lotte.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum LotteGwApiType {
	LT1000("1000", "Q", "법인회원신규여부검증"),
	LT1100("1100", "Q", "법인카드입회"),
	LT1200("1200", "Q", "전자서명값전송"),
	IMAGE_ZIP("IMAGE_ZIP", "", "이미지 zip파일 생성요청"),
	IMAGE_TRANSFER("IMAGE_TRANSFER", "", "이미지 전송요청"),
	;

	private String protocolCode; // 전문종별코드
	private String transferCode; // 송수신구분코드
	private String name;

	public static LotteGwApiType getLotteGwApiType(String paramProtocolCode) {
		return Arrays.stream(LotteGwApiType.values())
				.filter(companyType -> companyType.getProtocolCode().equals(paramProtocolCode))
				.findFirst()
				.orElse(null);
	}
}
