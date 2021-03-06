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
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.service.CorpService;
import com.nomadconnection.dapp.api.service.lotte.rpc.LotteGwRpc;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.api.v2.service.scraping.FullTextService;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.*;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1000;
import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1100;
import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1200;
import com.nomadconnection.dapp.core.domain.lotte.Lotte_GatewayTransactionIdx;
import com.nomadconnection.dapp.core.domain.repository.common.CommonCodeDetailRepository;
import com.nomadconnection.dapp.core.domain.repository.connect.ConnectedMngRepository;
import com.nomadconnection.dapp.core.domain.repository.consent.ConsentMappingRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_D1000Repository;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_D1100Repository;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_D1200Repository;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_GatewayTransactionIdxRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskConfigRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
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
    private final ConsentMappingRepository repoConsentMapping;
    private final RiskRepository repoRisk;
    private final RiskConfigRepository repoRiskConfig;
    private final ConnectedMngRepository repoConnectedMng;
    private final Lotte_D1000Repository repoD1000;
    private final Lotte_D1100Repository repoD1100;
    private final Lotte_D1200Repository repoD1200;
    private final Lotte_GatewayTransactionIdxRepository repoGatewayTransactionIdx;
    private final CommonCodeDetailRepository repoCodeDetail;

    private final LotteGwRpc lotteGwRpc;
    private final CorpService corpService;
    private final ShinhanCardServiceV2 shinhanCardService;
    private final FullTextService fullTextService;


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
            .setCpOgEnm(dto.getEngCorName())           //???????????????
            .setBzplcDdd(corNumber[0])                  //????????????????????????
            .setBzplcExno(corNumber[1])                  //?????????????????????
            .setBzplcTlno(corNumber[2])                  //????????????????????????

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
                    .setRlOwrDdc(Const.LOTTE_CORP_OWNER_CODE_5) // ?????? ?????? ????????? ??????
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

    private String getCardReqCount(Long count) { // ???????????? ??????????????? N?????? ?????? N-1?????? ??????
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
     * ???????????? ????????? ?????????
     *
     * @param idxUser     ???????????? User idx
     */
    @Transactional(noRollbackFor = Exception.class)
    public void initAlreadyMember(Long idxUser) {
        User user = repoUser.findById(idxUser).orElseThrow(
            () -> UserNotFoundException.builder().id(idxUser).build()
        );
        if (!ObjectUtils.isEmpty(user.corp())) {
            Long idxCorp = user.corp().idx();
            fullTextService.deleteAllShinhanFulltext(idxCorp);
            fullTextService.deleteAllLotteFulltext(idxCorp);
            repoRisk.deleteByCorpIdx(idxCorp);
            if (!ObjectUtils.isEmpty(user.corp().riskConfig())) {
                user.corp().riskConfig().user(null);
                user.corp().riskConfig().corp(null);
                user.corp().riskConfig(null);
                repoRiskConfig.deleteByCorpIdx(idxCorp);
            }
            user.corp().user(null);
            for (CardIssuanceInfo cardIssuanceInfo : user.corp().cardIssuanceInfo()) {
                if (CardType.GOWID.equals(cardIssuanceInfo.cardType())) {
                    cardIssuanceInfo.corp(null);
                    cardIssuanceInfo.issuanceDepth(IssuanceDepth.SELECT_CARD);
                    cardIssuanceInfo.cardCompany(null);
                }
            }
            repoConnectedMng.deleteAllByUserIdx(idxUser);
            user.corp(null);
            repoCorp.deleteCorpByIdx(idxCorp);
        }
        repoConsentMapping.deleteAllByUserIdx(idxUser);
        user.cardCompany(null);
        repoUser.save(user);
    }

    /**
     * ??????????????????????????????
     *
     * @param idxUser     ???????????? User idx
     * @return StatusDto    ????????????
     */
    @Transactional(noRollbackFor = Exception.class)
    public StatusDto verifyNewMember(Long idxUser) {
        // 1000(??????????????????????????????)
        Corp userCorp = corpService.getCorpByUserIdx(idxUser);
        DataPart1000 resultOfD1000 = proc1000(userCorp);

        if ("Y".equals(resultOfD1000.getBzNewYn())) {
            repoD1100.save(Lotte_D1100.builder().idxCorp(userCorp.idx()).build());
            repoD1200.save(Lotte_D1200.builder().idxCorp(userCorp.idx()).build());
            return StatusDto.builder().status(LotteUserStatus.SUCCESS).build();
        } else if ("N".equals(resultOfD1000.getBzNewYn())) {
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

        // ??????
        DataPart1000 requestRpc = new DataPart1000();
        BeanUtils.copyProperties(d1000, requestRpc);
        BeanUtils.copyProperties(commonPart, requestRpc);
        DataPart1000 responseRpc = lotteGwRpc.request1000(requestRpc, userCorp.user().idx());

        BeanUtils.copyProperties(responseRpc, d1000);
        repoD1000.save(d1000);

        return responseRpc;
    }

    protected CommonPart getCommonPart(LotteGwApiType apiType) {
        // common part ??????.
        // optional : ?????? ??????, ?????? ?????? ??????, ?????? ?????????
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
        D1000 shinhanD1000 = shinhanCardService.getD1000ByCardIssuanceInfo(cardInfo);
        String ceoTypeCode = CeoType.convertShinhanToLotte(shinhanD1000.getD009()); // ????????? ??????

        Lotte_D1100 d1100 = getD1100(idxCorp);
        ceoNum = updateD1100Ceo(d1100, cardInfo, dto, ceoNum, ceoTypeCode);

        if (ObjectUtils.isEmpty(ceo)) {
            ceo = CeoInfo.builder()
                .cardIssuanceInfo(cardInfo)
                .engName(dto.getEngName())
                .name(dto.getName())
                .nationality(dto.getNation())
                .isForeign(!"KR".equalsIgnoreCase(dto.getNation()))
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
                .isForeign(!"KR".equalsIgnoreCase(dto.getNation()))
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
        if (!StringUtils.hasText(d1100.getCstEnm()) || ceoNum == 1) { // ????????? ???????????????
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

        } else if (!StringUtils.hasText(d1100.getCstEnm2()) || ceoNum == 2) { // ????????? ???????????????
            repoD1100.save(d1100
                .setCstNm2(encryptName)
                .setCstEnm2(encryptEngName)
                .setNatyC2(dto.getNation())
                .setMaFemDc2(String.valueOf(dto.getGenderCode()))
            );
            ceoNum = 2;

        } else if (!StringUtils.hasText(d1100.getCstEnm3()) || ceoNum == 3) { // ????????? ???????????????
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
                .setTkpNm(Lotte_Seed128.encryptEcb(dto.getName())) // ????????????
                .setTkpEnm(Lotte_Seed128.encryptEcb(dto.getEngName())) // ??????????????????
                .setTkpRrno(idNum) // ?????????????????????
                .setTkpDpnm(getValueOrDefault(dto.getDepartment(), "????????????")) // ??????????????????
                .setTkpPsiNm(getValueOrDefault(dto.getTitle(), "????????????")) // ??????????????????
                .setTkpNatyC(dto.getNation()) // ?????????????????????
                .setTkpMlId(Lotte_Seed128.encryptEcb(user.email())) // ??????????????????
                .setTkpDdd(Lotte_Seed128.encryptEcb(corNumber[0])) // ???????????????????????????
                .setTkpExno(Lotte_Seed128.encryptEcb(corNumber[1])) // ?????????????????????
                .setTkpTlno(Lotte_Seed128.encryptEcb(corNumber[2])) // ???????????????????????????
                .setTkpMbzNo(Lotte_Seed128.encryptEcb(phoneNumber[0])) // ??????????????????????????????
                .setTkpMexno(Lotte_Seed128.encryptEcb(phoneNumber[1])) // ???????????????????????????
                .setTkpMtlno(Lotte_Seed128.encryptEcb(phoneNumber[2])) // ?????????????????????????????????
            );
        }
    }

    // 1000 ???????????? ?????????1,2,3 ???????????? ??????(d11,15,19)
    public void updateIdentification(CardIssuanceInfo cardIssuanceInfo, CardIssuanceDto.IdentificationReq dto, Map<String, String> decryptData) {
        Lotte_D1100 d1100 = repoD1100.findFirstByIdxCorpOrderByUpdatedAtDesc(cardIssuanceInfo.corp().idx()).orElseThrow(
            () -> CorpNotRegisteredException.builder().build()
        );

        String idNum = dto.getIdentificationNumberFront() + decryptData.get(EncryptParam.IDENTIFICATION_NUMBER);
        String encryptIdNum = Lotte_Seed128.encryptEcb(idNum);
        String idfIsuBurNm = "??????24";
        String idfNo2 = Lotte_Seed128.encryptEcb(dto.getIssueDate());
        if (CertificationType.DRIVER.equals(dto.getIdentityType())) {
            idfIsuBurNm = getDriverLocalName(dto.getDriverLocal()) + "?????????";
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
}
