package com.nomadconnection.dapp.api.dto.shinhan.gateway;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @interfaceID : 1510
 * @description : 사업자등록증스크래핑
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DataPart_1510 extends CommonPart {

    private String d001;    // 발급번호

    private String d002;    // 법인명(상호)

    private String d003;    // 사업자등록번호

    private String d004;    // 사업자종류

    private String d005;    // 성명(대표자)

    private String d006;    // 사업장소재지(주소)

    private String d007;  // 주민등록번호(법인등록번호)

    private String d008;    // 개업일

    private String d009; // 사업자등록일

    private String d010; // 발급기관

    private String d011;    // 업태

    private String d012;    // 종목

}
