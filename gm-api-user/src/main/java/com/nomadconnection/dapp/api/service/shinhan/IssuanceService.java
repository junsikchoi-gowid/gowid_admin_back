package com.nomadconnection.dapp.api.service.shinhan;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 카드 신청
     */
    @Transactional(rollbackFor = Exception.class)
    public UserCorporationDto.IssuanceRes issuance(Long userIdx, UserCorporationDto.IssuanceReq request) {

        User user = findUser(userIdx);
        Corp userCorp = user.corp();

        // 1200(법인회원신규여부검증)
        DataPart1200 resultOfD1200 = proc1200(userCorp);

        // 1510(사업자등록증스크래핑)
        proc1510(userCorp);

        // 1520(재무제표스크래핑)
        proc1520(userCorp);

        // 1530(등기부등본스크래핑)
        proc1530(userCorp);

        if (resultOfD1200.getD003().equals("Y")) {
            // 1400(기존-법인조건변경신청)
            proc1400(userCorp);
        } else {
            // 1000(신규-법인회원신규심사요청)
            proc1000(userCorp);
        }

        // 1100(법인카드신청)
        proc1100(userCorp);

        // 성공시 Body 는 공백으로.
        return new UserCorporationDto.IssuanceRes();
    }

    private DataPart1200 proc1200(Corp userCrop) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1200);

        // 데이터부 - db 추출, 세팅
        D1200 d1200 = d1200Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(userCrop.idx());

        // 연동
        DataPart1200 requestRpc = new DataPart1200();
        BeanUtils.copyProperties(d1200, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        return shinhanGwRpc.request1200(requestRpc);
    }

    private void proc1510(Corp userCrop) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1510);

        // 데이터부 - db 추출, 세팅
        D1510 d1510 = d1510Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(userCrop.idx());

        // 연동
        DataPart1510 requestRpc = new DataPart1510();
        BeanUtils.copyProperties(d1510, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        shinhanGwRpc.request1510(requestRpc);
    }

    private void proc1520(Corp userCrop) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1520);

        // 데이터부 - db 추출, 세팅
        D1520 d1520 = d1520Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(userCrop.idx());

        // 연동
        DataPart1520 requestRpc = new DataPart1520();
        BeanUtils.copyProperties(d1520, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        shinhanGwRpc.request1520(requestRpc);
    }

    private void proc1530(Corp userCrop) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1530);

        // 데이터부 - db 추출, 세팅
        D1530 d1530 = d1530Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(userCrop.idx());

        // 연동
        DataPart1530 requestRpc = new DataPart1530();
        BeanUtils.copyProperties(d1530, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        shinhanGwRpc.request1530(requestRpc);
    }

    private void proc1000(Corp userCrop) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1000);

        // 데이터부 - db 추출, 세팅
        D1000 d1000 = d1000Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(userCrop.idx());

        // 연동
        DataPart1000 requestRpc = new DataPart1000();
        BeanUtils.copyProperties(d1000, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        shinhanGwRpc.request1000(requestRpc);
    }

    private void proc1400(Corp userCrop) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1400);

        // 데이터부 - db 추출, 세팅
        D1400 d1400 = d1400Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(userCrop.idx());

        // 연동
        DataPart1400 requestRpc = new DataPart1400();
        BeanUtils.copyProperties(d1400, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);

        shinhanGwRpc.request1400(requestRpc);
    }

    private void proc1100(Corp userCrop) {
        // 공통부
        CommonPart commonPart = getCommonPart(ShinhanGwApiType.SH1100);

        // 데이터부 - db 추출, 세팅
        D1100 d1100 = d1100Repository.findFirstByIdxCorpOrderByUpdatedAtDesc(userCrop.idx());

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
