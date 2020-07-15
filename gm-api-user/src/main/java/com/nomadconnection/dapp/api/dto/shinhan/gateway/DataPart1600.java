package com.nomadconnection.dapp.api.dto.shinhan.gateway;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @interfaceID : 1600
 * @description : 신청재개
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DataPart1600 extends CommonPart {

    private String d001;    // 신청접수일자
    private String d002;      // 신청접수순번
    private String d003;      // 사업자등록번호

}
