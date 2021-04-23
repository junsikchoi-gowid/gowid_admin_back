package com.nomadconnection.dapp.api.v2.service.card;

import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.Card;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CeoInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CeoType;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1000Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1100Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1400Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1530Repository;
import com.nomadconnection.dapp.core.domain.res.ResAccount;
import com.nomadconnection.dapp.core.domain.shinhan.D1000;
import com.nomadconnection.dapp.core.domain.shinhan.D1100;
import com.nomadconnection.dapp.core.domain.shinhan.D1400;
import com.nomadconnection.dapp.core.domain.shinhan.D1530;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.encryption.shinhan.Seed128;
import com.nomadconnection.dapp.core.utils.EnvUtil;
import com.nomadconnection.dapp.secukeypad.EncryptParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
public class ShinhanCardServiceV2 {
    private final D1000Repository repoD1000;
    private final D1100Repository repoD1100;
    private final D1400Repository repoD1400;
    private final D1530Repository repoD1530;

    private final EnvUtil envUtil;

    @Value("${encryption.seed128.enable}")
    private boolean ENC_SEED128_ENABLE;

    private static String HIDDEN_CODE = "*******";

    public D1000 updateD1000Corp(CardIssuanceInfo cardIssuanceInfo, CardIssuanceDto.RegisterCorporation dto) {
        D1000 d1000 = getD1000ByCardIssuanceInfo(cardIssuanceInfo);
        if (ObjectUtils.isEmpty(d1000)) {
            return d1000;
        }
        String[] corNumber = dto.getCorNumber().split("-");
        return repoD1000.save(d1000
            .setD006(dto.getEngCorName())           //법인영문명
            .setD008(dto.getBusinessCode())         //업종코드
            .setD026(corNumber[0])                  //직장전화지역번호
            .setD027(corNumber[1])                  //직장전화국번호
            .setD028(corNumber[2])                  //직장전화고유번호
            .setD036(corNumber[0])                  //신청관리자전화지역번호
            .setD037(corNumber[1])                  //신청관리자전화국번호
            .setD038(corNumber[2])                  //신청관리자전화고유번호
        );
    }

    public D1400 updateD1400Corp(CardIssuanceInfo cardIssuanceInfo, CardIssuanceDto.RegisterCorporation dto) {
        D1400 d1400 = getD1400ByCardIssuanceInfo(cardIssuanceInfo);
        if (ObjectUtils.isEmpty(d1400)) {
            return d1400;
        }
        String[] corNumber = dto.getCorNumber().split("-");
        return repoD1400.save(d1400
            .setD029(dto.getEngCorName())       // 법인영문명
            .setD048(corNumber[0])              // 직장전화지역번호
            .setD049(corNumber[1])              // 직장전화국번호
            .setD050(corNumber[2])              // 직장전화고유번호
            .setD058(corNumber[0])              // 신청관리자전화지역번호
            .setD059(corNumber[1])              // 신청관리자전화국번호
            .setD060(corNumber[2])              // 신청관리자전화고유번호
        );
    }

    public D1400 updateD1400Stockholder(Long idxCorp, CardIssuanceInfo cardInfo, List<CeoInfo> ceoInfos, CardIssuanceDto.RegisterStockholder dto) {
        D1400 d1400 = getD1400ByCardIssuanceInfo(cardInfo);
        if (ObjectUtils.isEmpty(d1400)) {
            return d1400;
        }

        for (CeoInfo ceoInfo : ceoInfos) {
            if (isRealOwnerConvertCeo(cardInfo, ceoInfo) && ceoInfo.ceoNumber() == 1) {
                return repoD1400.save(d1400.setD018(Const.SHINHAN_REGISTER_BRANCH_CODE)
                    .setD019(ceoInfo.name())
                    .setD020(ceoInfo.engName())
                    .setD021(ceoInfo.birth())
                    .setD022(ceoInfo.nationality())
                    .setD023(getCorpOwnerCode(dto))
                    .setD024("00000")
                );
            }
        }

        return repoD1400.save(d1400.setD018(Const.SHINHAN_REGISTER_BRANCH_CODE)
            .setD019(dto.getName())
            .setD020(dto.getEngName())
            .setD021(dto.getBirth())
            .setD022(dto.getNation())
            .setD023(getCorpOwnerCode(dto))
            .setD024(dto.getRate())
        );
    }

    public D1000 updateD1000Stockholder(Long idxCorp, CardIssuanceInfo cardInfo, List<CeoInfo> ceoInfos,
                                         CardIssuanceDto.RegisterStockholder dto) {
        D1000 d1000 = getD1000ByCardIssuanceInfo(cardInfo);
        if (ObjectUtils.isEmpty(d1000)) {
            return d1000;
        }

        for (CeoInfo ceoInfo : ceoInfos) {
            if (isRealOwnerConvertCeo(cardInfo, ceoInfo) && ceoInfo.ceoNumber() == 1) {
                return repoD1000.save(d1000.setD044(Const.SHINHAN_REGISTER_BRANCH_CODE)
                    .setD059(ceoInfo.name())
                    .setD060(ceoInfo.engName())
                    .setD061(ceoInfo.birth())
                    .setD062(ceoInfo.nationality())
                    .setD064(getCorpOwnerCode(dto))
                    .setD065("00000")
                    .setD066("KR".equalsIgnoreCase(dto.getNation()) ? "N" : "Y")
                );
            }
        }

        return repoD1000.save(d1000.setD044(Const.SHINHAN_REGISTER_BRANCH_CODE)
            .setD059(dto.getName())
            .setD060(dto.getEngName())
            .setD061(dto.getBirth())
            .setD062(dto.getNation())
            .setD064(getCorpOwnerCode(dto))
            .setD065(dto.getRate())
            .setD066("KR".equalsIgnoreCase(dto.getNation()) ? "N" : "Y")
        );
    }

    public void updateShinhanFulltextLimit(CardIssuanceInfo cardIssuanceInfo, String grantLimit) {
        updateD1000Limit(cardIssuanceInfo, grantLimit);
        updateD1100Limit(cardIssuanceInfo, grantLimit);
        updateD1400Limit(cardIssuanceInfo, grantLimit);
    }

    private D1000 updateD1000Limit(CardIssuanceInfo cardIssuanceInfo, String grantLimit) {
        D1000 d1000 = getD1000ByCardIssuanceInfo(cardIssuanceInfo);
        if (ObjectUtils.isEmpty(d1000)) {
            return d1000;
        }
        return repoD1000.save(d1000.setD050(grantLimit));
    }

    private D1100 updateD1100Limit(CardIssuanceInfo cardIssuanceInfo, String grantLimit) {
        D1100 d1100 = getD1100ByCardIssuanceInfo(cardIssuanceInfo);
        if (ObjectUtils.isEmpty(d1100)) {
            return d1100;
        }
        return repoD1100.save(d1100.setD020(grantLimit));
    }

    private D1400 updateD1400Limit(CardIssuanceInfo cardIssuanceInfo, String grantLimit) {
        D1400 d1400 = getD1400ByCardIssuanceInfo(cardIssuanceInfo);
        if (ObjectUtils.isEmpty(d1400)) {
            return d1400;
        }
        return repoD1400.save(d1400.setD014(grantLimit));
    }

    public void updateShinhanFulltextCard(CardIssuanceInfo cardIssuanceInfo, String grantLimit, CardIssuanceDto.RegisterCard dto) {
        updateD1000Card(cardIssuanceInfo, grantLimit, dto);
        updateD1100Card(cardIssuanceInfo, grantLimit, dto);
        updateD1400Card(cardIssuanceInfo, grantLimit, dto);
    }

    private D1000 updateD1000Card(CardIssuanceInfo cardIssuanceInfo, String grantLimit, CardIssuanceDto.RegisterCard dto) {
        D1000 d1000 = getD1000ByCardIssuanceInfo(cardIssuanceInfo);
        if (ObjectUtils.isEmpty(d1000)) {
            return d1000;
        }
        return repoD1000.save(d1000
            .setD022(dto.getZipCode().substring(0, 3))      //직장우편앞번호
            .setD023(dto.getZipCode().substring(3))         //직장우편뒷번호
            .setD024(dto.getAddressBasic())                 //직장기본주소
            .setD025(dto.getAddressDetail())                //직장상세주소
            .setD050(grantLimit)                            //제휴약정한도금액
            .setD055(dto.getAddressKey())                   //도로명참조KEY값
        );
    }

    private D1400 updateD1400Card(CardIssuanceInfo cardIssuanceInfo, String grantLimit, CardIssuanceDto.RegisterCard dto) {
        D1400 d1400 = getD1400ByCardIssuanceInfo(cardIssuanceInfo);
        if (ObjectUtils.isEmpty(d1400)) {
            return d1400;
        }
        return repoD1400.save(d1400
            .setD014(grantLimit)
            .setD044(dto.getZipCode().substring(0, 3))      // 직장우편앞번호
            .setD045(dto.getZipCode().substring(3))         // 직장우편뒷번호
            .setD046(dto.getAddressBasic())                 // 직장기본주소
            .setD047(dto.getAddressDetail())                // 직장상세주소
            .setD066(dto.getAddressKey())                   // 도로명참조KEY값
        );
    }

    private D1100 updateD1100Card(CardIssuanceInfo cardIssuanceInfo, String grantLimit, CardIssuanceDto.RegisterCard dto) {
        D1100 d1100 = getD1100ByCardIssuanceInfo(cardIssuanceInfo);
        if (ObjectUtils.isEmpty(d1100)) {
            return d1100;
        }
        return repoD1100.save(d1100
            .setD020(grantLimit)
            .setD029(dto.getReceiveType().getShinhanCode())
            .setD031(dto.getZipCode().substring(0, 3))
            .setD032(dto.getZipCode().substring(3))
            .setD033(dto.getAddressBasic())
            .setD034(dto.getAddressDetail())
            .setD039(dto.getCount() + "")
            .setD046(Const.CARD_RECEIVE_ADDRESS_CODE)
            .setD047(dto.getAddressKey())
        );
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
            .requestCount(dto.getCount()));
    }

    public void updateD1100Account(CardIssuanceInfo cardIssuanceInfo, ResAccount account) {
        D1100 d1100 = getD1100ByCardIssuanceInfo(cardIssuanceInfo);
        String bankCode = account.organization();
        bankCode = GowidUtils.get3digitsBankCode(bankCode);

        d1100.updateBankInfo(bankCode, Seed128.encryptEcb(account.resAccount()), account.resAccountHolder());
    }

    public CeoInfo updateCeo(CeoInfo ceo, Long idxCorp, CardIssuanceInfo cardInfo, CardIssuanceDto.RegisterCeo dto, Integer ceoNum) {
        D1000 d1000 = getD1000ByCardIssuanceInfo(cardInfo);
        ceoNum = updateD1000Ceo(d1000, idxCorp, cardInfo, dto, ceoNum);
        D1400 d1400 = getD1400ByCardIssuanceInfo(cardInfo);
        ceoNum = updateD1400Ceo(d1400, cardInfo, dto, ceoNum);

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
                .type(!ObjectUtils.isEmpty(d1000) ? CeoType.fromShinhan(d1000.getD009()) : null)
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
                .type(!ObjectUtils.isEmpty(d1000) ? CeoType.fromShinhan(d1000.getD009()) : null)
                .ceoNumber(ceoNum);
        }

        return ceo;
    }

    private Integer updateD1000Ceo(D1000 d1000, Long idxCorp, CardIssuanceInfo cardInfo, CardIssuanceDto.RegisterCeo dto, Integer ceoNum) {
        if (d1000 == null) {
            return ceoNum;
        }

        if (!StringUtils.hasText(d1000.getD012()) || (ceoNum == 1)) { // 첫번째 대표자정보
            d1000 = d1000.setD010(dto.getName())                     //대표자명1
                .setD012(dto.getEngName())                  //대표자영문명1
                .setD013(dto.getNation());                   //대표자국적코드1

            if (isStockholderUpdateCeo(cardInfo)) {
                d1000 = d1000.setD059(dto.getName())
                    .setD060(dto.getEngName())
                    .setD061(dto.getBirth())
                    .setD062(dto.getNation())
                    .setD065("00000")
                    .setD066("KR".equalsIgnoreCase(dto.getNation()) ? "N" : "Y");
            }

            repoD1000.save(d1000);
            ceoNum = 1;

            updateD1100Ceo(cardInfo, dto);

        } else if (!StringUtils.hasText(d1000.getD016()) || ceoNum == 2) { // 두번째 대표자정보
            repoD1000.save(d1000
                .setD014(dto.getName())         //대표자명2
                .setD016(dto.getEngName())      //대표자영문명2
                .setD017(dto.getNation())       //대표자국적코드2
            );
            ceoNum = 2;

        } else if (!StringUtils.hasText(d1000.getD020()) || ceoNum == 3) { // 세번째 대표자정보
            repoD1000.save(d1000
                .setD018(dto.getName())         //대표자명3
                .setD020(dto.getEngName())      //대표자영문명3
                .setD021(dto.getNation())       //대표자국적코드3
            );
            ceoNum = 3;
        }


        return ceoNum;
    }

    private D1100 updateD1100Ceo(CardIssuanceInfo cardIssuanceInfo, CardIssuanceDto.RegisterCeo dto) {
        D1100 d1100 = getD1100ByCardIssuanceInfo(cardIssuanceInfo);
        if (d1100 != null && dto.getPhoneNumber() != null) {
            String[] phoneNumber = dto.getPhoneNumber().split("-");
            d1100 = repoD1100.save(d1100
                .setD035(phoneNumber[0])
                .setD036(phoneNumber[1])
                .setD037(phoneNumber[2])
            );
        }
        return d1100;
    }

    private Integer updateD1400Ceo(D1400 d1400, CardIssuanceInfo cardInfo, CardIssuanceDto.RegisterCeo dto, Integer ceoNum) {
        if (d1400 == null) {
            return ceoNum;
        }

        if (!StringUtils.hasText(d1400.getD035()) || ceoNum == 1) { // 첫번째 대표자정보
            d1400 = d1400
                .setD032(dto.getName())                     //대표자명1
                .setD034(dto.getEngName())                  //대표자영문명1
                .setD035(dto.getNation())                   //대표자국적코드1
                .setD057(dto.getName());                     //신청관리자명

            if (isStockholderUpdateCeo(cardInfo)) {
                d1400 = d1400.setD019(dto.getName())
                    .setD020(dto.getEngName())
                    .setD021(dto.getBirth())
                    .setD022(dto.getNation())
                    .setD024("00000");
            }

            repoD1400.save(d1400);
            ceoNum = 1;

        } else if (!StringUtils.hasText(d1400.getD039()) || ceoNum == 2) { // 두번째 대표자정보
            repoD1400.save(d1400
                .setD036(dto.getName())         //대표자명2
                .setD038(dto.getEngName())      //대표자영문명2
                .setD039(dto.getNation())       //대표자국적코드2
            );
            ceoNum = 2;

        } else if (!StringUtils.hasText(d1400.getD043()) || ceoNum == 3) { // 세번째 대표자정보
            repoD1400.save(d1400
                .setD040(dto.getName())         //대표자명3
                .setD042(dto.getEngName())      //대표자영문명3
                .setD043(dto.getNation())       //대표자국적코드3
            );
            ceoNum = 3;
        }

        return ceoNum;
    }

    public void updateManager(User user, CardIssuanceInfo cardIssuanceInfo, CardIssuanceDto.RegisterManager dto) {
        D1000 d1000 = getD1000ByCardIssuanceInfo(cardIssuanceInfo);
        String idNum = null;
        if ("0".equals(dto.getCeoNumber())) {
            idNum = d1000.getD034();
        } else if ("1".equals(dto.getCeoNumber())) {
            idNum = d1000.getD011();
        } else if ("2".equals(dto.getCeoNumber())) {
            idNum = d1000.getD015();
        } else if ("3".equals(dto.getCeoNumber())) {
            idNum = d1000.getD019();
        }

        updateD1000Manager(d1000, user, dto, idNum);
        updateD1400Manager(user, cardIssuanceInfo, dto, idNum);
    }

    private void updateD1000Manager(D1000 d1000, User user, CardIssuanceDto.RegisterManager dto, String idNum) {
        if (d1000 != null) {
            String[] corNumber = user.corp().resCompanyNumber().split("-");
            String[] phoneNumber = dto.getPhoneNumber().split("-");
            repoD1000.save(d1000
                .setD032(getValueOrDefault(dto.getDepartment(), "대표이사")) // 신청관리자부서명
                .setD033(getValueOrDefault(dto.getTitle(), "대표이사")) // 신청관리자직위명
                .setD034(idNum) // 신청관리자주민번호
                .setD035(dto.getName()) // 신청관리자명
                .setD036(corNumber[0]) // 신청관리자전화지역번호
                .setD037(corNumber[1]) // 신청관리자전화국번호
                .setD038(corNumber[2]) // 신청관리자전화고유번호
                .setD040(phoneNumber[0]) // 신청관리자휴대전화식별번호
                .setD041(phoneNumber[1]) // 신청관리자휴대전화국번호
                .setD042(phoneNumber[2]) // 신청관리자휴대전화고유번호
                .setD043(user.email()) // 신청관리자이메일주소

            );
        }
    }

    private void updateD1400Manager(User user, CardIssuanceInfo cardIssuanceInfo, CardIssuanceDto.RegisterManager dto, String idNum) {
        D1400 d1400 = getD1400ByCardIssuanceInfo(cardIssuanceInfo);
        if (d1400 != null) {
            String[] corNumber = user.corp().resCompanyNumber().split("-");
            String[] phoneNumber = dto.getPhoneNumber().split("-");
            repoD1400.save(d1400
                .setD054(getValueOrDefault(dto.getDepartment(), "대표이사")) // 신청관리자부서명
                .setD055(getValueOrDefault(dto.getTitle(), "대표이사")) // 신청관리자직위명
                .setD056(idNum) // 신청관리자주민번호
                .setD057(dto.getName()) // 신청관리자명
                .setD058(corNumber[0]) // 신청관리자전화지역번호
                .setD059(corNumber[1]) // 신청관리자전화국번호
                .setD060(corNumber[2]) // 신청관리자전화고유번호
                .setD062(phoneNumber[0]) // 신청관리자휴대전화식별번호
                .setD063(phoneNumber[1]) // 신청관리자휴대전화국번호
                .setD064(phoneNumber[2]) // 신청관리자휴대전화고유번호
                .setD065(user.email()) // 신청관리자이메일주소
            );
        }
    }

    public void updateIdentification(CardIssuanceInfo cardIssuanceInfo, CardIssuanceDto.IdentificationReq dto, Map<String, String> decryptData) {
        save1000Identification(cardIssuanceInfo, dto, decryptData);
        save1400Identification(cardIssuanceInfo, dto, decryptData);
        if(envUtil.isProd() && !"0".equals(dto.getCeoSeqNo())) {
            save1530Identification(cardIssuanceInfo, dto);
        }
    }

    // 1000 테이블에 대표자1,2,3 주민번호 저장(d11,15,19)
    private void save1000Identification(CardIssuanceInfo cardIssuanceInfo, CardIssuanceDto.IdentificationReq dto, Map<String, String> decryptData) {
        D1000 d1000 = repoD1000.findFirstByCardIssuanceInfoOrderByUpdatedAtDesc(cardIssuanceInfo).orElseThrow(
            () -> CorpNotRegisteredException.builder().build()
        );
        String idNum = dto.getIdentificationNumberFront() + decryptData.get(EncryptParam.IDENTIFICATION_NUMBER);
        idNum = Seed128.encryptEcb(idNum);
        String ceoType = d1000.getD009();

        if ("0".equals(dto.getCeoSeqNo())) {
            d1000.setD034(idNum);      // 신청관리자주민등록번호
        } else if ("1".equals(dto.getCeoSeqNo())) {
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

        if(CeoType.EACH.getShinhanCode().equals(ceoType)){
            d1000.cleanUpOtherCeoInfo();
        }

        repoD1000.save(d1000);
    }

    // 1400 테이블에 대표자 주민번호 저장
    private void save1400Identification(CardIssuanceInfo cardIssuanceInfo, CardIssuanceDto.IdentificationReq dto, Map<String, String> decryptData) {
        D1400 d1400 = repoD1400.findFirstByCardIssuanceInfoOrderByUpdatedAtDesc(cardIssuanceInfo).orElseThrow(
            () -> CorpNotRegisteredException.builder().build()
        );

        String idNum = dto.getIdentificationNumberFront() + decryptData.get(EncryptParam.IDENTIFICATION_NUMBER);
        idNum = Seed128.encryptEcb(idNum);
        String ceoType = d1400.getD031();

        if ("0".equals(dto.getCeoSeqNo())) {
            d1400.setD056(idNum);       // 신청관리자주민등록번호
        } else if ("1".equals(dto.getCeoSeqNo())) {
            d1400.setD006(idNum);
            d1400.setD033(idNum);       // 대표자주민등록번호1
            d1400.setD056(idNum);       // 신청관리자주민등록번호
        } else if ("2".equals(dto.getCeoSeqNo())) {
            d1400.setD037(idNum);       // 대표자주민등록번호2
        } else if ("3".equals(dto.getCeoSeqNo())) {
            d1400.setD041(idNum);       // 대표자주민등록번호3
        } else {
            log.error("invalid ceoSeqNo. ceoSeqNo=" + dto.getCeoSeqNo());
            throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, "invalid ceoSeqNo. ceoSeqNo=" + dto.getCeoSeqNo());
        }

        if(CeoType.EACH.getShinhanCode().equals(ceoType)){
            d1400.cleanUpOtherCeoInfo();
        }

        repoD1400.save(d1400);
    }

    // 1530 테이블에 대표자 주민번호 저장
    private void save1530Identification(CardIssuanceInfo cardIssuanceInfo, CardIssuanceDto.IdentificationReq dto) {
        D1530 d1530 = repoD1530.findFirstByIdxCorpOrderByUpdatedAtDesc(cardIssuanceInfo.corp().idx()).orElseThrow(
            () -> CorpNotRegisteredException.builder().build()
        );

        String idNum = dto.getIdentificationNumberFront().substring(0, 6) + HIDDEN_CODE;
        idNum = Seed128.encryptEcb(idNum);

        // 외국인 신분증 진위여부시 한글명, 영문명으로 두번 요청(dto.getName : 한글명 or 영문명)하기때문에
        // 영문명으로 진위확인이 되는 경우 전문에 영문명이 저장되어 있지 않으므로
        // korName으로 체크
        // Todo : codef에서 외국인 정보를 정상적으로 주는 경우 해당 로직 개선 필요
        if (d1530.getD046().contains(dto.getKorName())) {
            d1530.setD047(idNum);       // 대표자주민등록번호1
        } else if (d1530.getD050().contains(dto.getKorName())) {
            d1530.setD051(idNum);       // 대표자주민등록번호2
        } else if (d1530.getD054().contains(dto.getKorName())) {
            d1530.setD055(idNum);       // 대표자주민등록번호3
        } else {
            log.error("Not matched ceoInfo in D1530. ceoInfo=" + dto);
            throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, "Not matched ceoInfo in D1530. ceoName=" + dto.getName());
        }

        repoD1530.save(d1530);
    }

    private String getCorpOwnerCode(CardIssuanceDto.RegisterStockholder dto) {
        if (dto.getIsHold25()) {
            return Const.SHINHAN_CORP_OWNER_CODE_1;
        } else {
            if (dto.getIsPersonal()) {
                return Const.SHINHAN_CORP_OWNER_CODE_2;
            } else {
                return Const.SHINHAN_CORP_OWNER_CODE_5;
            }
        }
    }

    public D1000 getD1000(Long idxCorp) {
        return repoD1000.getTopByIdxCorpOrderByIdxDesc(idxCorp);
    }

    public D1000 getD1000ByCardIssuanceInfo(CardIssuanceInfo cardIssuanceInfo) {
        return repoD1000.findFirstByCardIssuanceInfoOrderByUpdatedAtDesc(cardIssuanceInfo).orElseThrow(
            () -> CorpNotRegisteredException.builder().build()
        );
    }

    private D1100 getD1100(Long idxCorp) {
        return repoD1100.getTopByIdxCorpOrderByIdxDesc(idxCorp);
    }

    private D1100 getD1100ByCardIssuanceInfo(CardIssuanceInfo cardIssuanceInfo) {
        return repoD1100.findFirstByCardIssuanceInfoOrderByUpdatedAtDesc(cardIssuanceInfo).orElseThrow(
            () -> CorpNotRegisteredException.builder().build()
        );
    }

    private D1400 getD1400(Long idxCorp) {
        return repoD1400.getTopByIdxCorpOrderByIdxDesc(idxCorp);
    }

    private D1400 getD1400ByCardIssuanceInfo(CardIssuanceInfo cardIssuanceInfo) {
        return repoD1400.findFirstByCardIssuanceInfoOrderByUpdatedAtDesc(cardIssuanceInfo).orElseThrow(
            () -> CorpNotRegisteredException.builder().build()
        );
    }
}
