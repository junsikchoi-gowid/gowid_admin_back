package com.nomadconnection.dapp.api.dto.shinhan.gateway;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DataPart1700 extends CommonPart {
	private String d001;   // 신분증검증방법코드

	private String d002;    // 고객한글명

	private String d003;    // 고객주민등록번호, 외국인등록번호

	private String d004;    // 주민등록증발급일자

	private String d005;   // 운전변허번호

	private String d006;   // 운전면허지역코드

	private String d007;    // 운전면허위조방지번호
}
