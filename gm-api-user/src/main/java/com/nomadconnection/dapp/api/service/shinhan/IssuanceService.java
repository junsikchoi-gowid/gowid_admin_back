package com.nomadconnection.dapp.api.service.shinhan;

import com.nomadconnection.dapp.api.common.AsyncService;
import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.dto.SurveyDto;
import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.dto.shinhan.*;
import com.nomadconnection.dapp.api.dto.shinhan.enums.ShinhanGwApiType;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.api.SystemException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.api.service.CardIssuanceInfoService;
import com.nomadconnection.dapp.api.service.CorpService;
import com.nomadconnection.dapp.api.service.EmailService;
import com.nomadconnection.dapp.api.service.SurveyService;
import com.nomadconnection.dapp.api.service.notification.SlackNotiService;
import com.nomadconnection.dapp.api.service.shinhan.rpc.ShinhanGwRpc;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.api.util.SignVerificationUtil;
import com.nomadconnection.dapp.api.v2.dto.kised.KisedRequestDto;
import com.nomadconnection.dapp.api.v2.service.scraping.FinancialStatementsService;
import com.nomadconnection.dapp.api.v2.service.scraping.FullTextService;
import com.nomadconnection.dapp.api.v2.utils.ScrapingCommonUtils;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.common.SignatureHistory;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.common.SignatureHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.*;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.shinhan.*;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.encryption.shinhan.Seed128;
import com.nomadconnection.dapp.secukeypad.EncryptParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nomadconnection.dapp.api.dto.Notification.SlackNotiDto.RecoveryNotiReq.getSlackRecoveryMessage;
import static com.nomadconnection.dapp.api.v2.utils.ScrapingCommonUtils.isScrapingSuccess;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssuanceService {

    private final UserRepository userRepository;
    private final D1200Repository d1200Repository;
    private final D1510Repository d1510Repository;
    private final D1520Repository d1520Repository;
    private final D1530Repository d1530Repository;
    private final D1000Repository d1000Repository;
    private final D1400Repository d1400Repository;
    private final D1100Repository d1100Repository;
    private final SignatureHistoryRepository signatureHistoryRepository;
    private final CardIssuanceInfoRepository cardIssuanceInfoRepository;

    private final ShinhanGwRpc shinhanGwRpc;
    private final CommonService issCommonService;
    private final AsyncService asyncService;
    private final CorpService corpService;
    private final EmailService emailService;
    private final FinancialStatementsService financialStatementsService;
    private final SlackNotiService slackNotiService;
    private final CardIssuanceInfoService cardIssuanceInfoService;
    private final SurveyService surveyService;
    private final FullTextService fullTextService;

    @Value("${mail.receipt.send-enable}")
    boolean sendReceiptEmailEnable;

    @Value("${encryption.seed128.enable}")
    private boolean ENC_SEED128_ENABLE;

    /**
     * ?????? ??????
     * 1200
     * 1510
     * 1520
     * - ???????????? ?????????: ?????? 2?????? 2?????????
     * - ????????????(???????????? ???): ?????? ????????? 1?????????, ??????????????????=N, ??????????????? ???????????? ????????? ??????
     * 1530
     * 1000/1400
     * 3000(????????? ????????????)
     * ????????? ????????????
     */
    @Transactional(noRollbackFor = Exception.class)
    public void issuance(Long userIdx, CardIssuanceDto.IssuanceReq request, Long signatureHistoryIdx) throws Exception {
        try {
            paramsLogging(request);
            request.setUserIdx(userIdx);
            CardIssuanceInfo cardIssuanceInfo = findCardIssuanceInfo(request.getCardIssuanceInfoIdx());
            Corp userCorp = corpService.getCorpByUserIdx(userIdx);
            encryptAndSaveD1100(userCorp.idx(), cardIssuanceInfo);

            fullTextService.updateEmployeesType(cardIssuanceInfo, cardIssuanceInfo.getFinancialConsumers().getOverFiveEmployees());

            // 1200(??????????????????????????????)
            CardType cardType = request.getCardType();
            DataPart1200 resultOfD1200 = proc1200(userCorp, cardType);
            saveSignatureHistory(signatureHistoryIdx, resultOfD1200);

            // 15xx ????????????
            proc15xx(userCorp, resultOfD1200.getD007(), resultOfD1200.getD008());

            // ??????(1000) or ??????(1400) ??????
            if ("Y".equals(resultOfD1200.getD003())) {
                proc1000(userCorp, resultOfD1200, cardType);         // 1000(??????-??????????????????????????????)
            } else if ("N".equals(resultOfD1200.getD003())) {
                proc1400(userCorp, resultOfD1200, cardType);         // 1400(??????-????????????????????????)
            } else {
                String msg = "d003 is not Y/N. resultOfD1200.getD003() = " + resultOfD1200.getD003();
                CommonUtil.throwBusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1200, msg);
            }

            // BRP ??????(?????????)
            asyncService.run(() -> procBpr(userCorp, resultOfD1200, userIdx, cardIssuanceInfo.cardType()));

            cardIssuanceInfoService
                .updateIssuanceStatusByApplicationDateAndNumber(resultOfD1200.getD007(), resultOfD1200.getD008(), cardType, IssuanceStatus.APPLY);
        } catch (Exception e){
            log.error("[issuance] issuance info error", e);
            throw e;
        }
    }

    public void updateCardIssuanceInfo(CardIssuanceInfo cardIssuanceInfo){


    }


    private void saveSignatureHistory(Long signatureHistoryIdx, DataPart1200 resultOfD1200) {
        SignatureHistory signatureHistory = getSignatureHistory(signatureHistoryIdx);
        signatureHistory.setApplicationDate(resultOfD1200.getD007());
        signatureHistory.setApplicationNum(resultOfD1200.getD008());
    }

    private void paramsLogging(CardIssuanceDto.IssuanceReq request) {
        log.debug("## request params : " + request.toString());
    }

    @Async
    void procBpr(Corp userCorp, DataPart1200 resultOfD1200, Long userIdx, CardType cardType) {
        try {
            Thread.sleep(5000L);
            if (proc3000(userCorp, resultOfD1200, userIdx, cardType)) {
                sendReceiptEmail(resultOfD1200, userCorp, cardType);
            }
        } catch (Exception e) {
            log.error("[procBpr] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    public void procBprByHand(DataPart1200 resultOfD1200, Long userIdx, int fileType) {
        try {
            DataPart3000 resultOfD3000 = proc3000(resultOfD1200, userIdx);                    // 3000(????????? ????????????)
            if ("Y".equals(resultOfD3000.getD001())) {
                procBrpTransferByHand(resultOfD3000, resultOfD1200.getD001(), userIdx, fileType);
            }
        } catch (Exception e) {
            log.error("[sendShinhanImage] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private void sendReceiptEmail(DataPart1200 resultOfD1200, Corp userCorp, CardType cardType) {
        if (!sendReceiptEmailEnable) {
            return;
        }

        CardIssuanceInfo cardIssuanceInfo = cardIssuanceInfoService.findTopByCorp(userCorp, cardType);
        D1100 d1100 = d1100Repository.findFirstByCardIssuanceInfoOrderByUpdatedAtDesc(cardIssuanceInfo).orElseThrow(
                () -> new SystemException(ErrorCode.External.INTERNAL_SERVER_ERROR,
                        "data of d1100 is not exist(corpIdx=" + userCorp.idx() + ")")
        );
        Map<String, String> issuanceCounts = new HashMap<>();
        issuanceCounts.put("counts", d1100.getD039());
        SurveyDto surveyResult = surveyService.findAnswerByUser(userCorp.user());

        if(CardType.GOWID.equals(cardType)){
            emailService.sendReceiptEmail(resultOfD1200.getD001(), issuanceCounts, CardCompany.SHINHAN, resultOfD1200.getD003(),
                surveyResult, d1100.getD033() + " " + d1100.getD034());
        } else if(CardType.KISED.equals(cardType)){
            emailService.sendKisedReceiptEmail(resultOfD1200.getD001(), d1100.getD039(), resultOfD1200.getD003(),
                surveyResult, d1100.getD033() + " " + d1100.getD034());
        }
        log.debug("## receipt email sent. biz no = " + resultOfD1200.getD001());
    }

    private boolean proc3000(Corp userCorp, DataPart1200 resultOfD1200, Long userIdx, CardType cardType) {
        DataPart3000 resultOfD3000 = proc3000(resultOfD1200, userIdx);                    // 3000(????????? ????????????)
        if ("Y".equals(resultOfD3000.getD001())) {
            procBrpTransfer(resultOfD3000, userCorp.resCompanyIdentityNo(), userIdx, cardType);     // ????????? ????????????
            return true;
        }
        return false;
    }

    private SignatureHistory getSignatureHistory(Long signatureHistoryIdx) {
        return signatureHistoryRepository.findById(signatureHistoryIdx).orElseThrow(
                () -> new SystemException(ErrorCode.External.INTERNAL_SERVER_ERROR,
                        "signatureHistory(" + signatureHistoryIdx + ") is not found")
        );
    }

    // 1100 ????????? ??????
    private void encryptAndSaveD1100(Long corpIdx, CardIssuanceInfo cardIssuanceInfo) {
        D1100 d1100 = d1100Repository.findFirstByCardIssuanceInfoOrderByUpdatedAtDesc(cardIssuanceInfo).orElseThrow(
                () -> new SystemException(ErrorCode.External.INTERNAL_ERROR_GW,
                        "data of d1100 is not exist(corpIdx=" + corpIdx + ")")
        );

        d1100.updateBankInfo(
            GowidUtils.get3digitsBankCode(cardIssuanceInfo.bankAccount().getBankCode()),
            Seed128.encryptEcb(cardIssuanceInfo.bankAccount().getBankAccount()),
            cardIssuanceInfo.bankAccount().getBankAccountHolder());
        d1100.setD040(cardIssuanceInfo.cardType().getRecommender());
        d1100.setD041(Const.ID_VERIFICATION_NO);
        d1100.setD044("Y");
        d1100.setD045("Y");
        d1100Repository.save(d1100);
    }

    private DataPart3000 proc3000(DataPart1200 resultOfD1200, Long idxUser) {
        // ?????????
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH3000);

        String mapCode = null;
        if ("Y".equals(resultOfD1200.getD003())) {
            mapCode = "02"; // ??????
        } else if ("N".equals(resultOfD1200.getD003())) {
            mapCode = "04"; // ????????????
        } else {
            String msg = "d003 is not Y/N. resultOfD1200.getD003() = " + resultOfD1200.getD003();
            CommonUtil.throwBusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_3000, msg);
        }

        // ????????????
        DataPart3000 requestRpc = new DataPart3000(mapCode, resultOfD1200.getD007() + resultOfD1200.getD008());
        BeanUtils.copyProperties(commonPart, requestRpc);

        // ?????? ??? ??????
        return shinhanGwRpc.request3000(requestRpc, idxUser);
    }

    private void procBrpTransfer(DataPart3000 resultOfD3000, String companyIdentityNo, Long idxUser, CardType cardType) {
        BprTransferReq requestRpc = new BprTransferReq(resultOfD3000);
        shinhanGwRpc.requestBprTransfer(requestRpc, companyIdentityNo, idxUser, cardType);
    }

    private void procBrpTransferByHand(DataPart3000 resultOfD3000, String licenseNo, Long idxUser, int fileType) {
        BprTransferReq requestRpc = new BprTransferReq(resultOfD3000);
        shinhanGwRpc.requestBprSingleTransfer(requestRpc, licenseNo, idxUser, fileType);
    }

    private DataPart1200 proc1200(Corp userCorp, CardType cardType) {
        CardIssuanceInfo cardIssuanceInfo = cardIssuanceInfoService.findByUserAndCardType(userCorp.user(), cardType);
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1200);
        D1200 d1200 = d1200Repository.findFirstByCardIssuanceInfoOrderByUpdatedAtDesc(cardIssuanceInfo).orElseGet(
            () -> D1200.builder().build()
        );

        d1200.setD001(CommonUtil.replaceHyphen(userCorp.resCompanyIdentityNo()));
        d1200.setD002(cardType.getCorpType());
        d1200.updateCardType(cardType);
        d1200.setIdxCorp(userCorp.idx());
        d1200.setCardIssuanceInfo(cardIssuanceInfo);

        // ??????
        DataPart1200 requestRpc = new DataPart1200();
        BeanUtils.copyProperties(d1200, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);
        DataPart1200 responseRpc = shinhanGwRpc.request1200(requestRpc, userCorp.user().idx());

        BeanUtils.copyProperties(responseRpc, d1200);
        d1200Repository.save(d1200);

        handleError1200(userCorp, cardType, responseRpc.getC009(), responseRpc.getC013());

        return responseRpc;
    }

    public void handleError1200(Corp corp, CardType cardType, String code, String message){
        final String ALREADY_UNDER_REVIEW = "03";

        if(Const.API_SHINHAN_RESULT_SUCCESS.equals(code)){
            return;
        } else if(ALREADY_UNDER_REVIEW.equals(code)){
            cardIssuanceInfoService.updateIssuanceStatus(corp, cardType, IssuanceStatus.EXISTING);
            throw new SystemException(ErrorCode.External.EXISTING_SHINHAN_1200, code + "/" + message);
        }

        throw new SystemException(ErrorCode.External.REJECTED_SHINHAN_1200, code + "/" + message);
    }

    private void proc15xx(Corp userCorp, String applyDate, String applyNo) throws Exception {
        // 1510(?????????????????? ?????????????????????)
        proc1510(userCorp, applyDate, applyNo);

        // 1520(???????????? ?????????????????????)
        proc1520(userCorp, applyDate, applyNo);

        // 1530(??????????????? ?????????????????????)
        proc1530(userCorp, applyDate, applyNo);
    }

    public void proc1510(Corp userCorp, String applyDate, String applyNo) {
        // ?????????
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1510);

        // ???????????? - db ??????, ??????
        D1510 d1510 = d1510Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(userCorp.idx());
        if (d1510 == null) {
            String msg = "data of d1510 is not exist(corpIdx=" + userCorp.idx() + ")";
            CommonUtil.throwBusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1510, msg);
            return;
        }

        // ????????????, ??????
        d1510.setD001(applyDate);
        d1510.setD002(applyNo);

        if (StringUtils.hasText(d1510.getD013()) && d1510.getD013().length() > 40) {
            d1510.setD013(d1510.getD013().substring(0, 40));
        }

        if (StringUtils.hasText(d1510.getD014()) && d1510.getD014().length() > 40) {
            d1510.setD014(d1510.getD014().substring(0, 40));
        }

        // ??????
        DataPart1510 requestRpc = new DataPart1510();
        BeanUtils.copyProperties(d1510, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        shinhanGwRpc.request1510(requestRpc, userCorp.user().idx());
    }

    public void proc1520(Corp userCorp, String applyDate, String applyNo) throws Exception {
        List<D1520> d1520s = d1520Repository.findTop2ByIdxCorpOrderByUpdatedAtDesc(userCorp.idx());
        if (CollectionUtils.isEmpty(d1520s)) {
            log.error("data of d1520 is not exist(corpIdx=" + userCorp.idx() + ")");
            ApiResponse.ApiResult response = financialStatementsService.scrap(userCorp.user(), ScrapingCommonUtils.DEFAULT_CLOSING_STANDARDS_MONTH);
            if(!isScrapingSuccess(response.getCode())){
                slackNotiService.sendSlackNotification(getSlackRecoveryMessage(userCorp, response), slackNotiService.getSlackRecoveryUrl());
                return;
            }
        d1520s = d1520Repository.findTop2ByIdxCorpOrderByUpdatedAtDesc(userCorp.idx());
        }

        for (D1520 d1520 : d1520s) {
            CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1520);

            // ????????????, ??????
            d1520.setD001(applyDate);
            d1520.setD002(applyNo);

            DataPart1520 requestRpc = new DataPart1520();
            BeanUtils.copyProperties(d1520, requestRpc);
            BeanUtils.copyProperties(commonPart, requestRpc);

            shinhanGwRpc.request1520(requestRpc, userCorp.user().idx());
        }
    }

    public void proc1530(Corp userCorp, String applyDate, String applyNo) {
        // ?????????
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1530);

        // ???????????? - db ??????, ??????
        D1530 d1530 = d1530Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(userCorp.idx()).orElseThrow(
                () -> CorpNotRegisteredException.builder().build()
        );

        d1530.setD001(applyDate);   // ????????????
        d1530.setD002(applyNo);     // ??????
        d1530.setD018(CommonUtil.cutString(d1530.getD018(), 10));   // ????????? ?????? ?????? 10??? ???

        // ??????????????????_?????? 10??? ???
        d1530.setD022(CommonUtil.cutString(d1530.getD022(), 10));
        d1530.setD024(CommonUtil.cutString(d1530.getD024(), 10));
        d1530.setD026(CommonUtil.cutString(d1530.getD026(), 10));
        d1530.setD028(CommonUtil.cutString(d1530.getD028(), 10));
        d1530.setD030(CommonUtil.cutString(d1530.getD030(), 10));
        d1530.setD032(CommonUtil.cutString(d1530.getD032(), 10));
        d1530.setD034(CommonUtil.cutString(d1530.getD034(), 10));
        d1530.setD036(CommonUtil.cutString(d1530.getD036(), 10));
        d1530.setD038(CommonUtil.cutString(d1530.getD038(), 10));
        d1530.setD040(CommonUtil.cutString(d1530.getD040(), 10));

        // ????????????????????????_????????????', '????????????????????????_???????????? ???????????? => "?????????????????????"??? ??????
        if (StringUtils.isEmpty(d1530.getD019())) {
            d1530.setD019(d1530.getD057());
        }
        if (StringUtils.isEmpty(d1530.getD020())) {
            d1530.setD020(d1530.getD057());
        }

        DataPart1530 requestRpc = new DataPart1530();
        BeanUtils.copyProperties(d1530, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        requestRpc.setD047(Seed128.decryptEcb(d1530.getD047()));
        if (!ObjectUtils.isEmpty(d1530.getD051())) {
            requestRpc.setD051(Seed128.decryptEcb(d1530.getD051()));
        }
        if (!ObjectUtils.isEmpty(d1530.getD055())) {
            requestRpc.setD055(Seed128.decryptEcb(d1530.getD055()));
        }

        // ????????????_??????1,2,3 10??? ???
        requestRpc.setD045(CommonUtil.cutString(requestRpc.getD045(), 10));
        requestRpc.setD049(CommonUtil.cutString(requestRpc.getD049(), 10));
        requestRpc.setD053(CommonUtil.cutString(requestRpc.getD053(), 10));

        // ?????? ??? ??????
        shinhanGwRpc.request1530(requestRpc, userCorp.user().idx());
    }

    //    private void proc1000(Corp userCorp, DataPart1200 resultOfD1200, HttpServletRequest httpServletRequest) {
    private void proc1000(Corp userCorp, DataPart1200 resultOfD1200, CardType cardType) throws Exception {
        CardIssuanceInfo cardIssuanceInfo = cardIssuanceInfoService.findByUserAndCardType(userCorp.user(), cardType);
        // ?????????
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1000);

        // ???????????? - db ??????, ??????
        D1000 d1000 = d1000Repository.findFirstByCardIssuanceInfoOrderByUpdatedAtDesc(cardIssuanceInfo).orElseThrow(
                () -> CorpNotRegisteredException.builder().build()
        );

        // ????????????, ??????
        d1000.setD071(resultOfD1200.getD007());
        d1000.setD072(resultOfD1200.getD008());

        // d39(???????????????????????????) => 02 ??? ????????????
        d1000.setD039("00");
        // d43(??????????????????????????????) => ???????????????
        d1000.setD043(userCorp.user().email());

        if (!StringUtils.hasText(d1000.getD059())) {
            d1000.setD059(d1000.getD010());
            d1000.setD060(d1000.getD012());
            d1000.setD062(d1000.getD013());
            d1000.setD065("00000");
            d1000.setD061(Seed128.decryptEcb(d1000.getD011()).substring(0, 6));
        }

        // ??????
        DataPart1000 requestRpc = new DataPart1000();
        BeanUtils.copyProperties(d1000, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        if (!ENC_SEED128_ENABLE) {
            requestRpc.setD011(Seed128.decryptEcb(d1000.getD011()));
            requestRpc.setD015(Seed128.decryptEcb(d1000.getD015()));
            requestRpc.setD019(Seed128.decryptEcb(d1000.getD019()));
            requestRpc.setD034(Seed128.decryptEcb(d1000.getD034()));
        }

        shinhanGwRpc.request1000(requestRpc, userCorp.user().idx());
    }

    private void proc1400(Corp userCorp, DataPart1200 resultOfD1200, CardType cardType) {
        CardIssuanceInfo cardIssuanceInfo = cardIssuanceInfoService.findByUserAndCardType(userCorp.user(), cardType);
        // ?????????
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1400);

        // ???????????? - db ??????, ??????
        D1400 d1400 = d1400Repository.findFirstByCardIssuanceInfoOrderByUpdatedAtDesc(cardIssuanceInfo).orElseThrow(
                () -> CorpNotRegisteredException.builder().build()
        );

        // ????????????, ??????
        d1400.setD025(resultOfD1200.getD007());
        d1400.setD026(resultOfD1200.getD008());

        d1400.setD061("00");                        // ??????????????????????????? => 02 ??? ????????????
        d1400.setD065(userCorp.user().email());     // ?????????????????????????????? => ???????????????

        if (!StringUtils.hasText(d1400.getD019())) {
            d1400.setD019(d1400.getD032());
            d1400.setD020(d1400.getD034());
            d1400.setD022(d1400.getD035());
            d1400.setD024("00000");
            d1400.setD021(Seed128.decryptEcb(d1400.getD033()).substring(0, 6));
        }

        // ??????
        DataPart1400 requestRpc = new DataPart1400();
        BeanUtils.copyProperties(d1400, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        if (!ENC_SEED128_ENABLE) {
            requestRpc.setD033(Seed128.decryptEcb(d1400.getD033()));        //???????????????????????????1
            requestRpc.setD037(Seed128.decryptEcb(d1400.getD037()));        //???????????????????????????2
            requestRpc.setD041(Seed128.decryptEcb(d1400.getD041()));        //???????????????????????????3
            requestRpc.setD056(Seed128.decryptEcb(d1400.getD056()));        //?????????????????????????????????
        }

        shinhanGwRpc.request1400(requestRpc, userCorp.user().idx());
    }

    public DataPart1700 proc1700(Long idxUser, CardIssuanceDto.IdentificationReq request, Map<String, String> decryptData) {
        // ?????????
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1700);

        // ??????
        DataPart1700 requestRpc = new DataPart1700();
        BeanUtils.copyProperties(commonPart, requestRpc);
        requestRpc.setD001(request.getIdentityType().getShinhanCode());
        requestRpc.setD002(request.getName().replace(" ", ""));
        requestRpc.setD004(request.getIssueDate());
        requestRpc.setD006(request.getDriverLocal());
        requestRpc.setD007(request.getDriverCode());

        String d003 = request.getIdentificationNumberFront() + decryptData.get(EncryptParam.IDENTIFICATION_NUMBER);
        String d005 = decryptData.get(EncryptParam.DRIVER_NUMBER);
        if (ENC_SEED128_ENABLE) {
            log.debug("## raw string : d003='{}', d005='{}'", d003, d005);
            if (StringUtils.hasText(d003)) {
                d003 = Seed128.encryptEcb(d003);
            }
            if (StringUtils.hasText(d005)) {
                d005 = Seed128.encryptEcb(d005);
            }
        }
        requestRpc.setD003(d003);
        requestRpc.setD005(d005);

        return shinhanGwRpc.request1700(requestRpc, idxUser);
    }

    public DataPart1710 proc1710(Long idxUser, KisedRequestDto dto){
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1710);
        DataPart1710 request = DataPart1710.of(dto.getLicenseNo(), dto.getProjectId());
        BeanUtils.copyProperties(commonPart, request);

        return shinhanGwRpc.request1710(request, idxUser);
    }


    private User findUser(Long idx_user) {
        return userRepository.findById(idx_user).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("User")
                        .idx(idx_user)
                        .build()
        );
    }

    private CardIssuanceInfo findCardIssuanceInfo(Long idx) {
        return cardIssuanceInfoRepository.findByIdx(idx).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("CardIssuanceInfo")
                        .build()
        );
    }

    /**
     * ???????????? ?????? ??? ??????
     * ????????? ????????????
     * ????????? ????????? + base64
     */
    @Transactional(noRollbackFor = Exception.class)
    public SignatureHistory verifySignedBinaryAndSave(Long userIdx, String signedBinaryString) {
        SignVerificationUtil.verifySignedBinaryString(signedBinaryString);

        User user = findUser(userIdx);
        SignatureHistory signatureHistory = SignatureHistory.builder()
                .userIdx(user.idx())
                .corpIdx(user.corp().idx())
                .signedBinaryString(signedBinaryString)
                .build();

        return signatureHistoryRepository.save(signatureHistory);
    }

    public DataPart1200 makeDataPart1200(Long corpIdx){
        D1200 d1200 = d1200Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(corpIdx).orElseGet(
            () -> D1200.builder().build()
        );
        DataPart1200 resultOfD1200 = new DataPart1200();
        BeanUtils.copyProperties(d1200, resultOfD1200);

        return resultOfD1200;
    }

}