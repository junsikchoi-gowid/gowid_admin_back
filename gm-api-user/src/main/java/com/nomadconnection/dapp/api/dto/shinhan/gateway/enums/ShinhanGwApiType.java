package com.nomadconnection.dapp.api.dto.shinhan.gateway.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ShinhanGwApiType {
    SH1000("1000", "SHCGWD", "EVL", "1972", "1", "01", "GWD", "01", "법인회원신규심사요청"),
    SH1100("1100", "SHCGWD", "ISS", "781", "1", "01", "GWD", "01", "법인카드신청"),
    SH1200("1200", "SHCGWD", "PVL", "256", "1", "01", "GWD", "01", "법인회원신규여부검증"),
    SH1400("1400", "SHCGWD", "MOD", "875", "1", "01", "GWD", "01", "법인조건변경신청"),
    SH1510("1510", "SHCGWD", "SCA", "804", "1", "01", "GWD", "01", "사업자등록증스크래핑"),
    SH1520("1520", "SHCGWD", "SCB", "864", "1", "01", "GWD", "01", "재무제표스크래핑"),
    SH1530("1530", "SHCGWD", "SCC", "797", "1", "01", "GWD", "01", "등기부등본스크래핑"),
    SH1600("1600", "SHCGWD", "REI", "797", "2", "01", "GWD", "01", "카드신청 재개"),
    SH1700("1700", "SHCGWD", "IDN", "797", "1", "01", "GWD", "01", "신분증진위확인요청"),
    SH1800("1800", "SHCGWD", "SGN", "797", "1", "01", "GWD", "01", "카드신청 전자서명값 전송"),
    SH1900("1900", "SHCGWD", "LMT", "797", "1", "01", "GWD", "01", "실시간 한도 감액 요청"),
    SH3000("3000", "SHCGWD", "BPR", "270", "1", "01", "GWD", "01", "BPR데이타 존재여부확인");

    private String transactionCode;     // TRANSACTION CODE
    private String initialText;         // TEXT개시문자
    private String fullTextLength;      // 전문길이
    private String code;                // 전문종별코드
    private String transferFlag;        // 송수신flag, 1:요청, 2:응답
    private String memberNo;            // 회원사번호, 01 : GOWID
    private String memberCode;          // 대외기관코드, GOWID 코드(3자리)
    private String searchMemberNo;      // 조회제휴사번호, 01 : GOWID
    private String name;

    public static ShinhanGwApiType getShinhanGwApiType(String paramCode) {
        return Arrays.stream(ShinhanGwApiType.values())
                .filter(companyType -> companyType.getCode().equals(paramCode))
                .findFirst()
                .orElse(null);
    }
}
