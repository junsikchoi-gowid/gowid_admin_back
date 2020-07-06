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
import com.nomadconnection.dapp.api.service.rpc.ShinhanGwRpc;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.api.util.SignVerificationUtil;
import com.nomadconnection.dapp.core.domain.*;
import com.nomadconnection.dapp.core.domain.repository.SignatureHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.UserRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.*;
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

    private final ShinhanGwRpc shinhanGwRpc;
    private final CommonService issCommonService;
    private final AsyncService asyncService;

    @Value("${encryption.keypad.enable}")
    private boolean ENC_KEYPAD_ENABLE;

    @Value("${decryption.seed128.enable}")
    private boolean DEC_SEED128_ENABLE;

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
    public UserCorporationDto.IssuanceRes issuance(Long userIdx,
                                                   HttpServletRequest httpServletRequest,
                                                   UserCorporationDto.IssuanceReq request,
                                                   Long signatureHistoryIdx) {

        Corp userCorp = getCorpByUserIdx(userIdx);

        // 키패드 복호화(카드비번, 결제계좌) -> seed128 암호화 -> 1100 DB저장
        encryptAndSaveD1100(userCorp.idx(), httpServletRequest, request);

        // 1200(법인회원신규여부검증)
        DataPart1200 resultOfD1200 = proc1200(userCorp);
        SignatureHistory signatureHistory = getSignatureHistory(signatureHistoryIdx);
        signatureHistory.setApplicationDate(resultOfD1200.getD007());
        signatureHistory.setApplicationNum(resultOfD1200.getD008());

        // 15xx 서류제출
        proc15xx(userCorp, resultOfD1200.getD007(), resultOfD1200.getD008());

        // 신규(1000) or 변경(1400) 신청
        if ("Y".equals(resultOfD1200.getD003())) {
            proc1000(userCorp, resultOfD1200, httpServletRequest);         // 1000(신규-법인회원신규심사요청)
        } else if ("N".equals(resultOfD1200.getD003())) {
            proc1400(userCorp, resultOfD1200, httpServletRequest);         // 1400(기존-법인조건변경신청)
        } else {
            String msg = "d003 is not Y/N. resultOfD1200.getD003() = " + resultOfD1200.getD003();
            CommonUtil.throwBusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1200, msg);
        }

        // BRP 전송(비동기)
        asyncService.run(() -> procBpr(userCorp, resultOfD1200));

        return new UserCorporationDto.IssuanceRes();
    }

    private Corp getCorpByUserIdx(Long userIdx) {
        User user = findUser(userIdx);
        Corp userCorp = user.corp();
        if (userCorp == null) {
            log.error("not found corp. userIdx=" + userIdx);
            throw new BadRequestException(ErrorCode.Api.NOT_FOUND, "corp(userIdx=" + userIdx + ")");
        }
        return userCorp;
    }

    @Async
    void procBpr(Corp userCorp, DataPart1200 resultOfD1200) {
        if (proc3000(userCorp, resultOfD1200)) {
            return;
        }

        try {
            for (int i = 0; i < 3; i++) {
                Thread.sleep(5000L);
                if (proc3000(userCorp, resultOfD1200)) {
                    return;
                }
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new SystemException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_3000, e.getMessage());
        }

    }

    private boolean proc3000(Corp userCorp, DataPart1200 resultOfD1200) {
        DataPart3000 resultOfD3000 = proc3000(resultOfD1200);                    // 3000(이미지 제출여부)
        if ("Y".equals(resultOfD3000.getD001())) {
            procBrpTransfer(resultOfD3000, userCorp.resCompanyIdentityNo());     // 이미지 전송요청
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
    private void encryptAndSaveD1100(Long corpIdx, HttpServletRequest httpServletRequest, UserCorporationDto.IssuanceReq request) {
        D1100 d1100 = d1100Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(corpIdx).orElseThrow(
                () -> new SystemException(ErrorCode.External.INTERNAL_ERROR_GW,
                        "data of d1100 is not exist(corpIdx=" + corpIdx + ")")
        );

        d1100.setD021(Seed128.encryptEcb(request.getPayAccount()));
        String passwd = CommonUtil.getDecryptKeypad(httpServletRequest, EncryptParam.PASSWORD, ENC_KEYPAD_ENABLE);  // 키패드 암호화상태이면, 복호화함
        d1100.setD025(Seed128.encryptEcb(passwd));
    }

    private DataPart3000 proc3000(DataPart1200 resultOfD1200) {
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

        // todo : 테스트 데이터(삭제예정)
        requestRpc.setC009("00");

        // 요청 및 리턴
        return shinhanGwRpc.request3000(requestRpc);
    }

    private void procBrpTransfer(DataPart3000 resultOfD3000, String companyIdentityNo) {
        BprTransferReq requestRpc = new BprTransferReq(resultOfD3000);
        shinhanGwRpc.requestBprTransfer(requestRpc, companyIdentityNo);
    }

    DataPart1200 proc1200(Corp userCorp) {
        // 공통부
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1200);

        // 데이터부
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

        // todo : 테스트 데이터(삭제예정)
//        requestRpc.setC009("00");
//        requestRpc.setC013("성공이지롱");
//        requestRpc.setD003("Y");
//        requestRpc.setD003("N");
//        requestRpc.setD007(CommonUtil.getNowYYYYMMDD());
//        requestRpc.setD008(CommonUtil.getRandom5Num());

        issCommonService.saveGwTran(requestRpc);
        DataPart1200 responseRpc = shinhanGwRpc.request1200(requestRpc);
        issCommonService.saveGwTran(responseRpc);
        BeanUtils.copyProperties(responseRpc, d1200);

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

        // 연동
        DataPart1510 requestRpc = new DataPart1510();
        BeanUtils.copyProperties(d1510, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        // todo : 테스트 데이터(삭제예정)
//        requestRpc.setC009("00");

        issCommonService.saveGwTran(requestRpc);
        issCommonService.saveGwTran(shinhanGwRpc.request1510(requestRpc));
    }

    private void proc1520(Corp userCorp, String applyDate, String applyNo) {
        // 공통부
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1520);

        // 데이터부 - db 추출, 세팅
        List<D1520> d1520s = d1520Repository.findTop2ByIdxCorpOrderByUpdatedAtDesc(userCorp.idx());
        if (CollectionUtils.isEmpty(d1520s)) {
            String msg="data of d1520 is not exist(corpIdx="+userCorp.idx()+")";
            CommonUtil.throwBusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1520, msg);
        }

        // 연동
        for (D1520 d1520 : d1520s) {
            // 접수일자, 순번
            d1520.setD001(applyDate);
            d1520.setD002(applyNo);

            DataPart1520 requestRpc = new DataPart1520();
            BeanUtils.copyProperties(d1520, requestRpc);
            BeanUtils.copyProperties(commonPart, requestRpc);

            // todo : 테스트 데이터(삭제예정)
//            requestRpc.setC009("00");

            issCommonService.saveGwTran(requestRpc);
            issCommonService.saveGwTran(shinhanGwRpc.request1520(requestRpc));
        }
    }

    private void proc1530(Corp userCorp, String applyDate, String applyNo) {
        // 공통부
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1530);

        // 데이터부 - db 추출, 세팅
        D1530 d1530 = d1530Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(userCorp.idx());
        if (d1530 == null) {
            String msg="data of d1530 is not exist(corpIdx="+userCorp.idx()+")";
            CommonUtil.throwBusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1530, msg);
            return;
        }

        // 접수일자, 순번
        d1530.setD001(applyDate);
        d1530.setD002(applyNo);

        // 연동
        DataPart1530 requestRpc = new DataPart1530();
        BeanUtils.copyProperties(d1530, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        // todo : 테스트 데이터(삭제예정)
//        requestRpc.setC009("00");       // 성공리턴
//        requestRpc.setD007(requestRpc.getD007().substring(0, 5));  // 게이트웨이 길이 버그로인해 임시조치

        issCommonService.saveGwTran(requestRpc);
        issCommonService.saveGwTran(shinhanGwRpc.request1530(requestRpc));
    }

    private void proc1000(Corp userCorp, DataPart1200 resultOfD1200, HttpServletRequest httpServletRequest) {
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
        d1000.setD079(resultOfD1200.getD007());
        d1000.setD080(resultOfD1200.getD008());

        // 연동
        DataPart1000 requestRpc = new DataPart1000();
        BeanUtils.copyProperties(d1000, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        requestRpc.setD011(getDecKeyPadEncSeed128(EncryptParam.CEO_REGISTER_NO1, httpServletRequest));
        requestRpc.setD015(getDecKeyPadEncSeed128(EncryptParam.CEO_REGISTER_NO2, httpServletRequest));
        requestRpc.setD019(getDecKeyPadEncSeed128(EncryptParam.CEO_REGISTER_NO3, httpServletRequest));

        issCommonService.saveGwTran(requestRpc);
        issCommonService.saveGwTran(shinhanGwRpc.request1000(requestRpc));
    }

    // 키패드암호화 -> 복호화
    private String getDecKeyPadEncSeed128(String keypadEncParam, HttpServletRequest httpServletRequest) {
        String returnString = httpServletRequest.getParameter(keypadEncParam);
        log.debug("## keypad encrypted string : {}", returnString);

        if (ENC_KEYPAD_ENABLE) {
            returnString = CommonUtil.getDecryptKeypad(httpServletRequest, keypadEncParam);
            log.debug("## keypad decrypted string : {}", returnString);
        }
        if (DEC_SEED128_ENABLE) {
            returnString = Seed128.encryptEcb(returnString);
            log.debug("## seed128 encrypted string : {}", returnString);
        }
        return returnString;
    }

    private void proc1400(Corp userCorp, DataPart1200 resultOfD1200, HttpServletRequest httpServletRequest) {
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
        d1400.setD033(resultOfD1200.getD007());
        d1400.setD034(resultOfD1200.getD008());

        // 연동
        DataPart1400 requestRpc = new DataPart1400();
        BeanUtils.copyProperties(d1400, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        requestRpc.setD006(getDecKeyPadEncSeed128(EncryptParam.CEO_REGISTER_NO1, httpServletRequest));

        issCommonService.saveGwTran(requestRpc);
        issCommonService.saveGwTran(shinhanGwRpc.request1400(requestRpc));
    }


    private DataPart1700 proc1700(UserCorporationDto.IdentificationReq request, Map<String, String> decryptData) {
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

		requestRpc.setD003(request.getIdentificationNumberFront() + decryptData.get(EncryptParam.IDENTIFICATION_NUMBER));
		requestRpc.setD005(decryptData.get(EncryptParam.DRIVER_NUMBER));
//		requestRpc.setD003(Seed128.encryptEcb(request.getIdentificationNumberFront() + decryptData.get(EncryptParam.IDENTIFICATION_NUMBER))); todo: 운영환경에서는 보내기전 암호화
//		requestRpc.setD005(Seed128.encryptEcb(decryptData.get(EncryptParam.DRIVER_NUMBER)));

        issCommonService.saveGwTran(requestRpc);
        DataPart1700 responseRpc = shinhanGwRpc.request1700(requestRpc);
        issCommonService.saveGwTran(responseRpc);

        return responseRpc;
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
    public void verifyCeoIdentification(HttpServletRequest request, UserCorporationDto.IdentificationReq dto) {

        Map<String, String> decryptData;
        if (dto.getIdType().equals(UserCorporationDto.IdentificationReq.IDType.DRIVE_LICENCE)) {
            decryptData = SecuKeypad.decrypt(request, "encryptData", new String[]{EncryptParam.IDENTIFICATION_NUMBER, EncryptParam.DRIVER_NUMBER});
        } else {
            decryptData = SecuKeypad.decrypt(request, "encryptData", new String[]{EncryptParam.IDENTIFICATION_NUMBER});
        }

        // 1700(신분증검증)
        DataPart1700 resultOfD1700 = proc1700(dto, decryptData);

        if (!resultOfD1700.getD008().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw BadRequestedException.builder().category(BadRequestedException.Category.INVALID_CEO_IDENTIFICATION).desc(resultOfD1700.getD009()).build();
        }
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
                .corpIdx(user.idx())
                .userIdx(user.corp().idx())
                .signedBinaryString(signedBinaryString)
                .build();

        return signatureHistoryRepository.save(signatureHistory);
    }


}
