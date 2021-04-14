package com.nomadconnection.dapp.api.dto.shinhan.enums;

public enum ShinhanInterfaceId {

    SH1000(1000, "법인회원신규심사요청"),
    SH1100(1100, "법인카드신청"),
    SH1200(1200, "법인회원신규여부검증"),
    SH1400(1400, "법인조건변경신청"),
    SH1510(1510, "사업자등록증스크래핑"),
    SH1520(1520, "재무제표스크래핑"),
    SH1530(1530, "등기부등본스크래핑"),
    SH1700(1700, "신분증진위확인"),
    SH1710(1710, "창진원과제번호확인"),
    ;

    private int id;
    private String name;

    ShinhanInterfaceId(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
