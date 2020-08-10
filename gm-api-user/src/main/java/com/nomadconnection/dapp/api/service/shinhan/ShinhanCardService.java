package com.nomadconnection.dapp.api.service.shinhan;

import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.MismatchedException;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.*;
import com.nomadconnection.dapp.core.domain.common.CommonCodeDetail;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.embed.BankAccount;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.StockholderFileRepository;
import com.nomadconnection.dapp.core.domain.repository.common.CommonCodeDetailRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CeoInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.VentureBusinessRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResAccountRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskConfigRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1000Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1100Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1400Repository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.res.ResAccount;
import com.nomadconnection.dapp.core.domain.risk.RiskConfig;
import com.nomadconnection.dapp.core.domain.shinhan.D1000;
import com.nomadconnection.dapp.core.domain.shinhan.D1100;
import com.nomadconnection.dapp.core.domain.shinhan.D1400;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.encryption.Seed128;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShinhanCardService {

    private final UserRepository repoUser;
    private final CorpRepository repoCorp;
    private final CardIssuanceInfoRepository repoCardIssuance;
    private final D1000Repository repoD1000;
    private final RiskConfigRepository repoRiskConfig;
    private final RiskRepository repoRisk;
    private final D1100Repository repoD1100;
    private final CommonCodeDetailRepository repoCodeDetail;
    private final CeoInfoRepository repoCeo;
    private final VentureBusinessRepository repoVenture;
    private final StockholderFileRepository repoFile;
	private final ResAccountRepository repoResAccount;
    private final D1400Repository repoD1400;


    /**
     * 법인정보 등록
     *
     * @param idx_user     등록하는 User idx
     * @param dto          등록정보
     * @param idx_CardInfo CardIssuanceInfo idx
     */
    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceDto.CorporationRes updateCorporation(Long idx_user, CardIssuanceDto.RegisterCorporation dto, Long idx_CardInfo, String depthKey) {
        User user = findUser(idx_user);

        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        D1000 d1000 = updateD1000Corp(user.corp().idx(), dto);
        updateD1400Corp(user.corp().idx(), dto);

        Corp corp = repoCorp.save(user.corp()
                .resCompanyEngNm(dto.getEngCorName())
                .resCompanyNumber(dto.getCorNumber())
                .resBusinessCode(dto.getBusinessCode())
                .resUserType(d1000 != null ? d1000.getD009() : null)
        );

        if (StringUtils.hasText(depthKey)) {
            repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
        }

        return CardIssuanceDto.CorporationRes.from(corp, cardInfo.idx());
    }

    /**
     * 법인정보 등록
     *
     * @param idx_user     등록하는 User idx
     * @param dto          등록정보
     * @param idx_CardInfo CardIssuanceInfo idx
     */
    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceDto.CorporationExtendRes updateCorporationExtend(Long idx_user, CardIssuanceDto.RegisterCorporationExtend dto, Long idx_CardInfo, String depthKey) {
        User user = findUser(idx_user);

        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        cardInfo = repoCardIssuance.save(cardInfo.corpExtend(CorpExtend.builder()
                .isVirtualCurrency(dto.getIsListedCompany())
                .isListedCompany(dto.getIsListedCompany())
                .listedCompanyCode(dto.getListedCompanyCode())
                .build())
        );

        if (StringUtils.hasText(depthKey)) {
            repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
        }

        return CardIssuanceDto.CorporationExtendRes.from(cardInfo, getListedCompanyName(dto.getListedCompanyCode()));
    }

    private String getListedCompanyName(String ListedCompanyCode) {
        CommonCodeDetail commonCodeDetail = repoCodeDetail.getByCodeAndCode1(CommonCodeType.LOTTE_LISTED_EXCHANGE, ListedCompanyCode);
        if (ObjectUtils.isEmpty(commonCodeDetail)) {
            return null;
        }
        return commonCodeDetail.value1();
    }

    private D1400 updateD1400Corp(Long idx_corp, CardIssuanceDto.RegisterCorporation dto) {
        D1400 d1400 = getD1400(idx_corp);
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


    private D1000 updateD1000Corp(Long idx_corp, CardIssuanceDto.RegisterCorporation dto) {
        D1000 d1000 = getD1000(idx_corp);
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

    /**
     * 벤처기업정보 등록
     *
     * @param idx_user     등록하는 User idx
     * @param dto          등록정보
     * @param idx_CardInfo CardIssuanceInfo idx
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceDto.VentureRes registerVenture(Long idx_user, CardIssuanceDto.RegisterVenture dto, Long idx_CardInfo, String depthKey) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        String investorName = repoVenture.findEqualsName(dto.getInvestorName());
        cardInfo.venture(Venture.builder()
                .investAmount(dto.getAmount())
                .isVC(dto.getIsVC())
                .isVerifiedVenture(dto.getIsVerifiedVenture())
                .investor(investorName != null ? investorName : dto.getInvestorName())
                .isExist(investorName != null ? true : false)
                .build()
        );

        updateRiskConfigVenture(user, dto);

        if (StringUtils.hasText(depthKey)) {
            repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
        }

        return CardIssuanceDto.VentureRes.from(repoCardIssuance.save(cardInfo));
    }

    private RiskConfig updateRiskConfigVenture(User user, CardIssuanceDto.RegisterVenture dto) {
        Optional<RiskConfig> riskConfig = repoRiskConfig.findByCorpAndEnabled(user.corp(), true);
        if (riskConfig.isPresent()) {
            return repoRiskConfig.save(riskConfig.get()
                    .ventureCertification(dto.getIsVerifiedVenture())
                    .vcInvestment(dto.getIsVC())
            );
        } else {
            return repoRiskConfig.save(RiskConfig.builder()
                    .user(user)
                    .corp(user.corp())
                    .ventureCertification(dto.getIsVerifiedVenture())
                    .enabled(true)
                    .vcInvestment(dto.getIsVC())
                    .build()
            );
        }
    }

    /**
     * 주주정보 등록
     *
     * @param idx_user     등록하는 User idx
     * @param dto          등록정보
     * @param idx_CardInfo CardIssuanceInfo idx
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceDto.VentureRes registerStockholder(Long idx_user, CardIssuanceDto.RegisterStockholder dto, Long idx_CardInfo, String depthKey) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        cardInfo = cardInfo.stockholder(Stockholder.builder()
                .isStockHold25(dto.getIsHold25())
                .isStockholderList(dto.getIsStockholderList())
                .isStockholderPersonal(dto.getIsPersonal())
                .stockholderName(dto.getName())
                .stockholderEngName(dto.getEngName())
                .stockholderBirth(dto.getBirth())
                .stockholderNation(dto.getNation())
                .stockRate(dto.getRate())
                .build());

        updateRiskConfigStockholder(user, dto);
        updateD1000Stockholder(user.corp().idx(), cardInfo, dto);
        updateD1400Stockholder(user.corp().idx(), cardInfo, dto);

        if (StringUtils.hasText(depthKey)) {
            repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
        }

        return CardIssuanceDto.VentureRes.from(repoCardIssuance.save(cardInfo));
    }

    private RiskConfig updateRiskConfigStockholder(User user, CardIssuanceDto.RegisterStockholder dto) {
        Optional<RiskConfig> riskConfig = repoRiskConfig.findByCorpAndEnabled(user.corp(), true);
        if (riskConfig.isPresent()) {
            return repoRiskConfig.save(riskConfig.get()
                    .isStockHold25(dto.getIsHold25())
                    .isStockholderList(dto.getIsStockholderList())
                    .isStockholderPersonal(dto.getIsPersonal())
            );
        } else {
            return repoRiskConfig.save(RiskConfig.builder()
                    .user(user)
                    .corp(user.corp())
                    .isStockHold25(dto.getIsHold25())
                    .isStockholderList(dto.getIsStockholderList())
                    .isStockholderPersonal(dto.getIsPersonal())
                    .build()
            );
        }
    }

    private D1400 updateD1400Stockholder(Long idx_corp, CardIssuanceInfo cardInfo, CardIssuanceDto.RegisterStockholder dto) {
        D1400 d1400 = getD1400(idx_corp);
        if (ObjectUtils.isEmpty(d1400)) {
            return d1400;
        }

        CeoInfo ceoInfo = repoCeo.getByCardIssuanceInfo(cardInfo);
        if (isUpdateRealCeo(cardInfo) && !ObjectUtils.isEmpty(ceoInfo)) {
            return repoD1400.save(d1400.setD018(Const.SHINHAN_REGISTER_BRANCH_CODE)
                    .setD019(ceoInfo.name())
                    .setD020(ceoInfo.engName())
                    .setD021(ceoInfo.birth())
                    .setD022(ceoInfo.nationality())
                    .setD023(getCorpOwnerCode(dto))
                    .setD024("00000")
            );
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

    private D1000 updateD1000Stockholder(Long idx_corp, CardIssuanceInfo cardInfo, CardIssuanceDto.RegisterStockholder dto) {
        D1000 d1000 = getD1000(idx_corp);
        if (ObjectUtils.isEmpty(d1000)) {
            return d1000;
        }

        CeoInfo ceoInfo = repoCeo.getByCardIssuanceInfo(cardInfo);
        if (isUpdateRealCeo(cardInfo) && !ObjectUtils.isEmpty(ceoInfo)) {
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

    /**
     * 카드 희망한도 저장
     *
     * @param idx_user 등록하는 User idx
     * @param dto      등록정보
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceDto.CardRes saveHopeLimit(Long idx_user, CardIssuanceDto.HopeLimitReq dto, String depthKey) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(dto.getCardIssuanceInfoIdx())) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        Card card = cardInfo.card();
        if (ObjectUtils.isEmpty(card)) {
            card = Card.builder().build();
        }

        if (StringUtils.hasText(depthKey)) {
            repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
        }

        return CardIssuanceDto.CardRes.from(repoCardIssuance.save(cardInfo.card(card.hopeLimit(dto.getHopeLimit()))));
    }

    /**
     * 카드발급정보 등록
     *
     * @param idx_user     등록하는 User idx
     * @param dto          등록정보
     * @param idx_CardInfo CardIssuanceInfo idx
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceDto.CardRes registerCard(Long idx_user, CardIssuanceDto.RegisterCard dto, Long idx_CardInfo, String depthKey) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        Double cardLimitNow = repoRisk.findCardLimitNowFirst(idx_user, CommonUtil.getNowYYYYMMDD());
        Long calculatedLimitLong = 0L;
        if (!ObjectUtils.isEmpty(cardLimitNow)) {
            calculatedLimitLong = Long.parseLong(String.valueOf(Math.round(cardLimitNow)));
        }
        String calculatedLimit = String.valueOf(calculatedLimitLong);

        String hopeLimit = calculatedLimit;
        if (!ObjectUtils.isEmpty(cardInfo.card()) && !ObjectUtils.isEmpty(cardInfo.card().hopeLimit())) {
            hopeLimit = cardInfo.card().hopeLimit();
        }

        String grantLimit = calculatedLimitLong < Long.parseLong(hopeLimit) ? calculatedLimit : hopeLimit;

        updateRiskConfigCard(user, grantLimit, calculatedLimit, hopeLimit);
        updateD1000Card(user.corp().idx(), grantLimit, dto);
        updateD1400Card(user.corp().idx(), grantLimit, dto);
        updateD1100Card(user.corp().idx(), grantLimit, dto);

        Card card = cardInfo.card();
        if (ObjectUtils.isEmpty(card)) {
            card = Card.builder().build();
        }
        cardInfo.card(card
                .addressBasic(dto.getAddressBasic())
                .addressDetail(dto.getAddressDetail())
                .zipCode(dto.getZipCode())
                .addressKey(dto.getAddressKey())
                .calculatedLimit(calculatedLimit)
                .grantLimit(grantLimit)
                .receiveType(dto.getReceiveType())
                .requestCount(dto.getCount()));

        if (StringUtils.hasText(depthKey)) {
            repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
        }

        return CardIssuanceDto.CardRes.from(repoCardIssuance.save(cardInfo));
    }

    private RiskConfig updateRiskConfigCard(User user, String grantLimit, String calculatedLimit, String hopeLimit) {
        Optional<RiskConfig> riskConfig = repoRiskConfig.findByCorpAndEnabled(user.corp(), true);
        if (riskConfig.isPresent()) {
            return repoRiskConfig.save(riskConfig.get()
                    .calculatedLimit(calculatedLimit)
                    .hopeLimit(hopeLimit)
                    .grantLimit(grantLimit)
            );
        } else {
            return repoRiskConfig.save(RiskConfig.builder()
                    .user(user)
                    .corp(user.corp())
                    .calculatedLimit(calculatedLimit)
                    .hopeLimit(hopeLimit)
                    .grantLimit(grantLimit)
                    .enabled(true)
                    .build()
            );
        }
    }

    private D1000 updateD1000Card(Long idx_corp, String grantLimit, CardIssuanceDto.RegisterCard dto) {
        D1000 d1000 = getD1000(idx_corp);
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

    private D1400 updateD1400Card(Long idx_corp, String grantLimit, CardIssuanceDto.RegisterCard dto) {
        D1400 d1400 = getD1400(idx_corp);
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

    private D1100 updateD1100Card(Long idx_corp, String grantLimit, CardIssuanceDto.RegisterCard dto) {
        D1100 d1100 = getD1100(idx_corp);
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

    /**
     * 결제 계좌정보 등록
     *
     * @param idx_user     등록하는 User idx
     * @param dto          등록정보
     * @param idx_CardInfo CardIssuanceInfo idx
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceDto.AccountRes registerAccount(Long idx_user, CardIssuanceDto.RegisterAccount dto, Long idx_CardInfo, String depthKey) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        ResAccount account = findResAccount(dto.getAccountIdx());
        if (ObjectUtils.isEmpty(account.resAccountHolder())) {
            account.resAccountHolder(dto.getAccountHolder());
        }

        updateD1100Account(user.corp().idx(), account);

        cardInfo.bankAccount(BankAccount.builder()
                .bankAccount(account.resAccount())
                .bankCode(account.organization())
                .bankAccountHolder(account.resAccountHolder())
                .build());

        if (StringUtils.hasText(depthKey)) {
            repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
        }

        return CardIssuanceDto.AccountRes.from(repoCardIssuance.save(cardInfo), getBankName(account.organization()));
    }

    private D1100 updateD1100Account(Long idx_corp, ResAccount account) {
        D1100 d1100 = getD1100(idx_corp);
        if (ObjectUtils.isEmpty(d1100) || ObjectUtils.isEmpty(account)) {
            return d1100;
        }
        String bankCode = account.organization();
        if (bankCode != null && bankCode.length() > 3) {
            bankCode = bankCode.substring(bankCode.length() - 3);
        }
        return repoD1100.save(d1100
                .setD024(bankCode)
                .setD025(Seed128.encryptEcb(account.resAccount()))
                .setD026(account.resAccountHolder())
        );
    }

    /**
     * 대표자 정보
     *
     * @param idx_user 조회하는 User idx
     * @return 등록 정보
     */
    @Transactional(readOnly = true)
    public CardIssuanceDto.CeoTypeRes getCeoType(Long idx_user) {
        User user = findUser(idx_user);
        if (user.corp() == null) {
            throw EntityNotFoundException.builder().entity("Corp").build();
        }
        D1000 d1000 = getD1000(user.corp().idx());
        Integer count = 1;
        CeoType ceoType = CeoType.SINGLE;
        if (d1000 != null) {
            ceoType = CeoType.fromShinhan(d1000.getD009());
            if (StringUtils.hasText(d1000.getD010()) && StringUtils.hasText(d1000.getD014()) && StringUtils.hasText(d1000.getD018())) {
                count = 3;
            } else if (StringUtils.hasText(d1000.getD010()) && StringUtils.hasText(d1000.getD014()) && !StringUtils.hasText(d1000.getD018())) {
                count = 2;
            }
        }
        return CardIssuanceDto.CeoTypeRes.builder()
                .type(ceoType)
                .count(count)
                .build();
    }

    /**
     * 대표자 등록
     *
     * @param idx_user     등록하는 User idx
     * @param dto          등록정보
     * @param idx_CardInfo CardIssuanceInfo idx
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceDto.CeoRes registerCeo(Long idx_user, CardIssuanceDto.RegisterCeo dto, Long idx_CardInfo, String depthKey) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        CeoInfo ceo = null;
        if (!ObjectUtils.isEmpty(dto.getCeoIdx())) {
            ceo = findCeoInfo(dto.getCeoIdx());
            if (!cardInfo.ceoInfos().contains(ceo)) {
                throw MismatchedException.builder().category(MismatchedException.Category.CEO).build();
            }
        }

        Integer ceoNum = 0;

        D1000 d1000 = getD1000(user.corp().idx());
        ceoNum = updateD1000Ceo(d1000, user.corp().idx(), cardInfo, dto, ceo, ceoNum);

        D1400 d1400 = getD1400(user.corp().idx());
        ceoNum = updateD1400Ceo(d1400, cardInfo, dto, ceo, ceoNum);

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
                    .type(!ObjectUtils.isEmpty(d1000) ? CeoType.fromShinhan(d1000.getD009()) : null)
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
                    .type(!ObjectUtils.isEmpty(d1000) ? CeoType.fromShinhan(d1000.getD009()) : null)
                    .ceoNumber(ceoNum);
        }

        if (isUpdateRealCeo(cardInfo)) {
            cardInfo.stockholder().stockholderName(dto.getName())
                    .stockholderEngName(dto.getEngName())
                    .stockholderBirth(dto.getBirth())
                    .stockholderNation(dto.getNation())
                    .stockRate("0000");
        }

        if (StringUtils.hasText(depthKey)) {
            repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
        }

        return CardIssuanceDto.CeoRes.from(repoCeo.save(ceo)).setDeviceId("");
    }

    private Integer updateD1000Ceo(D1000 d1000, Long idx_corp, CardIssuanceInfo cardInfo, CardIssuanceDto.RegisterCeo dto, CeoInfo ceo, Integer ceoNum) {
        if (d1000 == null) {
            return ceoNum;
        }

        if (!StringUtils.hasText(d1000.getD012()) || (ceo != null && ceo.ceoNumber().equals(1))) { // 첫번째 대표자정보
            d1000 = d1000.setD010(dto.getName())                     //대표자명1
                    .setD012(dto.getEngName())                  //대표자영문명1
                    .setD013(dto.getNation())                   //대표자국적코드1
                    .setD035(dto.getName())                     //신청관리자명
                    .setD040(dto.getPhoneNumber().substring(0, 3))      //신청관리자휴대전화식별번호
                    .setD041(dto.getPhoneNumber().substring(3, 7))      //신청관리자휴대전화국번호
                    .setD042(dto.getPhoneNumber().substring(7));         //신청관리자휴대전화고유번호

            if (isUpdateRealCeo(cardInfo)) {
                d1000 = d1000.setD059(dto.getName())
                        .setD060(dto.getEngName())
                        .setD061(dto.getBirth())
                        .setD062(dto.getNation())
                        .setD065("00000")
                        .setD066("KR".equalsIgnoreCase(dto.getNation()) ? "N" : "Y");
            }

            repoD1000.save(d1000);
            ceoNum = 1;

            updateD1100Ceo(idx_corp, dto);

        } else if (!StringUtils.hasText(d1000.getD016()) || (ceo != null && ceo.ceoNumber().equals(2))) { // 두번째 대표자정보
            repoD1000.save(d1000
                    .setD014(dto.getName())         //대표자명2
                    .setD016(dto.getEngName())      //대표자영문명2
                    .setD017(dto.getNation())       //대표자국적코드2
            );
            ceoNum = 2;

        } else if (!StringUtils.hasText(d1000.getD020()) || (ceo != null && ceo.ceoNumber().equals(3))) { // 세번째 대표자정보
            repoD1000.save(d1000
                    .setD018(dto.getName())         //대표자명3
                    .setD020(dto.getEngName())      //대표자영문명3
                    .setD021(dto.getNation())       //대표자국적코드3
            );
            ceoNum = 3;
        }


        return ceoNum;
    }

    private boolean isUpdateRealCeo(CardIssuanceInfo cardInfo) {
        return !ObjectUtils.isEmpty(cardInfo.stockholder())
                && Boolean.FALSE.equals(cardInfo.stockholder().isStockHold25())
                && Boolean.FALSE.equals(cardInfo.stockholder().isStockholderPersonal());
    }

    private D1100 updateD1100Ceo(Long idx_corp, CardIssuanceDto.RegisterCeo dto) {
        D1100 d1100 = getD1100(idx_corp);
        if (d1100 != null) {
            d1100 = repoD1100.save(d1100
                    .setD035(dto.getPhoneNumber().substring(0, 3))
                    .setD036(dto.getPhoneNumber().substring(3, 7))
                    .setD037(dto.getPhoneNumber().substring(7))
            );
        }
        return d1100;
    }

    private Integer updateD1400Ceo(D1400 d1400, CardIssuanceInfo cardInfo, CardIssuanceDto.RegisterCeo dto, CeoInfo ceo, Integer ceoNum) {
        if (d1400 == null) {
            return ceoNum;
        }

        if (!StringUtils.hasText(d1400.getD035()) || (ceo != null && ceo.ceoNumber().equals(1))) { // 첫번째 대표자정보
            d1400 = d1400
                    .setD032(dto.getName())                     //대표자명1
                    .setD034(dto.getEngName())                  //대표자영문명1
                    .setD035(dto.getNation())                   //대표자국적코드1
                    .setD057(dto.getName())                     //신청관리자명
                    .setD062(dto.getPhoneNumber().substring(0, 3))      //신청관리자휴대전화식별번호
                    .setD063(dto.getPhoneNumber().substring(3, 7))      //신청관리자휴대전화국번호
                    .setD064(dto.getPhoneNumber().substring(7));         //신청관리자휴대전화고유번호

            if (isUpdateRealCeo(cardInfo)) {
                d1400 = d1400.setD019(dto.getName())
                        .setD020(dto.getEngName())
                        .setD021(dto.getBirth())
                        .setD022(dto.getNation())
                        .setD024("00000");
            }

            repoD1400.save(d1400);
            ceoNum = 1;

        } else if (!StringUtils.hasText(d1400.getD039()) || (ceo != null && ceo.ceoNumber().equals(2))) { // 두번째 대표자정보
            repoD1400.save(d1400
                    .setD036(dto.getName())         //대표자명2
                    .setD038(dto.getEngName())      //대표자영문명2
                    .setD039(dto.getNation())       //대표자국적코드2
            );
            ceoNum = 2;

        } else if (!StringUtils.hasText(d1400.getD043()) || (ceo != null && ceo.ceoNumber().equals(3))) { // 세번째 대표자정보
            repoD1400.save(d1400
                    .setD040(dto.getName())         //대표자명3
                    .setD042(dto.getEngName())      //대표자영문명3
                    .setD043(dto.getNation())       //대표자국적코드3
            );
            ceoNum = 3;
        }

        return ceoNum;
    }

    private String getBankName(String bankCode) {
        CommonCodeDetail commonCodeDetail = repoCodeDetail.getByCodeAndCode1(CommonCodeType.BANK_1, bankCode);
        if (commonCodeDetail != null) {
            return commonCodeDetail.value1();
        }
        return null;
    }

    /**
     * 신한 운전면허 지역코드 조회
     *
     * @return 신한 운전면허 지역코드 목록
     */
    @Transactional(readOnly = true)
    public List<CardIssuanceDto.ShinhanDriverLocalCode> getShinhanDriverLocalCodes() {
        return repoCodeDetail.findAllByCode(CommonCodeType.SHINHAN_DRIVER_LOCAL_CODE).stream().map(CardIssuanceDto.ShinhanDriverLocalCode::from).collect(Collectors.toList());
    }

    /**
     * 대표자 타당성 확인
     *
     * @param idx_user 조회하는 User idx
     * @param dto      대표자 타당성 확인 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public void verifyValidCeo(Long idx_user, CardIssuanceDto.CeoValidReq dto, String depthKey) {
        User user = findUser(idx_user);
        if (ObjectUtils.isEmpty(user.corp())) {
            throw EntityNotFoundException.builder().entity("Corp").build();
        }

        CardIssuanceInfo cardIssuanceInfo = findCardIssuanceInfo(user.corp());
        cardIssuanceInfo.ceoInfos().forEach(ceoInfo -> {
            if (dto.getPhoneNumber().equals(ceoInfo.phoneNumber())) {
                throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, "ALREADY_AUTH_CEO");
            }
        });

        // 스크래핑 데이터와 입력 데이터 일치여부 확인
        verifyCorrespondCeo(user.corp().idx(), dto);

        if (StringUtils.hasText(depthKey)) {
            repoCardIssuance.save(cardIssuanceInfo.issuanceDepth(depthKey));
        }
    }

    public void verifyCorrespondCeo(Long idx_corp, CardIssuanceDto.CeoValidReq dto) {
        D1000 d1000 = getD1000(idx_corp);
        if (ObjectUtils.isEmpty(d1000)) {
            throw EntityNotFoundException.builder().entity("D1000").build();
        }

        boolean isValidCeoInfo = !checkCeo(d1000.getD010(), d1000.getD012(), d1000.getD011(), dto)
                && !checkCeo(d1000.getD014(), d1000.getD016(), d1000.getD015(), dto)
                && !checkCeo(d1000.getD018(), d1000.getD020(), d1000.getD019(), dto);

        if (isValidCeoInfo) {
            throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, "MISMATCH_CEO");
        }
    }

    private boolean checkCeo(String korName, String engName, String idNum, CardIssuanceDto.CeoValidReq dto) {
        if (!StringUtils.hasText(idNum)) {
            return false;
        }

        idNum = Seed128.decryptEcb(idNum);

        if ((dto.getName().equals(korName) || dto.getName().equals(engName)) && dto.getIdentificationNumberFront().substring(0, 6).equals(idNum.substring(0, 6))) {
            return true;
        }
        return false;
    }


    private User findUser(Long idx_user) {
        return repoUser.findById(idx_user).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("User")
                        .idx(idx_user)
                        .build()
        );
    }

    private CeoInfo findCeoInfo(Long idx_ceo) {
        return repoCeo.findById(idx_ceo).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("CeoInfo")
                        .idx(idx_ceo)
                        .build()
        );
    }

    private CardIssuanceInfo findCardIssuanceInfo(Corp corp) {
        return repoCardIssuance.findTopByCorpAndDisabledFalseOrderByIdxDesc(corp).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("CardIssuanceInfo")
                        .build()
        );
    }

    private D1000 getD1000(Long idx_corp) {
        return repoD1000.getTopByIdxCorpOrderByIdxDesc(idx_corp);
    }

    private D1100 getD1100(Long idx_corp) {
        return repoD1100.getTopByIdxCorpOrderByIdxDesc(idx_corp);
    }

    private D1400 getD1400(Long idx_corp) {
        return repoD1400.getTopByIdxCorpOrderByIdxDesc(idx_corp);
	}

	private StockholderFile findStockholderFile(Long idx_file) {
		return repoFile.findById(idx_file).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("StockholderFile")
						.idx(idx_file)
						.build()
		);
	}

	private ResAccount findResAccount(Long idx_resAccount) {
		return repoResAccount.findById(idx_resAccount).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("ResAccount")
						.idx(idx_resAccount)
						.build()
		);
	}
}
