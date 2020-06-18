package com.nomadconnection.dapp.api.dto.shinhan.gateway;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @interfaceID : 3000
 * @description : BPR데이터 존재여부 확인 response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DataPart3000Res extends CommonPart {

    private String d001;    // BPR데이터존재여부

    private String d002;    // BPR_MAP코드

    private String d003;    // BPR업무접수번호

    private String d004;    // BPR_ECC번호

    private String d005;    // BPR이미지KEY값

    private String d006;    // BPR접수지점코드

    private String d007;    // BPR경로코드

    private String d008;    // BPR응답코드

    private String d009;    // BPR생성구분코드

    private String d010;    // BPR처리응답코드

    private String d011;    // BPR응답메세지

}
