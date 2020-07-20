package com.nomadconnection.dapp.api.service.shinhan;

import com.nomadconnection.dapp.api.common.AsyncService;
import com.nomadconnection.dapp.api.dto.UserCorporationDto;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.CommonPart;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.enums.ShinhanGwApiType;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.repository.shinhan.GatewayTransactionIdxRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.GwTranHistRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.SignatureHistoryRepository;
import com.nomadconnection.dapp.core.domain.shinhan.GatewayTransactionIdx;
import com.nomadconnection.dapp.core.domain.shinhan.GwTranHist;
import com.nomadconnection.dapp.core.domain.shinhan.IssuanceProgressType;
import com.nomadconnection.dapp.core.domain.shinhan.SignatureHistory;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonService {

    private final GatewayTransactionIdxRepository gatewayTransactionIdxRepository;
    private final GwTranHistRepository gwTranHistRepository;
    private final AsyncService asyncService;
    private final UserService userService;
    private final SignatureHistoryRepository signatureHistoryRepository;

    public void saveProgressFailed(Long userIdx, IssuanceProgressType progressType) {
        asyncService.run(() -> saveIssuanceProgFailedBg(userIdx, progressType));
    }

    public void saveProgressFailed(UserCorporationDto.ResumeReq request, IssuanceProgressType progressType) {
        asyncService.run(() -> saveIssuanceProgFailedBg(request, progressType));
    }

    @Async
    public void saveIssuanceProgFailedBg(Long userIdx, IssuanceProgressType progressType) {
        userService.saveIssuanceProgFailed(userIdx, progressType);
    }

    @Async
    public void saveIssuanceProgFailedBg(UserCorporationDto.ResumeReq request, IssuanceProgressType progressType) {
        log.debug("### start saveIssuanceProgFailedBg");
        SignatureHistory signatureHistory = getSignatureHistoryByApplicationInfo(request.getD001(), request.getD002());
        userService.saveIssuanceProgFailed(signatureHistory.getUserIdx(), progressType);
        log.debug("### end saveIssuanceProgFailedBg");
    }

    public void saveProgressSuccess(Long userIdx, IssuanceProgressType progressType) {
        asyncService.run(() -> saveIssuanceProgSuccessBg(userIdx, progressType));
    }

    public void saveProgressSuccess(UserCorporationDto.ResumeReq request, IssuanceProgressType progressType) {
        asyncService.run(() -> saveIssuanceProgSuccessBg(request, progressType));
    }

    @Async
    public void saveIssuanceProgSuccessBg(Long userIdx, IssuanceProgressType progressType) {
        userService.saveIssuanceProgSuccess(userIdx, progressType);
    }

    @Async
    public void saveIssuanceProgSuccessBg(UserCorporationDto.ResumeReq request, IssuanceProgressType progressType) {
        log.debug("### start saveIssuanceProgSuccessBg");
        SignatureHistory signatureHistory = getSignatureHistoryByApplicationInfo(request.getD001(), request.getD002());
        userService.saveIssuanceProgSuccess(signatureHistory.getUserIdx(), progressType);
        log.debug("### end saveIssuanceProgSuccessBg");
    }

    public SignatureHistory getSignatureHistoryByApplicationInfo(String applicationDate, String applicationNum) {
        return signatureHistoryRepository.findFirstByApplicationDateAndApplicationNum(applicationDate, applicationNum).orElseThrow(
                () -> new BadRequestException(ErrorCode.Api.NOT_FOUND,
                        "not found signatureHistoryRepository. applicationDate[" + applicationDate + "], applicationNum[" + applicationNum + "]")
        );
    }


    // 연동 기록 저장
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
