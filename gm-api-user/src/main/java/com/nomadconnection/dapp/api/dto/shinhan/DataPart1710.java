package com.nomadconnection.dapp.api.dto.shinhan;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DataPart1710 extends CommonPart {

	private String d001; // 사업자등록번호

	private String d002; // 과제번호

	private String d003; // 과제명

	private String d004; // 총 시작일

	private String d005; // 총 종료일

	private String d006; // 기관이름

	private String d007; // 총예산현금

	private String d008; // 총예산현물

	private String d009; // 은행코드

	private String d010; // 계좌번호

	private String d011; // 예금주명

	public static DataPart1710 of(String licenseNo, String projectId){
		return DataPart1710.builder().d001(licenseNo).d002(projectId).build();
	}

}
