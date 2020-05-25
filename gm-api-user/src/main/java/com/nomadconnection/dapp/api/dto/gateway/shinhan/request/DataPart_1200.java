package com.nomadconnection.dapp.api.dto.gateway.shinhan.request;

import lombok.*;

/**
 * @interfaceID : 1200
 * @description : 법인회원신규여부검증
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class DataPart_1200 extends CommonPart {

    private String d001;    // 사업자등록번호

    private String d002;      // 법인회원구분코드(01: 신용카드회원)

    private String d003;      // 신규대상여부(Y/N)

    private Long d004;    // 총한도금액

    private Long d005;  // 특화한도금액

    private Long d006;    // 제휴약정한도금액


}
