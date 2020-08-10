package com.nomadconnection.dapp.api.dto.shinhan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @interfaceID : 1200
 * @description : 법인회원신규여부검증
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DataPart1200 extends CommonPart {

    private String d001;    // 사업자등록번호

    private String d002;      // 법인회원구분코드(01: 신용카드회원)

    private String d003;      // 신규대상여부(Y/N)

    private String d004;    // 총한도금액

    private String d005;  // 특화한도금액

    private String d006;    // 제휴약정한도금액

    private String d007;    // 신청접수일자

    private String d008;    // 신청접수순번

}
