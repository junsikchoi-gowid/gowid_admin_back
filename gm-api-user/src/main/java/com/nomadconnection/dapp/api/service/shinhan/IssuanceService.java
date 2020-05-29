package com.nomadconnection.dapp.api.service.shinhan;

import com.nomadconnection.dapp.api.dto.shinhan.gateway.CommonPart;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.DataPart1200;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.DataPart1510;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.enums.ShinhanGwApiType;
import com.nomadconnection.dapp.api.dto.shinhan.ui.UiResponse;
import com.nomadconnection.dapp.api.service.rpc.ShinhanGwRpc;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.D1200;
import com.nomadconnection.dapp.core.domain.D1510;
import com.nomadconnection.dapp.core.domain.GatewayTransactionIdx;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1200Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1510Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.GatewayTransactionIdxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssuanceService {

    private final GatewayTransactionIdxRepository gatewayTransactionIdxRepository;

    private final D1200Repository d1200Repository;
    private final D1510Repository d1510Repository;

    private final ShinhanGwRpc shinhanGwRpc;

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
    public UiResponse application(String businessLicenseNo) {

        // 1200(법인회원신규여부검증)
        proc1200(businessLicenseNo);

        // 1510(사업자등록증스크래핑)
        proc1510(businessLicenseNo);

        // 1520(재무제표스크래핑)

        // 1530(등기부등본스크래핑)

        // 1400(기존-법인조건변경신청) or 1000(신규-법인회원신규심사요청)
        // 보류 및 재요청 처리

        // 1100(법인카드신청)

        // todo : Response Type 정의
        return UiResponse.builder()
                .code("")
                .desc("")
                .build();
    }

    private void proc1200(String businessLicenseNo) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1200);

        // 데이터부 - db 추출, 세팅
        D1200 d1200 = d1200Repository.findFirstByD001OrderByCreatedAtDesc(businessLicenseNo);

        // 연동
        DataPart1200 requestRpc = new DataPart1200();
        BeanUtils.copyProperties(d1200, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        shinhanGwRpc.request1200(requestRpc);
    }

    private void proc1510(String businessLicenseNo) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1510);

        // 데이터부 - db 추출, 세팅
        D1510 d1510 = d1510Repository.findFirstByD003OrderByCreatedAt(businessLicenseNo);

        // 연동
        DataPart1510 requestRpc = new DataPart1510();
        BeanUtils.copyProperties(d1510, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        shinhanGwRpc.request1510(requestRpc);
    }

    private CommonPart getCommonPart(ShinhanGwApiType apiType) {
        // common part 세팅.
        // optional : 응답 코드, 대외 기관 코드, 응답 메시지
        return CommonPart.builder()
                .c001(apiType.getTransactionCode())
                .c002(apiType.getInitialText())
                .c003(apiType.getFullTextLength())
                .c004(apiType.getCode())
                .c005(apiType.getTransferFlag())
                .c006(getTransactionId(Integer.parseInt(apiType.getCode())))
                .c007(CommonUtil.getNowYYYYMMDD())
                .c008(CommonUtil.getNowHHMMSS())
                .c010(apiType.getMemberNo())
                .c011(apiType.getMemberCode())
                .c012(apiType.getSearchMemberNo())
                .build();
    }

    private String getTransactionId(Integer interfaceId) {
        GatewayTransactionIdx gatewayTransactionIdx = GatewayTransactionIdx.builder()
                .interfaceId(interfaceId).build();
        gatewayTransactionIdxRepository.save(gatewayTransactionIdx);
        gatewayTransactionIdxRepository.flush();

        long tmpTranId = 10000000000L + gatewayTransactionIdx.getIdx();
        return "0" + tmpTranId;     // 010000000001
    }
}
