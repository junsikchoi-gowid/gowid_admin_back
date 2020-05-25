package com.nomadconnection.dapp.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssuanceService {

    /**
     * 카드 신청
     * todo :
     * 1) 각 연동별 db 데이터 추출
     * 2) 각 연동 구현 및 실패시 예외 처리
     * - 각 연동 uri 프로퍼티 추가
     * - 연동 아이디 테이블 제작 및 연동 아이디 추출
     * - 1400(기존)/1000(신규) 에서 보류시 처리
     * 3) 프론트 리턴 데이터 생성
     */
    @Transactional(rollbackFor = Exception.class)
    public void application() {

        // 1200(법인회원신규여부검증)

        // 1510(사업자등록증스크래핑)

        // 1520(재무제표스크래핑)

        // 1530(등기부등본스크래핑)

        // 1400(기존-법인조건변경신청) or 1000(신규-법인회원신규심사요청)

        // 1100(법인카드신청)

    }
}
