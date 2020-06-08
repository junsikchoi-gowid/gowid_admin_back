package com.nomadconnection.dapp.api.dto.shinhan.gateway;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @interfaceID : 1400
 * @description : 법인회원조건변경신청
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DataPart1400 extends CommonPart {

    private String d001;   // 거래구분코드

    private String d002;    // 사업자등록번호

    private String d003;    // 회원구분코드

    private String d004;    // 법인명

    private String d005;   // 기업규모코드

    private String d006;   // 대표자주민등록번호

    private String d007; // 대표자명

    private String d008; // 보증인고객식별번호

    private String d009;   // 보증인고객한글명

    private Long d010;  // 현재제휴한도

    private String d011;    // 업종코드

    private String d012; // 특화카드구분코드

    private String d013;  // 조건변경법인심사신청구분코드

    private Long d014;    // 변경후제휴한도금액

    private String d015; // 신청인사번

    private String d016;  // 확인자사번

    private String d017; // 의견내용

    private String d018;  // 등록지점코드

    private String d019;   // 법인실소유자한글명

    private String d020;    // 법인실소유자영문명

    private String d021;  // 법인실소유자생년월일

    private String d022;    // 법인실소유자국적코드

    private String d023;   // 법인실소유자유형코드

    private String d024;   // 자금세탁방지실소유자지분율

    private String d025;   // 고위드기업등급

    private String d026; // 벤처확인서보유여부

    private String d027; // VC투자유치여부

    private String d028;  // 고위드계산한도

    private String d029; // 기준잔고

    private String d030;  // 45일 평균잔고

    private String d031;  // 45일 중간잔고

    private String d032;  // 현재잔고

    private String d033;  // 접수일자

    private String d034;  // 접수순번
}
