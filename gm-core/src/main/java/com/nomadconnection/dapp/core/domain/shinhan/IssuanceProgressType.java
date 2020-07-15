package com.nomadconnection.dapp.core.domain.shinhan;

import lombok.Getter;


/**
 * 전자서명전
 * 전자서명완료
 * 신규/기존 여부 체크 성공/실패
 * <p>
 * 스크래핑 전문 전송 성공/실패
 * 자동심사(1000/1400) 완료/실패
 * 이미지전송 성공/실패
 * 수동심사결과(1600) 성공/실패
 * 카드신청정보 전송 성공/실패
 * 전자서명값 전송 성공/실패
 */
@Getter
public enum IssuanceProgressType {
    NOT_SIGNED,         // 전자서명전
    SIGNED,             // 서명완료
    P_1200,             // 신규/기존 여부 체크              성공/실패
    P_15XX,             // 스크래핑 전문 전송               성공/실패
    P_AUTO_CHECK,       // 자동심사(1000/1400)             완료/실패
    P_IMG,              // 이미지전송                    성공/실패
    P_1600,             // 수동심사결과(1600)            성공/실패
    P_1100,             // 카드신청정보 전송                성공/실패
    P_1800;             // 전자서명값 전송                성공/실패

    private String code;
}
