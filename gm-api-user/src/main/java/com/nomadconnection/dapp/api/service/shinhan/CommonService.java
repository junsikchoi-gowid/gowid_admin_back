package com.nomadconnection.dapp.api.service.shinhan;

import com.nomadconnection.dapp.api.common.AsyncService;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.dto.shinhan.CommonPart;
import com.nomadconnection.dapp.api.dto.shinhan.DataPart1600;
import com.nomadconnection.dapp.api.dto.shinhan.enums.ShinhanGwApiType;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.common.GatewayTransactionIdx;
import com.nomadconnection.dapp.core.domain.common.IssuanceProgressType;
import com.nomadconnection.dapp.core.domain.common.SignatureHistory;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1200Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.GatewayTransactionIdxRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.GwTranHistRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.SignatureHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.shinhan.D1200;
import com.nomadconnection.dapp.core.domain.shinhan.GwTranHist;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonService {

    private final GatewayTransactionIdxRepository gatewayTransactionIdxRepository;
    private final GwTranHistRepository gwTranHistRepository;
    private final AsyncService asyncService;
    private final UserService userService;
    private final SignatureHistoryRepository signatureHistoryRepository;
    private final UserRepository userRepository;
    private final CorpRepository corpRepository;
    private final D1200Repository d1200Repository;

    public void saveProgressFailed(Long userIdx, IssuanceProgressType progressType) {
        asyncService.run(() -> saveIssuanceProgFailedBg(userIdx, progressType));
    }

    public void saveProgressFailed(CardIssuanceDto.ResumeReq request, IssuanceProgressType progressType) {
        asyncService.run(() -> saveIssuanceProgFailedBg(request, progressType));
    }

    @Async
    public void saveIssuanceProgFailedBg(Long userIdx, IssuanceProgressType progressType) {
        userService.saveIssuanceProgFailed(userIdx, progressType);
    }

    @Async
    public void saveIssuanceProgFailedBg(CardIssuanceDto.ResumeReq request, IssuanceProgressType progressType) {
        log.debug("### start saveIssuanceProgFailedBg");
        SignatureHistory signatureHistory = getSignatureHistoryByApplicationInfo(request.getD001(), request.getD002());
        userService.saveIssuanceProgFailed(signatureHistory.getUserIdx(), progressType);
        log.debug("### end saveIssuanceProgFailedBg");
    }

    public void saveProgressSuccess(Long userIdx, IssuanceProgressType progressType) {
        asyncService.run(() -> saveIssuanceProgSuccessBg(userIdx, progressType));
    }

    public void saveProgressSuccess(CardIssuanceDto.ResumeReq request, IssuanceProgressType progressType) {
        asyncService.run(() -> saveIssuanceProgSuccessBg(request, progressType));
    }

    @Async
    public void saveIssuanceProgSuccessBg(Long userIdx, IssuanceProgressType progressType) {
        userService.saveIssuanceProgSuccess(userIdx, progressType);
    }

    @Async
    public void saveIssuanceProgSuccessBg(CardIssuanceDto.ResumeReq request, IssuanceProgressType progressType) {
        log.debug("### start saveIssuanceProgSuccessBg");
        SignatureHistory signatureHistory = getSignatureHistoryByApplicationInfo(request.getD001(), request.getD002());
        userService.saveIssuanceProgSuccess(signatureHistory.getUserIdx(), progressType);
        log.debug("### end saveIssuanceProgSuccessBg");
    }

    public SignatureHistory getSignatureHistoryByApplicationInfo(String applicationDate, String applicationNum) {
        return signatureHistoryRepository.findFirstByApplicationDateAndApplicationNumOrderByUpdatedAtDesc(applicationDate, applicationNum).orElseThrow(
                () -> new BadRequestException(ErrorCode.Api.NOT_FOUND,
                        "not found signatureHistoryRepository. applicationDate[" + applicationDate + "], applicationNum[" + applicationNum + "]")
        );
    }

    public SignatureHistory getSignatureHistoryByUserIdx(long userIdx) {
        return signatureHistoryRepository.findFirstByUserIdxOrderByUpdatedAtDesc(userIdx).orElseThrow(
                () -> new BadRequestException(ErrorCode.Api.NOT_FOUND, "not found signatureHistoryRepository. userIdx[" + userIdx + "]")
        );
    }


    // 연동 기록 저장
    @Async
    @Transactional(noRollbackFor = Exception.class)
    public void saveGwTran(CommonPart commonPart, Long idxUser) {
        log.debug("## save tran {} - start", commonPart.getC004());
        GwTranHist gwTranHist = new GwTranHist();
        BeanUtils.copyProperties(commonPart, gwTranHist);
        gwTranHist.setUserIdx(idxUser);
        gwTranHist.setCorpIdx(getCorpIdx(idxUser));
        gwTranHistRepository.save(gwTranHist);
        log.debug("## save tran {} - end", commonPart.getC004());
    }

    @Async
    @Transactional(noRollbackFor = Exception.class)
    public void saveGwTranForD1600(DataPart1600 dataPart1600) {
        log.debug("## save tran {} - start", dataPart1600.getC004());
        GwTranHist gwTranHist = new GwTranHist();
        BeanUtils.copyProperties(dataPart1600, gwTranHist);
        D1200 d1200 = getD1200ByApplicationDateAndApplicationNum(dataPart1600.getD001(), dataPart1600.getD002());
        if (d1200 != null) {
            gwTranHist.setUserIdx(getUserIdx(d1200.getIdxCorp()));
            gwTranHist.setCorpIdx(d1200.getIdxCorp());
        }
        gwTranHistRepository.save(gwTranHist);
        log.debug("## save tran {} - end", dataPart1600.getC004());
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

    private Long getCorpIdx(Long userIdx) {
        if (ObjectUtils.isEmpty(userIdx)) {
            return null;
        }
        User user = userRepository.findById(userIdx).orElse(null);
        if (user == null) {
            return null;
        }
        return !ObjectUtils.isEmpty(user.corp()) ? user.corp().idx() : null;
    }

    private D1200 getD1200ByApplicationDateAndApplicationNum(String applicationDate, String applicationNum) {
        return d1200Repository.findFirstByD007AndD008OrderByUpdatedAtDesc(applicationDate, applicationNum).orElse(null);
    }

    private Long getUserIdx(Long corpIdx) {
        if (ObjectUtils.isEmpty(corpIdx)) {
            return null;
        }
        Corp corp = corpRepository.findById(corpIdx).orElse(null);
        if (corp == null) {
            return null;
        }
        return corp.user().idx();
    }
}
