package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Getter
@AllArgsConstructor
public enum IssuanceDepth {
    // desc 추가
    SIGNUP("", "[0] 회원가입 완료"),
    HOPE_LIMIT("1", "[1] 희망한도 입력"),
    SELECT_CARD("0", "[2] 카드사 선택"),
    UNTACT_HOME("2", "[3] 이용약관 "),
    CORP_CERT("3", "[4] 법인여부 확인"),
    CORP_INFO("4", "[5] 법인정보 확인"),
    CORP_LEGAL_INFO("5", "[6] 법인 추가 정보"),
    EVAL_CONNECT("6", "[7] 계좌 연결"),
    EVAL_VENTURE("7", "[8] 추가 정보 입력"),
    OWNERS_UPLOAD("8", "[9] 주주 명부 업로드"),
    OWNERS_INFO("9", "[10] 주주 정보 입력"),
    UNTACT_LIMIT("10", "[11] 가능 한도 확인"),
    CARD_FORM("11", "[12] 카드 발급 정보"),
    CARD_ACCOUNT("12", "[13] 결제 계좌 선택"),
    SIGN_CEO("13", "[14] 대표자 인증"),
    SIGN_CARDMANAGER("15", "[15] 카드관리자 등록"),
    SIGN_SIGNATURE("14", "[16] 법인 전자서명");

    private String number;
    private String desc;

    // 안정화 후 삭제 예정
    public static IssuanceDepth getIssuanceDepthByNumber(String number) throws NoSuchElementException {
        return Arrays.stream(IssuanceDepth.values())
            .filter(issuanceDepth -> issuanceDepth.number.equals(number))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Not Found IssuanceDepth"));
    }

    public static IssuanceDepth getIssuanceDepth(String depthKey) throws NoSuchElementException {
        return Arrays.stream(IssuanceDepth.values())
            .filter(issuanceDepth -> issuanceDepth.toString().equals(depthKey))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Not Found IssuanceDepth"));
    }
}
