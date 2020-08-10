package com.nomadconnection.dapp.api.dto.lotte;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * CommonPart
 *
 * @interfaceID : -
 * @description : 전문공통부
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommonPart {

	protected String protocolCode; // 전문종별코드
	protected String transferCode; // 송수신구분코드
	protected String guid; // 거래번호
	protected String transferDate; // 전문전송일시
	protected String responseCode; // 응답코드
	protected String spare; // 예비필드
	protected String totalLength; // 총길이
}
