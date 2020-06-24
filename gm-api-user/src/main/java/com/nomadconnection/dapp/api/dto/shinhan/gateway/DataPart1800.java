package com.nomadconnection.dapp.api.dto.shinhan.gateway;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DataPart1800 extends CommonPart {
	private String d001;    // 전자서명식별번호. GWD + YYYYMMDD + 사업자번호(10) + "00"
	private String d002;    // 전자서명인증제품코드. 기본값 확정 필요(솔루션업체코드)(테스트시99)
	private String d003;    // 전자서명식별값1. 신청 내역 전자서명값 세팅
	private String d004;    // 전자서명식별값2.
}
