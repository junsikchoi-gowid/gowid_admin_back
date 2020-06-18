package com.nomadconnection.dapp.api.dto.shinhan.gateway;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @interfaceID : 3000
 * @description : BPR데이터 존재여부 확인 request
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DataPart3000Req extends CommonPart {

    private String d001;    // BPR_MAP코드(Mandatory)

    private String d002;    // BPR업무접수번호(Mandatory)

    private String d003;    // BPR_ECC번호(Optional)

    private String d004;    // BPR이미지KEY값(Optuoinal)

    public DataPart3000Req(String d001, String d002) {
        this.d001 = d001;
        this.d002 = d002;
    }

}
