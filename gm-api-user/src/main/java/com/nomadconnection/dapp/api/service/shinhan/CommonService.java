package com.nomadconnection.dapp.api.service.shinhan;

import com.nomadconnection.dapp.api.dto.shinhan.gateway.CommonPart;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.enums.ShinhanGwApiType;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.repository.shinhan.GatewayTransactionIdxRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.GwTranHistRepository;
import com.nomadconnection.dapp.core.domain.shinhan.GatewayTransactionIdx;
import com.nomadconnection.dapp.core.domain.shinhan.GwTranHist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonService {

    private final GatewayTransactionIdxRepository gatewayTransactionIdxRepository;
    private final GwTranHistRepository gwTranHistRepository;

    // 연동 기록 저장
    @Transactional
    protected void saveGwTran(CommonPart commonPart) {
        GwTranHist gwTranHist = new GwTranHist();
        BeanUtils.copyProperties(commonPart, gwTranHist);
        gwTranHistRepository.save(gwTranHist);
    }

    protected CommonPart getCommonPart(ShinhanGwApiType apiType) {
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

        long tmpTranId = 20000000000L + gatewayTransactionIdx.getIdx();
        return "0" + tmpTranId;     // 020000000001
    }
}
