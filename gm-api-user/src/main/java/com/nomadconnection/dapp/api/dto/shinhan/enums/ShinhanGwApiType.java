package com.nomadconnection.dapp.api.dto.shinhan.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ShinhanGwApiType {
    SH1000("GOSHC", "EVL", "1822", "1000", "1", "01", "GWD", "01", "법인회원신규심사요청"),
    SH1100("GOSHC", "ISS", "852", "1100", "1", "01", "GWD", "01", "법인카드신청"),
    SH1200("GOSHC", "PVL", "239", "1200", "1", "01", "GWD", "01", "법인회원신규여부검증"),
    SH1400("GOSHC", "MOD", "1835", "1400", "1", "01", "GWD", "01", "법인조건변경신청"),
    SH1510("GOSHC", "SCA", "817", "1510", "1", "01", "GWD", "01", "사업자등록증스크래핑"),
    SH1520("GOSHC", "SCB", "870", "1520", "1", "01", "GWD", "01", "재무제표스크래핑"),
    SH1530("GOSHC", "SCC", "1827", "1530", "1", "01", "GWD", "01", "등기부등본스크래핑"),
    SH1600("GOSHC", "REI", "194", "1600", "2", "01", "GWD", "01", "카드신청 재개"),
    SH1700("GOSHC", "IDC", "503", "1700", "1", "01", "GWD", "01", "신분증진위확인요청"),
    SH1800("GOSHC", "SGN", "8193", "1800", "1", "01", "GWD", "01", "카드신청 전자서명값 전송"),
    SH1900("GOSHC", "LMT", "395", "1900", "1", "01", "GWD", "01", "실시간 한도 감액 요청"),
    SH3000("GOSHC", "BPR", "384", "3000", "1", "01", "GWD", "01", "BPR데이타 존재여부확인"),
    BPR_TRANSFER("BPR_TRANSFER", null, null, null, null, null, null, null, "BPR데이타 전송요청");

    private String transactionCode;
    private String initialText;         // TEXT개시문자
    private String fullTextLength;      // 전문길이
    private String code;                // 전문종코드
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
