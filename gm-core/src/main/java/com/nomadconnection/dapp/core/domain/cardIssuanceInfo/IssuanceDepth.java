package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Getter
@AllArgsConstructor
public enum IssuanceDepth {
    // desc 추가
    SIGNUP("[0] 회원가입 완료"),
    HOPE_LIMIT("[1] 희망한도 입력"),
    SELECT_CARD("[2] 카드사 선택"),
    UNTACT_HOME("[3] 이용약관 "),
    CORP_CERT("[4] 법인여부 확인"),
    CORP_INFO("[5] 법인정보 확인"),
    CORP_LEGAL_INFO("[6] 법인 추가 정보"),
    EVAL_CONNECT("[7] 계좌 연결"),
    EVAL_VENTURE("[8] 추가 정보 입력"),
    OWNERS_UPLOAD("[9] 주주 명부 업로드"),
    OWNERS_INFO("[10] 주주 정보 입력"),
    UNTACT_LIMIT("[11] 가능 한도 확인"),
    CARD_FORM("[12] 카드 발급 정보"),
    CARD_ACCOUNT("[13] 결제 계좌 선택"),
    SIGN_CEO("[14] 대표자 인증"),
    SIGN_CARDMANAGER("[15] 카드관리자 등록"),
    SIGN_SIGNATURE("[16] 법인 전자서명"),
    UNTACT_CONSUMER_PROTECTION("[2] 금소법 적합성 확인"), // kised
    CORP_CHECK_PROJECT_ID("[4] 과제번호 확인"), // kised
    CORP_CONFIRMATION_UPLOAD("[5] 최종선정 확인서 업로드") // kised
    ;

    private String desc;

    // 미사용
    public static IssuanceDepth getIssuanceDepth(String depthKey) throws NoSuchElementException {
        return Arrays.stream(IssuanceDepth.values())
            .filter(issuanceDepth -> issuanceDepth.toString().equals(depthKey))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Not Found IssuanceDepth"));
    }
}
