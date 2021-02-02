package com.nomadconnection.dapp.api.v2.service.card;

import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.dto.lotte.CommonPart;
import com.nomadconnection.dapp.api.dto.lotte.DataPart1000;
import com.nomadconnection.dapp.api.dto.lotte.StatusDto;
import com.nomadconnection.dapp.api.dto.lotte.enums.LotteGwApiType;
import com.nomadconnection.dapp.api.dto.lotte.enums.LotteUserStatus;
import com.nomadconnection.dapp.api.dto.lotte.enums.Lotte_CardKind;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.service.CorpService;
import com.nomadconnection.dapp.api.service.lotte.rpc.LotteGwRpc;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.*;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.common.ConnectedMng;
import com.nomadconnection.dapp.core.domain.common.IssuanceProgress;
import com.nomadconnection.dapp.core.domain.consent.ConsentMapping;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1000;
import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1100;
import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1200;
import com.nomadconnection.dapp.core.domain.lotte.Lotte_GatewayTransactionIdx;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.StockholderFileRepository;
import com.nomadconnection.dapp.core.domain.repository.common.CommonCodeDetailRepository;
import com.nomadconnection.dapp.core.domain.repository.common.IssuanceProgressRepository;
import com.nomadconnection.dapp.core.domain.repository.consent.ConsentMappingRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CeoInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_D1000Repository;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_D1100Repository;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_D1200Repository;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_GatewayTransactionIdxRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskConfigRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.res.ConnectedMngRepository;
import com.nomadconnection.dapp.core.domain.res.ResAccount;
import com.nomadconnection.dapp.core.domain.shinhan.D1000;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.encryption.lotte.Lotte_Seed128;
import com.nomadconnection.dapp.secukeypad.EncryptParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

import static com.nomadconnection.dapp.api.util.CommonUtil.getValueOrDefault;
import static com.nomadconnection.dapp.api.v2.utils.CardCommonUtils.isRealOwnerConvertCeo;
import static com.nomadconnection.dapp.api.v2.utils.CardCommonUtils.isStockholderUpdateCeo;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotteCardServiceV2 {
    private final UserRepository repoUser;
    private final CorpRepository repoCorp;
    private final CardIssuanceInfoRepository repoCardIssuanceInfo;
    private final ConsentMappingRepository repoConsentMapping;
    private final IssuanceProgressRepository repoIssuanceProgress;
    private final StockholderFileRepository repoFile;
    private final RiskRepository repoRisk;
    private final RiskConfigRepository repoRiskConfig;
    private final CeoInfoRepository repoCeoInfo;
    private final ConnectedMngRepository repoConnectedMng;
    private final Lotte_D1000Repository repoD1000;
    private final Lotte_D1100Repository repoD1100;
    private final Lotte_D1200Repository repoD1200;
    private final Lotte_GatewayTransactionIdxRepository repoGatewayTransactionIdx;
    private final CommonCodeDetailRepository repoCodeDetail;

    private final LotteGwRpc lotteGwRpc;
    private final CorpService corpService;
    private final ShinhanCardServiceV2 shinhanCardService;


    public Lotte_D1100 updateD1100Corp(Long idxCorp, CardIssuanceDto.RegisterCorporation dto) {
        Lotte_D1100 d1100 = getD1100(idxCorp);
        if (ObjectUtils.isEmpty(d1100)) {
            return d1100;
        }
        String[] corNumber = dto.getCorNumber().split("-");
        return repoD1100.save(d1100
            .setTkpDdd(Lotte_Seed128.encryptEcb(corNumber[0]))
            .setTkpExno(Lotte_Seed128.encryptEcb(corNumber[1]))
            .setTkpTlno(Lotte_Seed128.encryptEcb(corNumber[2]))
            .setCpOgEnm(dto.getEngCorName())           //법인영문명
            .setBzplcDdd(corNumber[0])                  //직장전화지역번호
            .setBzplcExno(corNumber[1])                  //직장전화국번호
            .setBzplcTlno(corNumber[2])                  //직장전화고유번호

        );
    }

    public Lotte_D1100 updateD1100CorpExtend(Long idxCorp, CardIssuanceDto.RegisterCorporationExtend dto) {
        Lotte_D1100 d1100 = getD1100(idxCorp);
        if (ObjectUtils.isEmpty(d1100)) {
            return d1100;
        }

        return d1100.setVtCurTtEnpYn(dto.getIsVirtualCurrency() ? "Y" : "N");
    }

    public Lotte_D1100 updateD1100Stockholder(Long idxCorp, CardIssuanceInfo cardInfo, List<CeoInfo> ceoInfos,
                                               CardIssuanceDto.RegisterStockholder dto) {
        Lotte_D1100 d1100 = getD1100(idxCorp);
        if (ObjectUtils.isEmpty(d1100)) {
            return d1100;
        }

        for (CeoInfo ceoInfo : ceoInfos) {
            if (isRealOwnerConvertCeo(cardInfo, ceoInfo)) {
                return repoD1100.save(d1100
                    .setRlOwrDdc(Const.LOTTE_CORP_OWNER_CODE_5) // 법인 또는 단쳬의 대표
                    .setRlOwrNm(Lotte_Seed128.encryptEcb(ceoInfo.name()))
                    .setRlOwrEnm(Lotte_Seed128.encryptEcb(ceoInfo.engName()))
                    .setBird(Lotte_Seed128.encryptEcb(CommonUtil.birthLenConvert6To8(ceoInfo.birth())))
                    .setRlOwrNatyC(ceoInfo.nationality())
                    .setRlOwrVdMdc(Const.LOTTE_CORP_rlOwrVdMdc_CODE_09)
                    .setRlOwrDc(Const.LOTTE_CORP_rlOwrDc_CODE_4)
                    .setStchShrR("000")
                );
            }
        }

        String corpOwnerCode = getCorpOwnerCode(dto);
        return repoD1100.save(d1100
            .setRlOwrDdc(corpOwnerCode)
            .setRlOwrNm(Lotte_Seed128.encryptEcb(dto.getName()))
            .setRlOwrEnm(Lotte_Seed128.encryptEcb(dto.getEngName()))
            .setBird(Lotte_Seed128.encryptEcb(CommonUtil.birthLenConvert6To8(dto.getBirth())))
            .setRlOwrNatyC(dto.getNation())
            .setRlOwrVdMdc(Const.LOTTE_CORP_OWNER_CODE_5.equals(corpOwnerCode) ? Const.LOTTE_CORP_rlOwrVdMdc_CODE_09 : Const.LOTTE_CORP_rlOwrVdMdc_CODE_01)
            .setRlOwrDc(Const.LOTTE_CORP_OWNER_CODE_5.equals(corpOwnerCode) ? Const.LOTTE_CORP_rlOwrDc_CODE_4 : Const.LOTTE_CORP_rlOwrDc_CODE_1)
            .setStchShrR(dto.getRate())
        );
    }

    public Lotte_D1100 updateD1100Card(User user, String grantLimit, String calculatedLimit, String hopeLimit, CardIssuanceDto.RegisterCard dto) {
        Lotte_D1100 d1100 = getD1100(user.corp().idx());
        if (ObjectUtils.isEmpty(d1100)) {
            return d1100;
        }

        String encryptEmail = Lotte_Seed128.encryptEcb(user.email());
        d1100.setBllRvpDc(dto.getReceiveType().getLotteCode())
            .setMlId(!dto.getReceiveType().getLotteCode().equals("1") ? encryptEmail : null)
            .setCpAkLimAm(CommonUtil.divisionString(hopeLimit, 10000))
            .setAkLimAm(CommonUtil.divisionString(grantLimit, 10000))
            .setGowidCalLimAm(CommonUtil.divisionString(calculatedLimit, 10000))
            .setBzplcPsno(dto.getZipCode())
            .setBzplcPnadd(dto.getAddressBasic())
            .setBzplcBpnoAdd(dto.getAddressDetail())
            .setOffiNaddYn("N")
            .setTkpPsno(dto.getZipCode())
            .setTkpPnadd(dto.getAddressBasic())
            .setTkpBpnoAdd(dto.getAddressDetail())
            .setTkpMlId(encryptEmail)
            .setTkpNaddYn("N");

        d1100 = Lotte_CardKind.initCardKindInLotte_D1100(d1100);

        if (!ObjectUtils.isEmpty(dto.getGreenCount()) && !ObjectUtils.isEmpty(dto.getBlackCount())) {
            int seq = 1;
            if (dto.getGreenCount() > 0) {
                d1100 = Lotte_CardKind.setCardKindInLotte_D1100(d1100, Lotte_CardKind.GREEN, getCardReqCount(dto.getGreenCount()), seq);
                seq++;
            }
            if (dto.getBlackCount() > 0) {
                d1100 = Lotte_CardKind.setCardKindInLotte_D1100(d1100, Lotte_CardKind.BLACK, getCardReqCount(dto.getBlackCount()), seq);
                seq++;
            }
            if (dto.getGreenTrafficCount() > 0) {
                d1100 = Lotte_CardKind.setCardKindInLotte_D1100(d1100, Lotte_CardKind.GREEN_TRAFFIC, getCardReqCount(dto.getGreenTrafficCount()), seq);
                seq++;
            }
            if (dto.getBlackTrafficCount() > 0) {
                d1100 = Lotte_CardKind.setCardKindInLotte_D1100(d1100, Lotte_CardKind.BLACK_TRAFFIC, getCardReqCount(dto.getBlackTrafficCount()), seq);
                seq++;
            }
            if (dto.getHiPassCount() > 0) {
                d1100 = Lotte_CardKind.setCardKindInLotte_D1100(d1100, Lotte_CardKind.HI_PASS, getCardReqCount(dto.getHiPassCount()), seq);
            }
        }

        return repoD1100.save(d1100);
    }

    private String getCardReqCount(Long count) { // 롯데카드 신청수량이 N개인 경우 N-1개로 세팅
        return String.valueOf(count - 1);
    }

    private String getCorpOwnerCode(CardIssuanceDto.RegisterStockholder dto) {
        if (dto.getIsHold25()) {
            return Const.LOTTE_CORP_OWNER_CODE_1;
        } else {
            if (dto.getIsPersonal()) {
                return Const.LOTTE_CORP_OWNER_CODE_2;
            } else {
                return Const.LOTTE_CORP_OWNER_CODE_5;
            }
        }
    }

    public CardIssuanceInfo setCardInfoCard(CardIssuanceInfo cardInfo, CardIssuanceDto.RegisterCard dto, String calculatedLimit, String grantLimit) {
        Card card = cardInfo.card();
        if (ObjectUtils.isEmpty(card)) {
            card = Card.builder().build();
        }
        return cardInfo.card(card
            .addressBasic(dto.getAddressBasic())
            .addressDetail(dto.getAddressDetail())
            .zipCode(dto.getZipCode())
            .addressKey(dto.getAddressKey())
            .calculatedLimit(calculatedLimit)
            .grantLimit(grantLimit)
            .receiveType(dto.getReceiveType())
            .lotteGreenCount(dto.getGreenCount())
            .lotteBlackCount(dto.getBlackCount())
            .lotteGreenTrafficCount(dto.getGreenTrafficCount())
            .lotteBlackTrafficCount(dto.getBlackTrafficCount())
            .lotteHiPassCount(dto.getHiPassCount())
            .requestCount(dto.getBlackCount() + dto.getGreenCount() + dto.getGreenTrafficCount() +
                dto.getBlackTrafficCount() + dto.getHiPassCount()));
    }

    public Lotte_D1100 updateD1100Venture(Long idxCorp, CardIssuanceDto.RegisterVenture dto) {
        Lotte_D1100 d1100 = getD1100(idxCorp);
        if (ObjectUtils.isEmpty(d1100)) {
            return d1100;
        }

        String verifiedVentureYn = "N";
        if (!ObjectUtils.isEmpty(dto.getIsVerifiedVenture()) && dto.getIsVerifiedVenture()) {
            verifiedVentureYn = "Y";
        }

        String vcYn = "N";
        if (!ObjectUtils.isEmpty(dto.getIsVC()) && dto.getIsVC()) {
            vcYn = "Y";
        }

        return repoD1100.save(d1100
            .setVtbCfHvYn(verifiedVentureYn)
            .setIvArYn(vcYn));
    }

    /**
     * 법인회원신규여부검증
     *
     * @param idxUser     등록하는 User idx
     * @return StatusDto    신규여부
     */
    @Transactional(noRollbackFor = Exception.class)
    public StatusDto verifyNewMember(Long idxUser) {
        // 1000(법인회원신규여부검증)
        Corp userCorp = corpService.getCorpByUserIdx(idxUser);
        DataPart1000 resultOfD1000 = proc1000(userCorp);

        if ("Y".equals(resultOfD1000.getBzNewYn())) {
            repoD1100.save(Lotte_D1100.builder().idxCorp(userCorp.idx()).build());
            repoD1200.save(Lotte_D1200.builder().idxCorp(userCorp.idx()).build());
            return StatusDto.builder().status(LotteUserStatus.SUCCESS).build();
        } else if ("N".equals(resultOfD1000.getBzNewYn())) {
            Lotte_D1100 d1100 = repoD1100.getTopByIdxCorpOrderByIdxDesc(userCorp.idx());
            if (d1100 == null && CardCompany.LOTTE.equals(userCorp.user().cardCompany())) {
                deleteAllIssuanceInfo(userCorp.user());
            }
            return StatusDto.builder().status(LotteUserStatus.FAIL).build();
        } else {
            String msg = "bzNewYn is not Y/N. resultOfD1000.getBzNewYn() = " + resultOfD1000.getBzNewYn();
            CommonUtil.throwBusinessException(ErrorCode.External.INTERNAL_ERROR_LOTTE_1000, msg);
        }
        return StatusDto.builder().status(LotteUserStatus.NONE).build();
    }

    private DataPart1000 proc1000(Corp userCorp) {
        CommonPart commonPart = getCommonPart(LotteGwApiType.LT1000);
        Lotte_D1000 d1000 = repoD1000.getTopByIdxCorpOrderByIdxDesc(userCorp.idx());
        if (d1000 == null) {
            d1000 = Lotte_D1000.builder().idxCorp(userCorp.idx()).build();
        }

        d1000.setTransferDate(commonPart.getTransferDate());
        d1000.setBzno(CommonUtil.replaceHyphen(userCorp.resCompanyIdentityNo()));
        repoD1000.saveAndFlush(d1000);

        // 연동
        DataPart1000 requestRpc = new DataPart1000();
        BeanUtils.copyProperties(d1000, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);
        DataPart1000 responseRpc = lotteGwRpc.request1000(requestRpc, userCorp.user().idx());

        BeanUtils.copyProperties(responseRpc, d1000);
        repoD1000.save(d1000);

        return responseRpc;
    }

    protected CommonPart getCommonPart(LotteGwApiType apiType) {
        // common part 세팅.
        // optional : 응답 코드, 대외 기관 코드, 응답 메시지
        return CommonPart.builder()
            .protocolCode(apiType.getProtocolCode())
            .transferCode(apiType.getTransferCode())
            .guid(getTransactionId(Integer.parseInt(apiType.getProtocolCode())))
            .transferDate(CommonUtil.getNowYYYYMMDD() + CommonUtil.getNowHHMMSS())
            .build();
    }

    private String getTransactionId(Integer interfaceId) {
        Lotte_GatewayTransactionIdx gatewayTransactionIdx = Lotte_GatewayTransactionIdx.builder()
            .interfaceId(interfaceId)
            .build();
        repoGatewayTransactionIdx.save(gatewayTransactionIdx);
        repoGatewayTransactionIdx.flush();

        long tmpTranId = 30000000000L + gatewayTransactionIdx.getIdx();
        return "0" + tmpTranId;     // 030000000001
    }

    // UserService.initUser와 흡사
    private void deleteAllIssuanceInfo(User user) {
        Corp corp = user.corp();

        repoUser.saveAndFlush(user.corp(null).cardCompany(null));
        log.debug("Complete update gowid.User set idxCorp = null, cardCompany = null where idxCorp = @idxCorp");

        {
            List<Long> cardIssuanceInfoIdx = repoCardIssuanceInfo.findAllIdxByUserIdx(user.idx());
            repoCeoInfo.deleteAllByCardIssuanceInfoIdx(cardIssuanceInfoIdx);
            log.debug("Complete delete FROM gowid.CeoInfo where idxCardIssuanceInfo = @idxCardIssuanceInfo");

            repoFile.deleteAllByCardIssuanceInfoIdx(cardIssuanceInfoIdx);
            log.debug("Complete delete FROM gowid.StockholderFile where idxCardIssuanceInfo = @idxCardIssuanceInfo");

            repoCardIssuanceInfo.deleteAllByUserIdx(user.idx());
            log.debug("Complete delete from gowid.CardIssuanceInfo where idxUser = @idxUser");
        }

        List<ConnectedMng> connectedMng = repoConnectedMng.findByIdxUser(user.idx());
        if (!ObjectUtils.isEmpty(connectedMng)) {
            repoConnectedMng.deleteInBatch(repoConnectedMng.findByIdxUser(user.idx()));
            repoConnectedMng.flush();
        }
        log.debug("Complete delete from gowid.ConnectedMng where idxUser = @idxUser");

        List<ConsentMapping> consentMappings = repoConsentMapping.findAllByIdxUser(user.idx());
        if (!ObjectUtils.isEmpty(consentMappings)) {
            repoConsentMapping.deleteInBatch(consentMappings);
            repoConsentMapping.flush();
        }
        log.debug("Complete delete from gowid.ConsentMapping where idxUser = @idxUser");

        IssuanceProgress issuanceProgress = getIssuanceProgress(user.idx());
        if (!ObjectUtils.isEmpty(issuanceProgress)) {
            repoIssuanceProgress.delete(issuanceProgress);
            repoIssuanceProgress.flush();
        }
        log.debug("Complete delete from gowid.IssuanceProgress WHERE userIdx = @idxUser");

        repoRisk.deleteByCorpIdx(corp.idx());
        log.debug("Complete delete from gowid.Risk where idxCorp = @idxCorp");

        repoRiskConfig.deleteByCorpIdx(corp.idx());
        log.debug("Complete delete from gowid.RiskConfig where idxCorp = @idxCorp");

        repoCorp.deleteCorpByIdx(corp.idx());
        log.debug("Complete delete from gowid.Corp where idx = @idxCorp");
    }

    public Lotte_D1100 updateD1100Limit(User user, String grantLimit, String hopeLimit) {
        Lotte_D1100 d1100 = getD1100(user.corp().idx());
        if (ObjectUtils.isEmpty(d1100)) {
            return d1100;
        }

        d1100.setCpAkLimAm(CommonUtil.divisionString(hopeLimit, 10000))
            .setAkLimAm(CommonUtil.divisionString(grantLimit, 10000));
        return repoD1100.save(d1100);
    }

    public Lotte_D1100 updateD1100Account(Long idxCorp, ResAccount account) {
        Lotte_D1100 d1100 = getD1100(idxCorp);
        if (ObjectUtils.isEmpty(d1100) || ObjectUtils.isEmpty(account)) {
            return d1100;
        }
        String bankCode = account.organization();
        if (bankCode != null && bankCode.length() > 3) {
            bankCode = bankCode.substring(bankCode.length() - 3);
        }
        return repoD1100.save(d1100
            .setAcno(Lotte_Seed128.encryptEcb(account.resAccount()))
            .setDpwnm(Lotte_Seed128.encryptEcb(account.resAccountHolder()))
            .setFtbc(bankCode)
        );
    }

    public CeoInfo updateCeo(CeoInfo ceo, Long idxCorp, CardIssuanceInfo cardInfo, CardIssuanceDto.RegisterCeo dto, Integer ceoNum) {
        D1000 shinhanD1000 = shinhanCardService.getD1000(idxCorp);
        String ceoTypeCode = CeoType.convertShinhanToLotte(shinhanD1000.getD009()); // 대표자 유형

        Lotte_D1100 d1100 = getD1100(idxCorp);
        ceoNum = updateD1100Ceo(d1100, cardInfo, dto, ceoNum, ceoTypeCode);

        if (ObjectUtils.isEmpty(ceo)) {
            ceo = CeoInfo.builder()
                .cardIssuanceInfo(cardInfo)
                .engName(dto.getEngName())
                .name(dto.getName())
                .nationality(dto.getNation())
                .isForeign("KR".equalsIgnoreCase(dto.getNation()) ? false : true)
                .phoneNumber(dto.getPhoneNumber())
                .agencyCode(dto.getAgency())
                .genderCode(dto.getGenderCode())
                .birth(dto.getBirth())
                .certificationType(dto.getIdentityType())
                .type(!ObjectUtils.isEmpty(d1100) ? CeoType.fromLotte(ceoTypeCode) : null)
                .ceoNumber(ceoNum)
                .build();
        } else {
            ceo.engName(dto.getEngName())
                .name(dto.getName())
                .nationality(dto.getNation())
                .isForeign("KR".equalsIgnoreCase(dto.getNation()) ? false : true)
                .phoneNumber(dto.getPhoneNumber())
                .agencyCode(dto.getAgency())
                .genderCode(dto.getGenderCode())
                .birth(dto.getBirth())
                .certificationType(dto.getIdentityType())
                .type(!ObjectUtils.isEmpty(d1100) ? CeoType.fromLotte(ceoTypeCode) : null)
                .ceoNumber(ceoNum);
        }
        return ceo;
    }

    private Integer updateD1100Ceo(Lotte_D1100 d1100, CardIssuanceInfo cardInfo, CardIssuanceDto.RegisterCeo dto, Integer ceoNum, String ceoTypeCode) {
        if (d1100 == null) {
            return ceoNum;
        }

        String encryptName = Lotte_Seed128.encryptEcb(dto.getName());
        String encryptEngName = Lotte_Seed128.encryptEcb(dto.getEngName());

        d1100.setDgTc(ceoTypeCode);
        if (!StringUtils.hasText(d1100.getCstEnm()) || ceoNum == 1) { // 첫번째 대표자정보
            d1100 = d1100
                .setCstNm(encryptName)
                .setCstEnm(encryptEngName)
                .setNatyC(dto.getNation())
                .setMaFemDc(String.valueOf(dto.getGenderCode()));

            if (isStockholderUpdateCeo(cardInfo)) {
                d1100 = d1100
                    .setRlOwrNm(encryptName)
                    .setRlOwrEnm(encryptEngName)
                    .setBird(Lotte_Seed128.encryptEcb(CommonUtil.birthLenConvert6To8(dto.getBirth())))
                    .setRlOwrNatyC(dto.getNation())
                    .setRlMaFemDc(String.valueOf(dto.getGenderCode()))
                    .setRlOwrVdMdc(Const.LOTTE_CORP_rlOwrVdMdc_CODE_09)
                    .setRlOwrDc(Const.LOTTE_CORP_rlOwrDc_CODE_4)
                    .setStchShrR("000");
            }

            repoD1100.save(d1100);
            ceoNum = 1;

        } else if (!StringUtils.hasText(d1100.getCstEnm2()) || ceoNum == 2) { // 두번째 대표자정보
            repoD1100.save(d1100
                .setCstNm2(encryptName)
                .setCstEnm2(encryptEngName)
                .setNatyC2(dto.getNation())
                .setMaFemDc2(String.valueOf(dto.getGenderCode()))
            );
            ceoNum = 2;

        } else if (!StringUtils.hasText(d1100.getCstEnm3()) || ceoNum == 3) { // 세번째 대표자정보
            repoD1100.save(d1100
                .setCstNm3(encryptName)
                .setCstEnm3(encryptEngName)
                .setNatyC3(dto.getNation())
                .setMaFemDc3(String.valueOf(dto.getGenderCode()))
            );
            ceoNum = 3;
        }

        return ceoNum;
    }

    public void updateManager(User user, CardIssuanceDto.RegisterManager dto) {
        Lotte_D1100 d1100 = getD1100(user.corp().idx());
        String idNum = null;

        if ("0".equals(dto.getCeoNumber())) {
            idNum = d1100.getTkpRrno();
        } else if ("1".equals(dto.getCeoNumber())) {
            idNum = d1100.getDgRrno();
        } else if ("2".equals(dto.getCeoNumber())) {
            idNum = d1100.getDgRrno2();
        } else if ("3".equals(dto.getCeoNumber())) {
            idNum = d1100.getDgRrno3();
        }

        updateD1100Manager(d1100, user, dto, idNum);
    }

    private void updateD1100Manager(Lotte_D1100 d1100, User user, CardIssuanceDto.RegisterManager dto, String idNum) {
        if (d1100 != null) {
            String[] corNumber = user.corp().resCompanyNumber().split("-");
            String[] phoneNumber = dto.getPhoneNumber().split("-");
            repoD1100.save(d1100
                .setTkpNm(Lotte_Seed128.encryptEcb(dto.getName())) // 수령자명
                .setTkpEnm(Lotte_Seed128.encryptEcb(dto.getEngName())) // 수령자영문명
                .setTkpRrno(idNum) // 수령자주민번호
                .setTkpDpnm(getValueOrDefault(dto.getDepartment(), "대표이사")) // 수령자부서명
                .setTkpPsiNm(getValueOrDefault(dto.getTitle(), "대표이사")) // 수령자직위명
                .setTkpNatyC(dto.getNation()) // 수령자국적코드
                .setTkpMlId(Lotte_Seed128.encryptEcb(user.email())) // 수령자이메일
                .setTkpDdd(Lotte_Seed128.encryptEcb(corNumber[0])) // 수령자전화지역번호
                .setTkpExno(Lotte_Seed128.encryptEcb(corNumber[1])) // 수령자전화국번
                .setTkpTlno(Lotte_Seed128.encryptEcb(corNumber[2])) // 수령자전화개별번호
                .setTkpMbzNo(Lotte_Seed128.encryptEcb(phoneNumber[0])) // 수령자이동사업자번호
                .setTkpMexno(Lotte_Seed128.encryptEcb(phoneNumber[1])) // 수령자이동전화국번
                .setTkpMtlno(Lotte_Seed128.encryptEcb(phoneNumber[2])) // 수령자이동전화개별번호
            );
        }
    }

    // 1000 테이블에 대표자1,2,3 주민번호 저장(d11,15,19)
    public void updateIdentification(CardIssuanceInfo cardIssuanceInfo, CardIssuanceDto.IdentificationReq dto, Map<String, String> decryptData) {
        Lotte_D1100 d1100 = repoD1100.findFirstByIdxCorpOrderByUpdatedAtDesc(cardIssuanceInfo.corp().idx()).orElseThrow(
            () -> CorpNotRegisteredException.builder().build()
        );

        String idNum = dto.getIdentificationNumberFront() + decryptData.get(EncryptParam.IDENTIFICATION_NUMBER);
        String encryptIdNum = Lotte_Seed128.encryptEcb(idNum);
        String idfIsuBurNm = "정부24";
        String idfNo2 = Lotte_Seed128.encryptEcb(dto.getIssueDate());
        if (CertificationType.DRIVER.equals(dto.getIdentityType())) {
            idfIsuBurNm = getDriverLocalName(dto.getDriverLocal()) + "경찰청";
            idfNo2 = Lotte_Seed128.encryptEcb(getDriverLocalNumber(dto.getDriverLocal()) + decryptData.get(EncryptParam.DRIVER_NUMBER));
        }

        if ("0".equals(dto.getCeoSeqNo())) {
            d1100.setTkpRrno(encryptIdNum);
        } else if ("1".equals(dto.getCeoSeqNo())) {
            d1100.setHsVdPhc(dto.getIdentityType().getLotteCode());
            d1100.setIdfIsuBurNm(idfIsuBurNm);
            d1100.setIdfKndcNm(dto.getIdentityType().getDescription());
            d1100.setIdfNo2(idfNo2);
            d1100.setDgRrno(encryptIdNum);
        } else if ("2".equals(dto.getCeoSeqNo())) {
            d1100.setDgRrno2(encryptIdNum);
        } else if ("3".equals(dto.getCeoSeqNo())) {
            d1100.setDgRrno3(encryptIdNum);
        } else {
            log.error("invalid ceoSeqNo. ceoSeqNo=" + dto.getCeoSeqNo());
            throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, "invalid ceoSeqNo. ceoSeqNo=" + dto.getCeoSeqNo());
        }

        repoD1100.save(d1100);
    }

    private String getDriverLocalName(String code) {
        return repoCodeDetail.findFirstByCode1AndCode(code, CommonCodeType.SHINHAN_DRIVER_LOCAL_CODE).orElseThrow(
            () -> EntityNotFoundException.builder()
                .entity("CommonCodeDetail")
                .build()
        ).value1();
    }

    private String getDriverLocalNumber(String code) {
        return repoCodeDetail.findFirstByCode1AndCode(code, CommonCodeType.SHINHAN_DRIVER_LOCAL_CODE).orElseThrow(
            () -> EntityNotFoundException.builder()
                .entity("CommonCodeDetail")
                .build()
        ).value2();
    }

    private Lotte_D1100 getD1100(Long idxCorp) {
        return repoD1100.getTopByIdxCorpOrderByIdxDesc(idxCorp);
    }

    private IssuanceProgress getIssuanceProgress(Long idx_user) {
        return repoIssuanceProgress.findById(idx_user).orElse(null);
    }
}
