package com.nomadconnection.dapp.api.service.shinhan;

import com.nomadconnection.dapp.api.common.AsyncService;
import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.UserCorporationDto;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.*;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.enums.ShinhanGwApiType;
import com.nomadconnection.dapp.api.exception.BadRequestedException;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.exception.api.SystemException;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.api.service.rpc.ShinhanGwRpc;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.api.util.SignVerificationUtil;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.common.CommonCodeDetailRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.*;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.shinhan.*;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.encryption.Seed128;
import com.nomadconnection.dapp.secukeypad.EncryptParam;
import com.nomadconnection.dapp.secukeypad.SecuKeypad;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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
    private final CommonCodeDetailRepository commonCodeDetailRepository;
    private final IssuanceProgressRepository issuanceProgressRepository;

    private final ShinhanGwRpc shinhanGwRpc;
    private final CommonService issCommonService;
    private final AsyncService asyncService;
    private final UserService userService;

    @Value("${encryption.keypad.enable}")
    private boolean ENC_KEYPAD_ENABLE;

    @Value("${encryption.seed128.enable}")
    private boolean ENC_SEED128_ENABLE;

    /**
     * 카드 신청
     * 1200
     * 1510
     * 1520
     * - 재무제표 보유시: 최대 2년치 2회연동
     * - 미보유시(신설업체 등): 최근 데이터 1회연동, 발급가능여부=N, 실설업체는 재무제표 이미지 없음
     * 1530
     * 1000/1400
     * 3000(이미지 제출여부)
     * 이미지 전송요청
     */
    @Transactional(noRollbackFor = Exception.class)
    public void issuance(Long userIdx,
                         UserCorporationDto.IssuanceReq request,
                         Long signatureHistoryIdx) {
        paramsLogging(request);
        request.setUserIdx(userIdx);
        userService.saveIssuanceProgFailed(userIdx, IssuanceProgressType.SIGNED);
        Corp userCorp = getCorpByUserIdx(userIdx);
        encryptAndSaveD1100(userCorp.idx(), request);
        userService.saveIssuanceProgSuccess(userIdx, IssuanceProgressType.SIGNED);

        // 1200(법인회원신규여부검증)
        userService.saveIssuanceProgFailed(userIdx, IssuanceProgressType.P_1200);
        DataPart1200 resultOfD1200 = proc1200(userCorp);
        saveSignatureHistory(signatureHistoryIdx, resultOfD1200);
        userService.saveIssuanceProgSuccess(userIdx, IssuanceProgressType.P_1200);


        // 15xx 서류제출
        userService.saveIssuanceProgFailed(userIdx, IssuanceProgressType.P_15XX);
        proc15xx(userCorp, resultOfD1200.getD007(), resultOfD1200.getD008());
        userService.saveIssuanceProgSuccess(userIdx, IssuanceProgressType.P_15XX);

        // 신규(1000) or 변경(1400) 신청
        userService.saveIssuanceProgFailed(userIdx, IssuanceProgressType.P_AUTO_CHECK);
        if ("Y".equals(resultOfD1200.getD003())) {
            proc1000(userCorp, resultOfD1200);         // 1000(신규-법인회원신규심사요청)
        } else if ("N".equals(resultOfD1200.getD003())) {
            proc1400(userCorp, resultOfD1200);         // 1400(기존-법인조건변경신청)
        } else {
            String msg = "d003 is not Y/N. resultOfD1200.getD003() = " + resultOfD1200.getD003();
            CommonUtil.throwBusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1200, msg);
        }
        userService.saveIssuanceProgSuccess(userIdx, IssuanceProgressType.P_AUTO_CHECK);
        issuanceProgressRepository.flush();
        // BRP 전송(비동기)
        asyncService.run(() -> procBpr(userCorp, resultOfD1200, userIdx));

    }

    private void saveSignatureHistory(Long signatureHistoryIdx, DataPart1200 resultOfD1200) {
        SignatureHistory signatureHistory = getSignatureHistory(signatureHistoryIdx);
        signatureHistory.setApplicationDate(resultOfD1200.getD007());
        signatureHistory.setApplicationNum(resultOfD1200.getD008());
    }

    private CardIssuanceInfo getCardIssuanceInfo(UserCorporationDto.IssuanceReq request) {
        return cardIssuanceInfoRepository.findByIdx(request.getCardIssuanceInfoIdx()).orElseThrow(
                () -> new SystemException(ErrorCode.External.INTERNAL_ERROR_GW,
                        "CardIssuanceInfo is not exist(idx=" + request.getCardIssuanceInfoIdx() + ")")
        );
    }

    private void paramsLogging(UserCorporationDto.IssuanceReq request) {
        log.debug("## request params : " + request.toString());
    }

    private Corp getCorpByUserIdx(Long userIdx) {
        User user = findUser(userIdx);
        Corp userCorp = user.corp();
        if (userCorp == null) {
            userService.saveIssuanceProgFailed(userIdx, IssuanceProgressType.SIGNED);
            log.error("not found corp. userIdx=" + userIdx);
            throw new BadRequestException(ErrorCode.Api.NOT_FOUND, "corp(userIdx=" + userIdx + ")");
        }
        return userCorp;
    }

    @Async
    void procBpr(Corp userCorp, DataPart1200 resultOfD1200, Long userIdx) {
        userService.saveIssuanceProgFailed(userIdx, IssuanceProgressType.P_IMG);

        if (proc3000(userCorp, resultOfD1200, userIdx)) {
            userService.saveIssuanceProgSuccess(userIdx, IssuanceProgressType.P_IMG);
            return;
        }

        try {
            for (int i = 0; i < 3; i++) {
                Thread.sleep(5000L);
                if (proc3000(userCorp, resultOfD1200, userIdx)) {
                    userService.saveIssuanceProgSuccess(userIdx, IssuanceProgressType.P_IMG);
                    return;
                }
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new SystemException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_3000, e.getMessage());
        }

    }

    private boolean proc3000(Corp userCorp, DataPart1200 resultOfD1200, Long userIdx) {
        DataPart3000 resultOfD3000 = proc3000(resultOfD1200, userIdx);                    // 3000(이미지 제출여부)
        if ("Y".equals(resultOfD3000.getD001())) {
            procBrpTransfer(resultOfD3000, userCorp.resCompanyIdentityNo(), userIdx);     // 이미지 전송요청
            return true;
        }
        return false;
    }

    public SignatureHistory getSignatureHistory(Long signatureHistoryIdx) {
        return signatureHistoryRepository.findById(signatureHistoryIdx).orElseThrow(
                () -> new SystemException(ErrorCode.External.INTERNAL_SERVER_ERROR,
                        "signatureHistory(" + signatureHistoryIdx + ") is not found")
        );
    }

    // 1100 데이터 저장
    private void encryptAndSaveD1100(Long corpIdx, UserCorporationDto.IssuanceReq request) {
        D1100 d1100 = d1100Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(corpIdx).orElseThrow(
                () -> new SystemException(ErrorCode.External.INTERNAL_ERROR_GW,
                        "data of d1100 is not exist(corpIdx=" + corpIdx + ")")
        );

        CardIssuanceInfo cardIssuanceInfo = getCardIssuanceInfo(request);
        d1100.setD025(Seed128.encryptEcb(cardIssuanceInfo.bankAccount().getBankAccount()));
        d1100.setD040(Const.ID_VERIFICATION_NO);
        d1100.setD041(Const.ID_VERIFICATION_NO);
        d1100.setD044("Y");
        d1100.setD045("Y");
        d1100Repository.save(d1100);
    }

    private DataPart3000 proc3000(DataPart1200 resultOfD1200, Long idxUser) {
        // 공통부
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH3000);

        String mapCode = null;
        if ("Y".equals(resultOfD1200.getD003())) {
            mapCode = "02"; // 신규
        } else if ("N".equals(resultOfD1200.getD003())) {
            mapCode = "04"; // 조건변경
        } else {
            String msg = "d003 is not Y/N. resultOfD1200.getD003() = " + resultOfD1200.getD003();
            CommonUtil.throwBusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_3000, msg);
        }

        // 데이터부
        DataPart3000 requestRpc = new DataPart3000(mapCode, resultOfD1200.getD007() + resultOfD1200.getD008());
        BeanUtils.copyProperties(commonPart, requestRpc);

        // 요청 및 리턴
        return shinhanGwRpc.request3000(requestRpc, idxUser);
    }

    private void procBrpTransfer(DataPart3000 resultOfD3000, String companyIdentityNo, Long idxUser) {
        BprTransferReq requestRpc = new BprTransferReq(resultOfD3000);
        shinhanGwRpc.requestBprTransfer(requestRpc, companyIdentityNo, idxUser);
    }

    DataPart1200 proc1200(Corp userCorp) {
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1200);
        D1200 d1200 = d1200Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(userCorp.idx());
        if (d1200 == null) {
            d1200 = new D1200();
        }
        d1200.setD001(userCorp.resCompanyIdentityNo().replaceAll("-", ""));
        d1200.setD002(Const.D1200_MEMBER_TYPE_CODE);
        d1200.setIdxCorp(userCorp.idx());

        // 연동
        DataPart1200 requestRpc = new DataPart1200();
        BeanUtils.copyProperties(d1200, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);
        DataPart1200 responseRpc = shinhanGwRpc.request1200(requestRpc, userCorp.user().idx());

        BeanUtils.copyProperties(responseRpc, d1200);
        d1200Repository.save(d1200);

        return responseRpc;
    }

    private void proc15xx(Corp userCorp, String applyDate, String applyNo) {
        // 1510(사업자등록증 스크래핑데이터)
        proc1510(userCorp, applyDate, applyNo);

        // 1520(재무제표 스크래핑데이터)
        proc1520(userCorp, applyDate, applyNo);

        // 1530(등기부등본 스크래핑데이터)
        proc1530(userCorp, applyDate, applyNo);
    }

    private void proc1510(Corp userCorp, String applyDate, String applyNo) {
        // 공통부
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1510);

        // 데이터부 - db 추출, 세팅
        D1510 d1510 = d1510Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(userCorp.idx());
        if (d1510 == null) {
            String msg="data of d1510 is not exist(corpIdx="+userCorp.idx()+")";
            CommonUtil.throwBusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1510, msg);
            return;
        }

        // 접수일자, 순번
        d1510.setD001(applyDate);
        d1510.setD002(applyNo);

        if (StringUtils.hasText(d1510.getD013()) && d1510.getD013().length() > 40) {
            d1510.setD013(d1510.getD013().substring(0, 40));
        }

        if (StringUtils.hasText(d1510.getD014()) && d1510.getD014().length() > 40) {
            d1510.setD014(d1510.getD014().substring(0, 40));
        }

        // 연동
        DataPart1510 requestRpc = new DataPart1510();
        BeanUtils.copyProperties(d1510, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        shinhanGwRpc.request1510(requestRpc, userCorp.user().idx());
    }

    private void proc1520(Corp userCorp, String applyDate, String applyNo) {

        List<D1520> d1520s = d1520Repository.findTop2ByIdxCorpOrderByUpdatedAtDesc(userCorp.idx());
        if (CollectionUtils.isEmpty(d1520s)) {
            String msg="data of d1520 is not exist(corpIdx="+userCorp.idx()+")";
            CommonUtil.throwBusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1520, msg);
        }

        for (D1520 d1520 : d1520s) {
            CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1520);

            // 접수일자, 순번
            d1520.setD001(applyDate);
            d1520.setD002(applyNo);

            DataPart1520 requestRpc = new DataPart1520();
            BeanUtils.copyProperties(d1520, requestRpc);
            BeanUtils.copyProperties(commonPart, requestRpc);

            shinhanGwRpc.request1520(requestRpc, userCorp.user().idx());
        }
    }

    private void proc1530(Corp userCorp, String applyDate, String applyNo) {
        // 공통부
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1530);

        // 데이터부 - db 추출, 세팅
        D1530 d1530 = d1530Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(userCorp.idx());
        if (d1530 == null) {
            String msg = "data of d1530 is not exist(corpIdx=" + userCorp.idx() + ")";
            CommonUtil.throwBusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1530, msg);
            return;
        }

        // 접수일자, 순번
        d1530.setD001(applyDate);
        d1530.setD002(applyNo);

        // 발행할주식의총수_변경일자', '발행할주식의총수_등기일자 빈값일때 => "법인성립연월일"로 입력
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

        // 연동 및 저장
        shinhanGwRpc.request1530(requestRpc, userCorp.user().idx());
    }

    //    private void proc1000(Corp userCorp, DataPart1200 resultOfD1200, HttpServletRequest httpServletRequest) {
    private void proc1000(Corp userCorp, DataPart1200 resultOfD1200) {
        // 공통부
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1000);

        // 데이터부 - db 추출, 세팅
        D1000 d1000 = d1000Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(userCorp.idx());
        if (d1000 == null) {
            String msg = "data of d1000 is not exist(corpIdx=" + userCorp.idx() + ")";
            CommonUtil.throwBusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1000, msg);
            return;
        }

        // 접수일자, 순번
        d1000.setD071(resultOfD1200.getD007());
        d1000.setD072(resultOfD1200.getD008());

        // d39(신청관리자내선번호) => 02 로 하드코딩
        d1000.setD039("00");
        // d43(신청관리자이메일주소) => 사용자계정
        d1000.setD043(userCorp.user().email());

        // 연동
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

    //    private void proc1400(Corp userCorp, DataPart1200 resultOfD1200, HttpServletRequest httpServletRequest) {
    private void proc1400(Corp userCorp, DataPart1200 resultOfD1200) {
        // 공통부
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1400);

        // 데이터부 - db 추출, 세팅
        D1400 d1400 = d1400Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(userCorp.idx());
        if (d1400 == null) {
            String msg = "data of d1400 is not exist(corpIdx=" + userCorp.idx() + ")";
            CommonUtil.throwBusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1400, msg);
            return;
        }

        // 접수일자, 순번
        d1400.setD025(resultOfD1200.getD007());
        d1400.setD026(resultOfD1200.getD008());

        d1400.setD062("00");                        // 신청관리자내선번호 => 02 로 하드코딩
        d1400.setD066(userCorp.user().email());     // 신청관리자이메일주소 => 사용자계정

        // 연동
        DataPart1400 requestRpc = new DataPart1400();
        BeanUtils.copyProperties(d1400, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        if (!ENC_SEED128_ENABLE) {
            //requestRpc.setD006(Seed128.decryptEcb(d1400.getD006()));
            requestRpc.setD034(Seed128.decryptEcb(d1400.getD034()));    //대표자주민등록번호1
            requestRpc.setD038(Seed128.decryptEcb(d1400.getD038()));        //대표자주민등록번호2
            requestRpc.setD042(Seed128.decryptEcb(d1400.getD042()));        //대표자주민등록번호3
            requestRpc.setD057(Seed128.decryptEcb(d1400.getD057()));        //신청관리자주민등록번호
        }

        shinhanGwRpc.request1400(requestRpc, userCorp.user().idx());
    }


    private DataPart1700 proc1700(Long idxUser, UserCorporationDto.IdentificationReq request, Map<String, String> decryptData) {
        // 공통부
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1700);

        // 연동
        DataPart1700 requestRpc = new DataPart1700();
        BeanUtils.copyProperties(commonPart, requestRpc);
        requestRpc.setD001(request.getIdCode());
        requestRpc.setD002(request.getKorName());
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


    private User findUser(Long idx_user) {
        return userRepository.findById(idx_user).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("User")
                        .idx(idx_user)
                        .build()
        );
    }

    /**
     * 카드 신청
     * <p>
     * 1700 신분증 위조확인
     */
    @Transactional(rollbackFor = Exception.class)
    public void verifyCeoIdentification(HttpServletRequest request, Long idxUser, UserCorporationDto.IdentificationReq dto) {
        Map<String, String> decryptData;
        if (dto.getIdType().equals(UserCorporationDto.IdentificationReq.IDType.DRIVE_LICENCE)) {
            decryptData = SecuKeypad.decrypt(request, "encryptData", new String[]{EncryptParam.IDENTIFICATION_NUMBER, EncryptParam.DRIVER_NUMBER});
            dto.setDriverLocal(findShinhanDriverLocalCode(dto.getDriverLocal()));
        } else {
            decryptData = SecuKeypad.decrypt(request, "encryptData", new String[]{EncryptParam.IDENTIFICATION_NUMBER});
        }

        // 1700(신분증검증)
        DataPart1700 resultOfD1700 = proc1700(idxUser, dto, decryptData);

        if (!resultOfD1700.getD008().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw BadRequestedException.builder().category(BadRequestedException.Category.INVALID_CEO_IDENTIFICATION).desc(resultOfD1700.getD009()).build();
        }

        save1400(dto, decryptData);
        save1000(dto, decryptData);
    }

    // 1400 테이블에 대표자 주민번호 저장
    private void save1400(UserCorporationDto.IdentificationReq dto, Map<String, String> decryptData) {
        if (!"1".equals(dto.getCeoSeqNo())) {
            return;
        }

        CardIssuanceInfo cardIssuanceInfo = getCardIssuanceInfo(dto);
        D1400 d1400 = d1400Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(cardIssuanceInfo.corp().idx());
        if (d1400 == null) {
            String msg = "data of d1400 is not exist(cardIssuanceInfo.idx =" + cardIssuanceInfo.idx() + ")";
            throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, msg);
        }
        String idNum = dto.getIdentificationNumberFront() + decryptData.get(EncryptParam.IDENTIFICATION_NUMBER);
        d1400.setD006(Seed128.encryptEcb(idNum));
        d1400Repository.save(d1400);
    }

    // 1000 테이블에 대표자1,2,3 주민번호 저장(d11,15,19)
    private void save1000(UserCorporationDto.IdentificationReq dto, Map<String, String> decryptData) {
        CardIssuanceInfo cardIssuanceInfo = getCardIssuanceInfo(dto);
        D1000 d1000 = d1000Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(cardIssuanceInfo.corp().idx());
        if (d1000 == null) {
            String msg = "data of d1000 is not exist(cardIssuanceInfo.idx =" + cardIssuanceInfo.idx() + ")";
            throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, msg);
        }
        String idNum = dto.getIdentificationNumberFront() + decryptData.get(EncryptParam.IDENTIFICATION_NUMBER);
        idNum = Seed128.encryptEcb(idNum);

        if ("1".equals(dto.getCeoSeqNo())) {
            d1000.setD011(idNum);
            d1000.setD034(idNum);
        } else if ("2".equals(dto.getCeoSeqNo())) {
            d1000.setD015(idNum);
        } else if ("3".equals(dto.getCeoSeqNo())) {
            d1000.setD019(idNum);
        } else {
            log.error("invalid ceoSeqNo. ceoSeqNo=" + dto.getCeoSeqNo());
            throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, "invalid ceoSeqNo. ceoSeqNo=" + dto.getCeoSeqNo());
        }

        d1000Repository.save(d1000);
    }

    private CardIssuanceInfo getCardIssuanceInfo(UserCorporationDto.IdentificationReq dto) {
        return cardIssuanceInfoRepository.findByIdx(dto.getCardIssuanceInfoIdx()).orElseThrow(
                () -> new SystemException(ErrorCode.External.INTERNAL_ERROR_GW,
                        "CardIssuanceInfo is not exist(idx=" + dto.getCardIssuanceInfoIdx() + ")")
        );
    }

    /**
     * 전자서명 검증 및 저장
     * 저장은 바이너리
     * 전송시 평문화 + base64
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

    private String findShinhanDriverLocalCode(String code) {
        return commonCodeDetailRepository.findFirstByValue1OrValue2AndCode(code, code, CommonCodeType.SHINHAN_DRIVER_LOCAL_CODE).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("CommonCodeDetail")
                        .build()
        ).code1();
    }
}