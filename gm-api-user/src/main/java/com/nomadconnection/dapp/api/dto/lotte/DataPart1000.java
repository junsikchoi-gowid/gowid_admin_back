package com.nomadconnection.dapp.api.dto.lotte;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @interfaceID : 1000
 * @description : 법인신규심사
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DataPart1000 extends CommonPart {

	private String bzno; // 사업자등록번호
	private String bzNewYn; // 신규대상여부
}
