package com.nomadconnection.dapp.api.service.shinhan;

import com.nomadconnection.dapp.api.common.AsyncService;
import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.UserCorporationDto;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.CommonPart;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.DataPart1100;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.DataPart1800;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.enums.ShinhanGwApiType;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.exception.api.InternalErrorException;
import com.nomadconnection.dapp.api.service.rpc.ShinhanGwRpc;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.*;
import com.nomadconnection.dapp.core.domain.repository.SignatureHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.*;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.encryption.Seed128;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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
    private final GwTranHistRepository gwTranHistRepository;
    private final ShinhanGwRpc shinhanGwRpc;
    private final AsyncService asyncService;
    private final CommonService issCommonService;

    @Value("${encryption.keypad.enable: true}")
    private Boolean ENC_KEYPAD_ENABLE;

    @Value("${encryption.seed128.enable: true}")
    private Boolean ENC_SEED128_ENABLE;

    // 1600(신청재개) 수신 후, 1100(법인카드 신청) 진행
    public UserCorporationDto.ResumeRes resumeApplication(UserCorporationDto.ResumeReq request) {
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1600);
        UserCorporationDto.ResumeRes response = new UserCorporationDto.ResumeRes();
        BeanUtils.copyProperties(commonPart, response);

        asyncService.run(() -> proc1100(request));  // 1100(법인카드신청), 비동기 처리
        asyncService.run(() -> proc1800(request));  // 1800(전자서명값전달), 비동기 처리

        return response;
    }

    @Async
    void proc1800(UserCorporationDto.ResumeReq request) {
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1800);

        D1200 d1200 = getD1200ByApplicationDateAndApplicationNum(request.getD001(), request.getD002());
        SignatureHistory signatureHistory = getSignatureHistoryByApplicationInfo(request.getD001(), request.getD002());

        DataPart1800 requestRpc = DataPart1800.builder()
                .d001(CommonUtil.getDigitalSignatureIdNumber(d1200.getD001()))
                .d002(Const.ELEC_SIGNATURE_CERTI_PROD_CODE)
                .d003(CommonUtil.encodeBase64(signatureHistory.getSignedBinaryString()))
                .build();
        BeanUtils.copyProperties(commonPart, requestRpc);
        requestRpc.setC009("00");   // todo : 테스트 데이터(삭제예정)

        issCommonService.saveGwTran(commonPart);
        issCommonService.saveGwTran(shinhanGwRpc.request1800(requestRpc));
    }

    private SignatureHistory getSignatureHistoryByApplicationInfo(String applicationDate, String applicationNum) {
        return signatureHistoryRepository.findFirstByApplicationDateAndApplicationNum(applicationDate, applicationDate).orElseThrow(
                () -> new BadRequestException(ErrorCode.Api.NOT_FOUND,
                        "not found d1200 of applicationDate[" + applicationDate + "], applicationNum[" + applicationNum + "]")
        );
    }

    @Async
    public void proc1100(UserCorporationDto.ResumeReq request) {
        // 공통부
        CommonPart commonPart = issCommonService.getCommonPart(ShinhanGwApiType.SH1100);

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

        // db에는 암호화된 상태.
        // 테스트 환경에서는 복호화해서 보냄
        requestRpc.setD021(Seed128.decryptEcb(d1100.getD021()));            // 비번
        requestRpc.setD025(Seed128.decryptEcb(d1100.getD025()));            // 결제계좌번호
        // todo 운영에서는 암호화해서 전송
//        requestRpc.setD021(d1100.getD021());            // 비번
//        requestRpc.setD025(d1100.getD025());            // 결제계좌번호

        issCommonService.saveGwTran(requestRpc);
        issCommonService.saveGwTran(shinhanGwRpc.request1100(requestRpc));
    }

    // 기존 1400/1000 연동으로 부터 법인 식별자 추출
    private Long getCorpIdxFromLastRequest(UserCorporationDto.ResumeReq request) {
        Long corpIdx;

        D1200 d1200 = getD1200ByApplicationDateAndApplicationNum(request.getD001(), request.getD002());
        if ("Y".equals(d1200.getD003())) {
            D1000 d1000 = d1000Repository.findFirstByD079AndD080OrderByUpdatedAtDesc(request.getD001(), request.getD002());
            corpIdx = d1000.getIdxCorp();
        } else {
            D1400 d1400 = d1400Repository.findFirstByD033AndD034OrderByUpdatedAtDesc(request.getD001(), request.getD002());
            corpIdx = d1400.getIdxCorp();
        }

        if (StringUtils.isEmpty(corpIdx)) {
            String msg = "not fount applyNo[" + request.getD001() + "], applyDate[" + request.getD002() + "]";
            throw new BadRequestException(ErrorCode.Api.NOT_FOUND, msg);
        }

        return corpIdx;
    }


    private D1200 getD1200ByApplicationDateAndApplicationNum(String applicationDate, String applicationNum) {
        return d1200Repository.findFirstByD007AndD008OrderByUpdatedAtDesc(applicationDate, applicationNum).orElseThrow(
                () -> new BadRequestException(ErrorCode.Api.NOT_FOUND,
                        "not found d1200 of applicationDate[" + applicationDate + "], applicationNum[" + applicationNum + "]")
        );
    }
}