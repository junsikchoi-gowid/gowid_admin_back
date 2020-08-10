package com.nomadconnection.dapp.api.dto.lotte;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @interfaceID : 1200
 * @description : 전자서명값전송
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DataPart1200 extends CommonPart {

	private String apfRcpno; // 접수일련번호

	private String bzno; // 사업자등록번호

	private String identifyValue; // 전자서명값

	private String receiptYn; // 접수 완료 여부

	private String message; // 접수메시지
}
