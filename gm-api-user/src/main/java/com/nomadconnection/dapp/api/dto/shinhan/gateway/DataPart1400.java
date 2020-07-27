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

    private String d006;   // 대표자주민등록번호. 전문규격변경으로인해 더미필드 처리 (null세팅)

    private String d007;    // 대표자명. 전문규격변경으로인해 더미필드 처리 (null세팅)

    private String d008; // 보증인고객식별번호

    private String d009;   // 보증인고객한글명

    private String d010;  // 현재제휴한도

    private String d011;    // 업종코드

    private String d012; // 특화카드구분코드

    private String d013;  // 조건변경법인심사신청구분코드

    private String d014;    // 변경후제휴한도금액

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

    private String d025;   // 접수일자

    private String d026;   // 접수순번

    // 전문 스펙 변경으로 추가됨
    private String d027;    // 법인등록번호

    private String d028;    // 법인자격코드

    private String d029;    // 법인영문명

    private String d030;    // 설립일자

    private String d031;    // 대표자코드

    private String d032;    // 대표자명1

    private String d033;    // 대표자주민등록번호1

    private String d034;    // 대표자영문명1

    private String d035;    // 대표자국적코드1

    private String d036;    // 대표자명2

    private String d037;    // 대표자주민등록번호2

    private String d038;    // 대표자영문명2

    private String d039;    // 대표자국적코드2

    private String d040;    // 대표자명3

    private String d041;    // 대표자주민등록번호3

    private String d042;    // 대표자영문명3

    private String d043;    // 대표자국적코드3

    private String d044;    // 직장우편앞번호

    private String d045;    // 직장우편뒷번호

    private String d046;    // 직장기본주소

    private String d047;    // 직장상세주소

    private String d048;    // 직장전화지역번호

    private String d049;    // 직장전화국번호

    private String d050;    // 직장전화고유번호

    private String d051;    // 팩스전화지역번호

    private String d052;    // 팩스전화국번호

    private String d053;    // 팩스전화고유번호

    private String d054;    // 신청관리자부서명

    private String d055;    // 신청관리자직위명

    private String d056;    // 신청관리자주민등록번호

    private String d057;    // 신청관리자명

    private String d058;    // 신청관리자전화지역번호

    private String d059;    // 신청관리자전화국번호

    private String d060;    // 신청관리자전화고유번호

    private String d061;    // 신청관리자전화내선번호

    private String d062;    // 신청관리자휴대전화식별번호

    private String d063;    // 신청관리자휴대전화국번호

    private String d064;    // 신청관리자휴대전화고유번호

    private String d065;    // 신청관리자이메일주소

    private String d066;    // 도로명참조KEY값
}
