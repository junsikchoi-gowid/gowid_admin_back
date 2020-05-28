package com.nomadconnection.dapp.api.dto.shinhan.gateway;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @interfaceID : 1520
 * @description : 재무제표스크래핑
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DataPart1520 extends CommonPart {

    private String d001;    // 사업자등록번호

    private String d002;    // 발급(승인)번호

    private String d003;  // 주민번호

    private String d004; // 상호(사업장명)

    private String d005;    // 발급가능여부

    private String d006;   // 시작일자

    private String d007; // 종료일자

    private String d008;    // 성명

    private String d009; // 주소

    private String d010;    // 종목

    private String d011;    // 업태

    private String d012;  // 작성일자

    private String d013;   // 귀속연도

    private String d014;  // 총자산

    private String d015;   // 매출

    private String d016;   // 납입자본금

    private String d017;   // 자기자본금

    private String d018; // 재무조사일

}
