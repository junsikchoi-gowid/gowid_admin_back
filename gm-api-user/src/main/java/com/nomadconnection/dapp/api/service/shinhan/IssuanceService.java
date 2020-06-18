package com.nomadconnection.dapp.api.service.shinhan;

import com.nomadconnection.dapp.api.common.AsyncService;
import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.UserCorporationDto;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.*;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.enums.ShinhanGwApiType;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.gateway.InternalErrorException;
import com.nomadconnection.dapp.api.service.rpc.ShinhanGwRpc;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.*;
import com.nomadconnection.dapp.core.domain.repository.UserRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.*;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.secukeypad.EncryptParam;
import com.nomadconnection.dapp.secukeypad.SecuKeypad;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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

    private final GatewayTransactionIdxRepository gatewayTransactionIdxRepository;
    private final UserRepository userRepository;

    private final D1200Repository d1200Repository;
    private final D1510Repository d1510Repository;
    private final D1520Repository d1520Repository;
    private final D1530Repository d1530Repository;
    private final D1000Repository d1000Repository;
    private final D1400Repository d1400Repository;
    private final D1100Repository d1100Repository;
    private final ShinhanGwRpc shinhanGwRpc;
    private final AsyncService asyncService;


    /**
     * 카드 신청
     * <p>
     * 1700 신분증 위조확인
     */
    public void verifyCeoIdentification(HttpServletRequest request, UserCorporationDto.IdentificationReq dto) {

        Map<String, String> decryptData = SecuKeypad.decrypt(request, "encryptData", new String[]{EncryptParam.IDENTIFICATION_NUMBER, EncryptParam.DRIVER_NUMBER});

        // 1700(신분증검증)
        DataPart1700 resultOfD1700 = proc1700(dto, decryptData);

//        if (!resultOfD1700.getD008().equals("")) { // TODO : 결과값 확인
//            throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1700, resultOfD1700.getD009());
//        }
    }

    /**
     * 카드 신청
     * 1200
     * 1510
     * 1520
     * - 재무제표 보유시: 최대 2년치 2회연동
     * - 미보유시(신설업체 등): 최근 데이터 1회연동, 발급가능여부=N, 실설업체는 재무제표 이미지 없음
     * 1530
     * 1000/1400
     */
    @Transactional(rollbackFor = Exception.class)
    public UserCorporationDto.IssuanceRes issuance(Long userIdx, UserCorporationDto.IssuanceReq request) {

        User user = findUser(userIdx);
        Corp userCorp = user.corp();
        if (userCorp == null) {
            throw new EntityNotFoundException("not found userIdx", "corp", userIdx);
        }

        // 1200(법인회원신규여부검증)
        DataPart1200 resultOfD1200 = proc1200(userCorp);

        // 15X0(서류제출)
        proc15xx(userCorp, resultOfD1200.getD007(), resultOfD1200.getD008());

        if ("Y".equals(resultOfD1200.getD003())) {
            proc1000(userCorp, resultOfD1200, request);         // 1000(신규-법인회원신규심사요청)
        } else if ("N".equals(resultOfD1200.getD003())) {
            proc1400(userCorp, resultOfD1200, request);         // 1400(기존-법인조건변경신청)
        } else {
            String msg = "d003 is not Y/N. resultOfD1200.getD003() = " + resultOfD1200.getD003();
            CommonUtil.throwBusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1200, msg);
        }

        // 성공시 Body 는 공백으로.
        return new UserCorporationDto.IssuanceRes();
    }



    private DataPart1200 proc1200(Corp userCorp) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1200);

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
        requestRpc.setC009("00");
//        requestRpc.setD003("Y");
        requestRpc.setD003("N");
        requestRpc.setD007(CommonUtil.getNowYYYYMMDD());
        requestRpc.setD008(CommonUtil.getRandom5Num());

        DataPart1200 resultOfD1200 = shinhanGwRpc.request1200(requestRpc);
        BeanUtils.copyProperties(resultOfD1200, d1200);

        return shinhanGwRpc.request1200(requestRpc);
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
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1510);

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
        requestRpc.setC009("00");

        shinhanGwRpc.request1510(requestRpc);
    }

    private void proc1520(Corp userCorp, String applyDate, String applyNo) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1520);

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
            requestRpc.setC009("00");

            shinhanGwRpc.request1520(requestRpc);
        }
    }

    private void proc1530(Corp userCorp, String applyDate, String applyNo) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1530);

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
        requestRpc.setC009("00");       // 성공리턴
        requestRpc.setD007(requestRpc.getD007().substring(0, 5));  // 게이트웨이 길이 버그로인해 임시조치

        shinhanGwRpc.request1530(requestRpc);
    }

    private void proc1000(Corp userCorp, DataPart1200 resultOfD1200, UserCorporationDto.IssuanceReq request) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1000);

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
        requestRpc.setD011(request.getCeoRegisterNo1());
        requestRpc.setD015(request.getCeoRegisterNo2());
        requestRpc.setD019(request.getCeoRegisterNo3());

        requestRpc.setC009("00"); // todo : 테스트 데이터(삭제예정). 응답코드 성공

        shinhanGwRpc.request1000(requestRpc);
    }

    private void proc1400(Corp userCorp, DataPart1200 resultOfD1200, UserCorporationDto.IssuanceReq request) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1400);

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
        requestRpc.setD006(request.getCeoRegisterNo1());

        // todo : 테스트 데이터(삭제예정)
        requestRpc.setC009("00");

        shinhanGwRpc.request1400(requestRpc);
    }

    // 1600(신청재개) 수신 후, 1100(법인카드 신청) 진행
    // todo : 에러 및 실패처리
    public UserCorporationDto.ResumeRes resumeApplication(UserCorporationDto.ResumeReq request) {
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1600);
        UserCorporationDto.ResumeRes response = new UserCorporationDto.ResumeRes();
        BeanUtils.copyProperties(commonPart, response);

        // 1100(법인카드신청), 비동기 처리
        asyncService.run(() -> proc1100(request));

        return response;
    }

    // todo :
    //  - 비번, 결제계좌번호 취득 방안 확인
    //  - 비동기 안되는 문제 해결
    @Async
    public void proc1100(UserCorporationDto.ResumeReq request) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1100);

        // corpIdx 추출
        Long corpIdx = getCorpIdxFromLastRequest(request);

        // 데이터부 - db 추출, 세팅
        D1100 d1100 = d1100Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(corpIdx).orElseThrow(
                () -> new InternalErrorException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1100,
                        "data of d1100 is not exist(corpIdx=" + corpIdx + ")")
        );

        // 연동
        DataPart1100 requestRpc = new DataPart1100();
        BeanUtils.copyProperties(d1100, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        // todo 내부 테스트 데이터 삭제예정
        requestRpc.setD021("0000");             // 비번
        requestRpc.setD025("12312123456");      // 결제계좌번호
        requestRpc.setD016(requestRpc.getD016().substring(0, 5));    // 게이트웨이 길이 버그로 인해 조치

        shinhanGwRpc.request1100(requestRpc);
    }

    // 기존 1400/1000 연동으로 부터 법인 식별자 추출
    private Long getCorpIdxFromLastRequest(UserCorporationDto.ResumeReq request) {
        Long corpIdx;

        D1400 d1400 = d1400Repository.findFirstByD033AndD034OrderByUpdatedAtDesc(request.getD001(), request.getD002());
        if (ObjectUtils.isEmpty(d1400)) {
            D1000 d1000 = d1000Repository.findFirstByD079AndD080OrderByUpdatedAtDesc(request.getD001(), request.getD002());
            corpIdx = d1000.getIdxCorp();
        } else {
            corpIdx = d1400.getIdxCorp();
        }

        // todo : 게이트웨이로 에러리턴 수정
        if (StringUtils.isEmpty(corpIdx)) {
            String msg = "not fount applyNo[" + request.getD001() + "], applyDate[" + request.getD002() + "]";
            throw new InternalErrorException(ErrorCode.External.INTERNAL_SERVER_ERROR, msg);
        }

        return corpIdx;
    }

    private DataPart1700 proc1700(UserCorporationDto.IdentificationReq request, Map<String, String> decryptData) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1700);

        // 연동
        DataPart1700 requestRpc = new DataPart1700();
        BeanUtils.copyProperties(commonPart, requestRpc);
        requestRpc.setD001(request.getIdCode());
        requestRpc.setD002(request.getKorName());
        requestRpc.setD003(request.getIdentificationNumberFront() + decryptData.get(EncryptParam.IDENTIFICATION_NUMBER));
        requestRpc.setD004(request.getIssueDate());
        requestRpc.setD005(decryptData.get(EncryptParam.DRIVER_NUMBER));
        requestRpc.setD006(request.getDriverLocal());
        requestRpc.setD007(request.getDriverCode());

        return shinhanGwRpc.request1700(requestRpc);
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

        long tmpTranId = 20000000000L + gatewayTransactionIdx.getIdx();
        return "0" + tmpTranId;     // 020000000001
    }

    private User findUser(Long idx_user) {
        return userRepository.findById(idx_user).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("User")
                        .idx(idx_user)
                        .build()
        );
    }

}
