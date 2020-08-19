package com.nomadconnection.dapp.api.service.shinhan;

import com.nomadconnection.dapp.api.common.AsyncService;
import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.AdminDto;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.CommonPart;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.DataPart1100;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.DataPart1800;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.enums.ShinhanGwApiType;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.exception.api.SystemException;
import com.nomadconnection.dapp.api.service.EmailService;
import com.nomadconnection.dapp.api.service.rpc.ShinhanGwRpc;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.api.util.SignVerificationUtil;
import com.nomadconnection.dapp.core.domain.repository.shinhan.*;
import com.nomadconnection.dapp.core.domain.shinhan.*;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.encryption.Seed128;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final D1200Repository d1200Repository;
    private final D1000Repository d1000Repository;
    private final D1400Repository d1400Repository;
    private final D1100Repository d1100Repository;
    private final SignatureHistoryRepository signatureHistoryRepository;
    private final IssuanceProgressRepository issuanceProgressRepository;
    private final ShinhanGwRpc shinhanGwRpc;
    private final AsyncService asyncService;
    private final CommonService issCommonService;
    private final EmailService emailService;

    @Value("${encryption.seed128.enable}")
    private boolean ENC_SEED128_ENABLE;

    @Value("${mail.approved.send-enable}")
    boolean sendEmailEnable;

    // 1600(신청재개) 수신 후, 1100(법인카드 신청) 진행
    @Transactional(noRollbackFor = Exception.class)
    public CardIssuanceDto.ResumeRes resumeApplication(CardIssuanceDto.ResumeReq request) {
        issCommonService.saveGwTranForD1600(request);
        log.debug("### saveProgressFailed start");
        issCommonService.saveProgressFailed(request, IssuanceProgressType.P_1600);
        log.debug("### saveProgressFailed end");
        issuanceProgressRepository.flush();

        CardIssuanceDto.ResumeRes response = getResumeRes(request);
        issCommonService.saveGwTranForD1600(response);

        if (!Const.API_SHINHAN_RESULT_SUCCESS.equals(request.getC009())) {
            log.error("## incoming result of 1600 is fail.");
            log.error("## c009 = " + request.getC009());
            log.error("## c013 = " + request.getC013());
            return response;
        }

        asyncService.run(() -> procResume(request));

        issCommonService.saveProgressSuccess(request, IssuanceProgressType.P_1600);
        log.debug("## response 1600 => " + response.toString());

        return response;
    }

    private CardIssuanceDto.ResumeRes getResumeRes(CardIssuanceDto.ResumeReq request) {
        CardIssuanceDto.ResumeRes response = new CardIssuanceDto.ResumeRes();
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1600);
        BeanUtils.copyProperties(request, response);
        BeanUtils.copyProperties(commonPart, response);
        response.setC006(request.getC006());    // 거래 고유번호
        response.setC009(request.getC009());    // 코드
        response.setC013(request.getC013());    // 메시지
        return response;
    }

    @Async
    void procResume(CardIssuanceDto.ResumeReq request) {
        log.debug("## start thread for 1100/1800 ");
        SignatureHistory signatureHistory = getSignatureHistory(request);

        issCommonService.saveProgressFailed(signatureHistory.getUserIdx(), IssuanceProgressType.P_1100);
        proc1100(request, signatureHistory, signatureHistory.getUserIdx());  // 1100(법인카드신청)
        issCommonService.saveProgressSuccess(signatureHistory.getUserIdx(), IssuanceProgressType.P_1100);

        issCommonService.saveProgressFailed(signatureHistory.getUserIdx(), IssuanceProgressType.P_1800);
        proc1800(request, signatureHistory, signatureHistory.getUserIdx());  // 1800(전자서명값전달)
        issCommonService.saveProgressSuccess(signatureHistory.getUserIdx(), IssuanceProgressType.P_1800);

        sendApprovedEmail(request, signatureHistory.getCorpIdx());
    }

    private void sendApprovedEmail(CardIssuanceDto.ResumeReq request, long corpIdx) {
        if (!sendEmailEnable) {
            return;
        }
        D1200 d1200 = getD1200ByApplicationDateAndApplicationNum(request.getD001(), request.getD002());
        if (StringUtils.isEmpty(d1200.getD001())) {
            log.error("## biz no is empty! email not sent!");
            log.error("## application date={}, application num={}", request.getD001(), request.getD002());
            return;
        }

        D1100 d1100 = d1100Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(corpIdx).orElseThrow(
                () -> new SystemException(ErrorCode.External.INTERNAL_SERVER_ERROR,
                        "data of d1100 is not exist(corpIdx=" + corpIdx + ")")
        );

        emailService.sendApproveEmail(d1200.getD001(), d1100.getD039());
        log.debug("## approved email sent. biz no = " + d1200.getD001());
    }

    private SignatureHistory getSignatureHistory(CardIssuanceDto.ResumeReq request) {
        SignatureHistory signatureHistory = issCommonService.getSignatureHistoryByApplicationInfo(request.getD001(), request.getD002());
        updateApplicationCount(signatureHistory);
        return signatureHistory;
    }

    private SignatureHistory getSignatureHistory(Long userIdx) {
        SignatureHistory signatureHistory = issCommonService.getSignatureHistoryByUserIdx(userIdx);
        updateApplicationCount(signatureHistory);
        return signatureHistory;
    }

    private void updateApplicationCount(SignatureHistory signatureHistory) {
        Long count = signatureHistory.getApplicationCount();
        if (count == null) {
            count = 0L;
        }
        signatureHistory.setApplicationCount(count + 1);
        signatureHistoryRepository.save(signatureHistory);
    }

    public void procAdmin1800(AdminDto.Issuance1800Req request) {
        SignatureHistory signatureHistory = getSignatureHistory(request.getUserIdx());
        CardIssuanceDto.ResumeReq request1800 = new CardIssuanceDto.ResumeReq();
        request1800.setD001(signatureHistory.getApplicationDate());
        request1800.setD002(signatureHistory.getApplicationNum());

        issCommonService.saveProgressFailed(signatureHistory.getUserIdx(), IssuanceProgressType.P_1800);
        proc1800(request1800, signatureHistory, signatureHistory.getUserIdx());  // 1800(전자서명값전달)
        issCommonService.saveProgressSuccess(signatureHistory.getUserIdx(), IssuanceProgressType.P_1800);
    }

    private void proc1800(CardIssuanceDto.ResumeReq request, SignatureHistory signatureHistory, Long idxUser) {
        log.debug("## 1800 start");
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1800);

        String signedPlainString = SignVerificationUtil.verifySignedBinaryStringAndGetPlainString(signatureHistory.getSignedBinaryString());

        DataPart1800 requestRpc = DataPart1800.builder()
                .d001(getDigitalSignatureIdNumber(request.getD001(), request.getD002(), signatureHistory.getApplicationCount()))
                .d002(Const.ELEC_SIGNATURE_CERTI_PROD_CODE)
                .d003(CommonUtil.encodeBase64(signedPlainString))
                .build();
        BeanUtils.copyProperties(commonPart, requestRpc);

        shinhanGwRpc.request1800(requestRpc, idxUser);
        log.debug("## 1800 end");
    }

    private void proc1100(CardIssuanceDto.ResumeReq request, SignatureHistory signatureHistory, Long idxUser) {
        // 공통부
        log.debug("## 1100 start");
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1100);

        // 데이터부
        Long corpIdx = getCorpIdxFromLastRequest(request);
        D1100 d1100 = d1100Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(corpIdx).orElseThrow(
                () -> new SystemException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1100,
                        "data of d1100 is not exist(corpIdx=" + corpIdx + ")")
        );
        d1100.setD050(getDigitalSignatureIdNumber(request.getD001(), request.getD002(), signatureHistory.getApplicationCount()));

        // 연동
        DataPart1100 requestRpc = new DataPart1100();
        BeanUtils.copyProperties(d1100, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        if (ENC_SEED128_ENABLE) {
            requestRpc.setD025(d1100.getD025());            // 결제계좌번호
        } else {
            requestRpc.setD025(Seed128.decryptEcb(d1100.getD025()));
        }

        shinhanGwRpc.request1100(requestRpc, idxUser);
        log.debug("## 1100 end");
    }

    // 기존 1400/1000 연동으로 부터 법인 식별자 추출
    private Long getCorpIdxFromLastRequest(CardIssuanceDto.ResumeReq request) {
        Long corpIdx;

        D1200 d1200 = getD1200ByApplicationDateAndApplicationNum(request.getD001(), request.getD002());
        if ("Y".equals(d1200.getD003())) {
            D1000 d1000 = d1000Repository.findFirstByD071AndD072OrderByUpdatedAtDesc(request.getD001(), request.getD002());
            corpIdx = d1000.getIdxCorp();
        } else {
            D1400 d1400 = d1400Repository.findFirstByD025AndD026OrderByUpdatedAtDesc(request.getD001(), request.getD002());
            corpIdx = d1400.getIdxCorp();
        }

        if (StringUtils.isEmpty(corpIdx)) {
            String msg = "not fount applyNo[" + request.getD001() + "], applyDate[" + request.getD002() + "]";
            throw new BadRequestException(ErrorCode.Api.NOT_FOUND, msg);
        }

        return corpIdx;
    }

    private String getDigitalSignatureIdNumber(String applicationDate, String applicationNum, Long count) {
        D1200 d1200 = getD1200ByApplicationDateAndApplicationNum(applicationDate, applicationNum);
        return CommonUtil.getDigitalSignatureIdNumber(d1200.getD001(), count);
    }

    private D1200 getD1200ByApplicationDateAndApplicationNum(String applicationDate, String applicationNum) {
        return d1200Repository.findFirstByD007AndD008OrderByUpdatedAtDesc(applicationDate, applicationNum).orElseThrow(
                () -> new BadRequestException(ErrorCode.Api.NOT_FOUND,
                        "not found d1200 of applicationDate[" + applicationDate + "], applicationNum[" + applicationNum + "]")
        );
    }
}
