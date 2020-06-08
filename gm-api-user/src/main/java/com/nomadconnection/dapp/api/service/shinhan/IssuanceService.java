package com.nomadconnection.dapp.api.service.shinhan;

import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.UserCorporationDto;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.*;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.enums.ShinhanGwApiType;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.service.rpc.ShinhanGwRpc;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.*;
import com.nomadconnection.dapp.core.domain.repository.UserRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssuanceService {

    private final GatewayTransactionIdxRepository gatewayTransactionIdxRepository;
    private final UserRepository userRepository;

    private final D1510Repository d1510Repository;
    private final D1520Repository d1520Repository;
    private final D1530Repository d1530Repository;

    private final D1000Repository d1000Repository;
    private final D1400Repository d1400Repository;

    private final D1100Repository d1100Repository;


    private final ShinhanGwRpc shinhanGwRpc;

    /**
     * 카드 신청
     *
     * 1200
     * -> 1000/1400 : 여기서 ui에 결과리턴 and 성공시 다음 계속 연동 진행
     * -> 1510
     * -> 1520(재무제표 보유시: 2년치 2회연동 / 미보유시(신설업체 등): 최근 데이터 1회연동, 발급가능여부=N)
     * -> 1530
     */
    @Transactional(rollbackFor = Exception.class)
    public UserCorporationDto.IssuanceRes issuance(Long userIdx, UserCorporationDto.IssuanceReq request) {

        User user = findUser(userIdx);
        Corp userCorp = user.corp();

        // 1200(법인회원신규여부검증)
        DataPart1200 resultOfD1200 = proc1200(userCorp);

        if (resultOfD1200.getD003().equals("Y")) {
            // 1000(신규-법인회원신규심사요청)
            proc1000(userCorp);
        } else {
            // 1400(기존-법인조건변경신청)
            proc1400(userCorp);
        }

        // 성공 후, 서류제출 비동기 진행
        proc15xx(userCorp);

        // 성공시 Body 는 공백으로.
        return new UserCorporationDto.IssuanceRes();
    }

    // 1600(신청재개) 수신 후, 1100(법인카드 신청) 진행
    // todo : 에러 및 실패처리
    public UserCorporationDto.ResumeRes resumeApplication(UserCorporationDto.ResumeReq request) {

        // corpIdx 추출
        Long corpIdx = getCorpIdxFromLastRequest(request);

        // 1100(법인카드신청)
        proc1100(corpIdx);

        return new UserCorporationDto.ResumeRes();
    }

    // 기존 1400/1000 연동으로 부터 법인 식별자 추출
    private Long getCorpIdxFromLastRequest(UserCorporationDto.ResumeReq request) {
        Long corpIdx;
        String entity;

        D1400 d1400 = d1400Repository.findFirstByD033AndD034OrderByUpdatedAtDesc(request.getD001(), request.getD002());
        if (d1400 == null) {
            D1000 d1000 = d1000Repository.findFirstByD079AndD080OrderByUpdatedAtDesc(request.getD001(), request.getD002());
            corpIdx = d1000.idxCorp();
            entity = "d1000";
        } else {
            corpIdx = d1400.idxCorp();
            entity = "d1400";
        }

        // todo : 게이트웨이로 에러리턴 수정
        if (StringUtils.isEmpty(corpIdx)) {
            log.error("not fount applyNo[[{}], applyDate[{}] in {}", request.getD001(), request.getD002(), entity);
            throw new EntityNotFoundException("not found corporation idx", entity, corpIdx);
        }

        return corpIdx;
    }

    private DataPart1200 proc1200(Corp userCorp) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1200);

        // 데이터부
        D1200 d1200 = D1200.builder()
                .d001(userCorp.resCompanyIdentityNo())
                .d002(Const.D1200_MEMBER_TYPE_CODE).build();

        // 연동
        DataPart1200 requestRpc = new DataPart1200();
        BeanUtils.copyProperties(d1200, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        return shinhanGwRpc.request1200(requestRpc);
    }

    @Async
    void proc15xx(Corp userCorp) {
        // 1510(사업자등록증스크래핑)
        proc1510(userCorp);

        // 1520(재무제표스크래핑)
        proc1520(userCorp);

        // 1530(등기부등본스크래핑)
        proc1530(userCorp);
    }

    private void proc1510(Corp userCorp) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1510);

        // 데이터부 - db 추출, 세팅
        D1510 d1510 = d1510Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(userCorp.idx());
        if (d1510 == null) {
            throw new EntityNotFoundException("not found corporation idx", "d1510", userCorp.idx());
        }

        // 연동
        DataPart1510 requestRpc = new DataPart1510();
        BeanUtils.copyProperties(d1510, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        shinhanGwRpc.request1510(requestRpc);
    }

    private void proc1520(Corp userCorp) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1520);

        // 데이터부 - db 추출, 세팅
        List<D1520> d1520s = d1520Repository.findTop2ByIdxCorpOrderByUpdatedAtDesc(userCorp.idx());
        if (d1520s == null || d1520s.isEmpty()) {
            throw new EntityNotFoundException("not found corporation idx", "d1520", userCorp.idx());
        }

        // 연동
        for (D1520 d1520 : d1520s) {
            DataPart1520 requestRpc = new DataPart1520();
            BeanUtils.copyProperties(d1520, requestRpc);
            BeanUtils.copyProperties(commonPart, requestRpc);

            shinhanGwRpc.request1520(requestRpc);
        }
    }

    private void proc1530(Corp userCorp) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1530);

        // 데이터부 - db 추출, 세팅
        D1530 d1530 = d1530Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(userCorp.idx());
        if (d1530 == null) {
            throw new EntityNotFoundException("not found corporation idx", "d1530", userCorp.idx());
        }

        // 연동
        DataPart1530 requestRpc = new DataPart1530();
        BeanUtils.copyProperties(d1530, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        shinhanGwRpc.request1530(requestRpc);
    }

    private void proc1000(Corp userCorp) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1000);

        // 데이터부 - db 추출, 세팅
        D1000 d1000 = d1000Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(userCorp.idx());
        if (d1000 == null) {
            throw new EntityNotFoundException("not found corporation idx", "d1000", userCorp.idx());
        }

        // 접수일 저장, 접수순번 증가 저장
        int applyNo= StringUtils.isEmpty(d1000.d080())? 0: Integer.parseInt(d1000.d080());
        applyNo++;
        d1000.d080(String.valueOf(applyNo));
        d1000.d079(CommonUtil.getNowYYYYMMDD());

        // 연동
        DataPart1000 requestRpc = new DataPart1000();
        BeanUtils.copyProperties(d1000, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);
        shinhanGwRpc.request1000(requestRpc);
    }

    private void proc1400(Corp userCorp) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1400);

        // 데이터부 - db 추출, 세팅
        D1400 d1400 = d1400Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(userCorp.idx());
        if (d1400 == null) {
            throw new EntityNotFoundException("not found corporation idx", "d1400", userCorp.idx());
        }

        // 접수일 저장, 접수순번 증가 저장
        int applyNo= StringUtils.isEmpty(d1400.d034())? 0: Integer.parseInt(d1400.d034());
        applyNo++;
        d1400.d034(String.valueOf(applyNo));
        d1400.d033(CommonUtil.getNowYYYYMMDD());

        // 연동
        DataPart1400 requestRpc = new DataPart1400();
        BeanUtils.copyProperties(d1400, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);
        shinhanGwRpc.request1400(requestRpc);
    }

    private void proc1100(Long idxCorp) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1100);

        // 데이터부 - db 추출, 세팅
        D1100 d1100 = d1100Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(idxCorp);
        if (d1100 == null) {
            throw new EntityNotFoundException("not found corporation idx", "d1100", idxCorp);
        }

        // 연동
        DataPart1100 requestRpc = new DataPart1100();
        BeanUtils.copyProperties(d1100, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        shinhanGwRpc.request1100(requestRpc);
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

    private User findUser(Long idx_user) {
        return userRepository.findById(idx_user).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("User")
                        .idx(idx_user)
                        .build()
        );
    }

}
