package com.nomadconnection.dapp.api.service.lotte;

import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.dto.lotte.enums.Lotte_CardKind;
import com.nomadconnection.dapp.api.dto.shinhan.DataPart1700;
import com.nomadconnection.dapp.api.exception.BadRequestedException;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.MismatchedException;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.service.shinhan.IssuanceService;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.*;
import com.nomadconnection.dapp.core.domain.common.CommonCodeDetail;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.embed.BankAccount;
import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1100;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.common.CommonCodeDetailRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CeoInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.VentureBusinessRepository;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_D1100Repository;
import com.nomadconnection.dapp.core.domain.repository.res.ResAccountRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskConfigRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1000Repository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.res.ResAccount;
import com.nomadconnection.dapp.core.domain.risk.RiskConfig;
import com.nomadconnection.dapp.core.domain.shinhan.D1000;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.encryption.lotte.Lotte_Seed128;
import com.nomadconnection.dapp.secukeypad.EncryptParam;
import com.nomadconnection.dapp.secukeypad.SecuKeypad;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotteCardService {
	private final UserRepository repoUser;
	private final CorpRepository repoCorp;
	private final CardIssuanceInfoRepository repoCardIssuance;
	private final RiskConfigRepository repoRiskConfig;
	private final RiskRepository repoRisk;
	private final Lotte_D1100Repository repoD1100;
	private final CommonCodeDetailRepository repoCodeDetail;
	private final CeoInfoRepository repoCeo;
	private final VentureBusinessRepository repoVenture;
	private final ResAccountRepository repoResAccount;
	private final D1000Repository repoShinhanD1000;

	private final IssuanceService shinhanIssuanceService;


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

		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
		if (!cardInfo.idx().equals(idx_CardInfo)) {
			throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
		}

		updateD1100Corp(user.corp().idx(), dto);
		D1000 shinhanD1000 = repoShinhanD1000.getTopByIdxCorpOrderByIdxDesc(user.corp().idx());

		Corp corp = repoCorp.save(user.corp()
				.resCompanyEngNm(dto.getEngCorName())
				.resCompanyNumber(dto.getCorNumber())
				.resBusinessCode(dto.getBusinessCode())
				.resUserType(!ObjectUtils.isEmpty(shinhanD1000) ? shinhanD1000.getD009() : null)
		);

		if (StringUtils.hasText(depthKey)) {
			repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
		}

		return CardIssuanceDto.CorporationRes.from(corp, cardInfo.idx());
	}

	/**
	 * 법인 추가정보 등록
	 *
	 * @param idx_user     등록하는 User idx
	 * @param dto          등록정보
	 * @param idx_CardInfo CardIssuanceInfo idx
	 */
	@Transactional(rollbackFor = Exception.class)
	public CardIssuanceDto.CorporationExtendRes updateCorporationExtend(Long idx_user, CardIssuanceDto.RegisterCorporationExtend dto, Long idx_CardInfo, String depthKey) {
		User user = findUser(idx_user);

		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
		if (!cardInfo.idx().equals(idx_CardInfo)) {
			throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
		}

		cardInfo = repoCardIssuance.save(cardInfo.corpExtend(CorpExtend.builder()
				.isVirtualCurrency(dto.getIsVirtualCurrency())
				.isListedCompany(dto.getIsListedCompany())
				.listedCompanyCode(dto.getListedCompanyCode())
				.build())
		);

		updateD1100CorpExtend(user.corp().idx(), dto);

		if (StringUtils.hasText(depthKey)) {
			repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
		}

		return CardIssuanceDto.CorporationExtendRes.from(cardInfo, getListedCompanyName(dto.getListedCompanyCode()));
	}

	private Lotte_D1100 updateD1100CorpExtend(Long idx_corp, CardIssuanceDto.RegisterCorporationExtend dto) {
		Lotte_D1100 d1100 = getD1100(idx_corp);
		if (ObjectUtils.isEmpty(d1100)) {
			return d1100;
		}

		return repoD1100.save(d1100
				.setVtCurTtEnpYn(dto.getIsVirtualCurrency() ? "Y" : "N")
				.setListStexC(dto.getIsListedCompany() ? dto.getListedCompanyCode() : null)
		);
	}

	private String getListedCompanyName(String ListedCompanyCode) {
		CommonCodeDetail commonCodeDetail = repoCodeDetail.getByCodeAndCode1(CommonCodeType.LOTTE_LISTED_EXCHANGE, ListedCompanyCode);
		if (ObjectUtils.isEmpty(commonCodeDetail)) {
			return null;
		}
		return commonCodeDetail.value1();
	}


	private Lotte_D1100 updateD1100Corp(Long idx_corp, CardIssuanceDto.RegisterCorporation dto) {
		Lotte_D1100 d1100 = getD1100(idx_corp);
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
		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
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
		updateD1100Venture(user.corp().idx(), dto);

		if (StringUtils.hasText(depthKey)) {
			repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
		}

		return CardIssuanceDto.VentureRes.from(repoCardIssuance.save(cardInfo));
	}

	private Lotte_D1100 updateD1100Venture(Long idx_corp, CardIssuanceDto.RegisterVenture dto) {
		Lotte_D1100 d1100 = getD1100(idx_corp);
		if (ObjectUtils.isEmpty(d1100)) {
			return d1100;
		}

		String verifiedVentureYn = "N";
		if (ObjectUtils.isEmpty(dto.getIsVerifiedVenture()) && dto.getIsVerifiedVenture()) {
			verifiedVentureYn = "Y";
		}

		String vcYn = "N";
		if (ObjectUtils.isEmpty(dto.getIsVC()) && dto.getIsVC()) {
			vcYn = "Y";
		}

		return repoD1100.save(d1100
				.setVtbCfHvYn(verifiedVentureYn)
				.setIvArYn(vcYn));
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
		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
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

		CeoInfo ceoInfo = repoCeo.getByCardIssuanceInfo(cardInfo);
		if (isUpdateRealCeo(cardInfo) && !ObjectUtils.isEmpty(ceoInfo)) {
			cardInfo = setStockholderByCeoInfo(cardInfo, ceoInfo);
		}

		updateRiskConfigStockholder(user, dto);
		updateD1000Stockholder(user.corp().idx(), cardInfo, dto);

		if (StringUtils.hasText(depthKey)) {
			repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
		}

		return CardIssuanceDto.VentureRes.from(repoCardIssuance.save(cardInfo));
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

	private Lotte_D1100 updateD1000Stockholder(Long idx_corp, CardIssuanceInfo cardInfo, CardIssuanceDto.RegisterStockholder dto) {
		Lotte_D1100 d1100 = getD1100(idx_corp);
		if (ObjectUtils.isEmpty(d1100)) {
			return d1100;
		}

		CeoInfo ceoInfo = repoCeo.getByCardIssuanceInfo(cardInfo);
		if (isUpdateRealCeo(cardInfo) && !ObjectUtils.isEmpty(ceoInfo)) {
			return repoD1100.save(d1100
					.setRlOwrDdc(getCorpOwnerCode(dto)) // 법인 또는 단쳬의 대표
					.setRlOwrNm(Lotte_Seed128.encryptEcb(ceoInfo.name()))
					.setRlOwrEnm(Lotte_Seed128.encryptEcb(ceoInfo.engName()))
					.setBird(Lotte_Seed128.encryptEcb(ceoInfo.birth()))
					.setRlOwrNatyC(ceoInfo.nationality())
					.setStchShrR("000")
			);
		}

		return repoD1100.save(d1100
				.setRlOwrDdc(getCorpOwnerCode(dto))
				.setRlOwrNm(Lotte_Seed128.encryptEcb(dto.getName()))
				.setRlOwrEnm(Lotte_Seed128.encryptEcb(dto.getEngName()))
				.setBird(Lotte_Seed128.encryptEcb(dto.getBirth()))
				.setRlOwrNatyC(dto.getNation())
				.setStchShrR(dto.getRate())
		);
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
		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
		if (!cardInfo.idx().equals(dto.getCardIssuanceInfoIdx())) {
			throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
		}

		Card card = cardInfo.card();
		if (ObjectUtils.isEmpty(card)) {
			card = Card.builder().build();
		}
		cardInfo.card(card.hopeLimit(dto.getHopeLimit()));

		if (StringUtils.hasText(card.grantLimit())) {
			Long calculatedLimitLong = Long.parseLong(card.calculatedLimit());
			Long hopeLimitLong = Long.parseLong(card.hopeLimit());
			card.grantLimit(calculatedLimitLong > hopeLimitLong ? card.hopeLimit() : card.calculatedLimit());
			updateRiskConfigLimit(user, card.grantLimit(), card.hopeLimit());
			updateD1100Limit(user, card.grantLimit(), card.hopeLimit());
		}

		if (StringUtils.hasText(depthKey)) {
			repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
		}

		return CardIssuanceDto.CardRes.from(repoCardIssuance.save(cardInfo));
	}

	private RiskConfig updateRiskConfigLimit(User user, String grantLimit, String hopeLimit) {
		Optional<RiskConfig> riskConfig = repoRiskConfig.findByCorpAndEnabled(user.corp(), true);
		if (riskConfig.isPresent()) {
			return repoRiskConfig.save(riskConfig.get()
					.hopeLimit(hopeLimit)
					.grantLimit(grantLimit)
			);
		}

		return null;
	}

	private Lotte_D1100 updateD1100Limit(User user, String grantLimit, String hopeLimit) {
		Lotte_D1100 d1100 = getD1100(user.corp().idx());
		if (ObjectUtils.isEmpty(d1100)) {
			return d1100;
		}

		d1100.setCpAkLimAm(CommonUtil.divisionString(hopeLimit, 10000))
				.setAkLimAm(CommonUtil.divisionString(grantLimit, 10000));
		return repoD1100.save(d1100);
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
		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
		if (!cardInfo.idx().equals(idx_CardInfo)) {
			throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
		}

		if (ObjectUtils.isEmpty(dto.getGreenCount()) && ObjectUtils.isEmpty(dto.getBlackCount())) {
			throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, "Green or Black, One of them must exist");
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
		updateD1100Card(user, grantLimit, calculatedLimit, hopeLimit, dto);

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
				.lotteGreenCount(dto.getGreenCount())
				.lotteBlackCount(dto.getBlackCount())
				.requestCount(dto.getBlackCount() + dto.getGreenCount()));

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

	private Lotte_D1100 updateD1100Card(User user, String grantLimit, String calculatedLimit, String hopeLimit, CardIssuanceDto.RegisterCard dto) {
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

		if (!ObjectUtils.isEmpty(dto.getGreenCount()) && !ObjectUtils.isEmpty(dto.getBlackCount())) {
			d1100 = Lotte_CardKind.setCardKindInLotte_D1100(d1100, Lotte_CardKind.GREEN, String.valueOf(dto.getGreenCount()), 1);
			d1100 = Lotte_CardKind.setCardKindInLotte_D1100(d1100, Lotte_CardKind.BLACK, String.valueOf(dto.getBlackCount()), 2);
		} else if (!ObjectUtils.isEmpty(dto.getGreenCount())) {
			d1100 = Lotte_CardKind.setCardKindInLotte_D1100(d1100, Lotte_CardKind.GREEN, String.valueOf(dto.getGreenCount()), 1);
		} else {
			d1100 = Lotte_CardKind.setCardKindInLotte_D1100(d1100, Lotte_CardKind.BLACK, String.valueOf(dto.getBlackCount()), 1);
		}

		return repoD1100.save(d1100);
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
		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
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

	private Lotte_D1100 updateD1100Account(Long idx_corp, ResAccount account) {
		Lotte_D1100 d1100 = getD1100(idx_corp);
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
		Lotte_D1100 d1100 = getD1100(user.corp().idx());
		Integer count = 1;
		CeoType ceoType = CeoType.SINGLE;
		if (d1100 != null) {
			// 신한전문에서 대표자 정보 가져오기
			ceoType = CeoType.fromLotte(CeoType.covertShinhanToLotte(getShinhanCeoCode(user)));

			if (StringUtils.hasText(d1100.getCstNm()) && StringUtils.hasText(d1100.getCstNm2()) && StringUtils.hasText(d1100.getCstNm3())) {
				count = 3;
			} else if (StringUtils.hasText(d1100.getCstNm()) && StringUtils.hasText(d1100.getCstNm2()) && !StringUtils.hasText(d1100.getCstNm3())) {
				count = 2;
			}
		}
		return CardIssuanceDto.CeoTypeRes.builder()
				.type(ceoType)
				.count(count)
				.build();
	}

	private String getShinhanCeoCode(User user) {
		D1000 shinhanD1000 = repoShinhanD1000.getTopByIdxCorpOrderByIdxDesc(user.corp().idx());
		if (!ObjectUtils.isEmpty(shinhanD1000)) {
			return shinhanD1000.getD009();
		}
		return "";
	}

	/**
	 * 대표자 신분증 저장
	 *
	 * @param idx_user 등록하는 User idx
	 * @param dto      등록정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public void saveCeoIdentification(HttpServletRequest request, Long idx_user, CardIssuanceDto.IdentificationReq dto, String depthKey) {
		User user = findUser(idx_user);

		Map<String, String> decryptData;
		if (dto.getIdentityType().equals(CertificationType.DRIVER)) {
			decryptData = SecuKeypad.decrypt(request, "encryptData", new String[]{EncryptParam.IDENTIFICATION_NUMBER, EncryptParam.DRIVER_NUMBER});
		} else {
			decryptData = SecuKeypad.decrypt(request, "encryptData", new String[]{EncryptParam.IDENTIFICATION_NUMBER});
		}

		// 1700(신분증검증)
		DataPart1700 resultOfD1700 = shinhanIssuanceService.proc1700(idx_user, dto, decryptData);

		if (!resultOfD1700.getD008().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
			throw BadRequestedException.builder().category(BadRequestedException.Category.INVALID_CEO_IDENTIFICATION).desc(resultOfD1700.getD009()).build();
		}

		Lotte_D1100 d1100 = getD1100(user.corp().idx());
		updateD1100Identification(d1100, dto, decryptData);

		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
		if (StringUtils.hasText(depthKey)) {
			repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
		}
	}

	// 1000 테이블에 대표자1,2,3 주민번호 저장(d11,15,19)
	private Lotte_D1100 updateD1100Identification(Lotte_D1100 d1100, CardIssuanceDto.IdentificationReq dto, Map<String, String> decryptData) {
		if (ObjectUtils.isEmpty(d1100)) {
			return d1100;
		}

		String idNum = dto.getIdentificationNumberFront() + decryptData.get(EncryptParam.IDENTIFICATION_NUMBER);
		String encryptIdNum = Lotte_Seed128.encryptEcb(idNum);
		String idfIsuBurNm = "정부24";
		String idfNo2 = Lotte_Seed128.encryptEcb(dto.getIssueDate());
		if (CertificationType.DRIVER.equals(dto.getIdentityType())) {
			idfIsuBurNm = getDriverLocalName(dto.getDriverLocal()) + "경찰청";
			idfNo2 = Lotte_Seed128.encryptEcb(getDriverLocalNumber(dto.getDriverLocal()) + decryptData.get(EncryptParam.DRIVER_NUMBER));
		}

		d1100.setHsVdPhc(dto.getIdentityType().getLotteCode());
		d1100.setIdfIsuBurNm(idfIsuBurNm);
		if ("1".equals(dto.getCeoSeqNo())) {
			d1100.setIdfKndcNm(dto.getIdentityType().getLotteCode());
			d1100.setIdfNo2(idfNo2);
			d1100.setDgRrno(encryptIdNum);
			d1100.setTkpRrno(encryptIdNum);
		} else if ("2".equals(dto.getCeoSeqNo())) {
			d1100.setDgRrno2(encryptIdNum);
		} else if ("3".equals(dto.getCeoSeqNo())) {
			d1100.setDgRrno3(encryptIdNum);
		} else {
			log.error("invalid ceoSeqNo. ceoSeqNo=" + dto.getCeoSeqNo());
			throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, "invalid ceoSeqNo. ceoSeqNo=" + dto.getCeoSeqNo());
		}

		return repoD1100.save(d1100);
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
		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
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

		Lotte_D1100 d1100 = getD1100(user.corp().idx());
		ceoNum = updateD1000Ceo(d1100, cardInfo, dto, ceo, ceoNum);

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
					.type(!ObjectUtils.isEmpty(d1100) ? CeoType.fromLotte(d1100.getDpwnm()) : null)
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
					.type(!ObjectUtils.isEmpty(d1100) ? CeoType.fromLotte(d1100.getDpwnm()) : null)
					.ceoNumber(ceoNum);
		}

		if (isUpdateRealCeo(cardInfo)) {
			cardInfo = setStockholderByCeoInfo(cardInfo, ceo);
		}

		if (StringUtils.hasText(depthKey)) {
			repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
		}

		return CardIssuanceDto.CeoRes.from(repoCeo.save(ceo)).setDeviceId("");
	}

	private Integer updateD1000Ceo(Lotte_D1100 d1100, CardIssuanceInfo cardInfo, CardIssuanceDto.RegisterCeo dto, CeoInfo ceo, Integer ceoNum) {
		if (d1100 == null) {
			return ceoNum;
		}

		String encryptName = Lotte_Seed128.encryptEcb(dto.getName());
		String encryptEngName = Lotte_Seed128.encryptEcb(dto.getEngName());

		if (!StringUtils.hasText(d1100.getCstEnm()) || (ceo != null && ceo.ceoNumber().equals(1))) { // 첫번째 대표자정보
			d1100 = d1100
					.setCstNm(encryptName)
					.setCstEnm(encryptEngName)
					.setNatyC(dto.getNation())
					.setMaFemDc(String.valueOf(dto.getGenderCode()))
					.setTkpNm(encryptName)
					.setTkpEnm(encryptEngName)
					.setTkpNatyC(dto.getNation())
					.setTkpMbzNo(Lotte_Seed128.encryptEcb(dto.getPhoneNumber().substring(0, 3)))
					.setTkpMexno(Lotte_Seed128.encryptEcb(dto.getPhoneNumber().substring(3, 7)))
					.setTkpMtlno(Lotte_Seed128.encryptEcb(dto.getPhoneNumber().substring(7)));

			if (isUpdateRealCeo(cardInfo)) {
				d1100 = d1100
						.setRlOwrNm(encryptName)
						.setRlOwrEnm(encryptEngName)
						.setBird(Lotte_Seed128.encryptEcb(dto.getBirth()))
						.setRlOwrNatyC(dto.getNation())
						.setRlMaFemDc(String.valueOf(dto.getGenderCode()))
						.setStchShrR("000");
			}

			repoD1100.save(d1100);
			ceoNum = 1;

		} else if (!StringUtils.hasText(d1100.getCstEnm2()) || (ceo != null && ceo.ceoNumber().equals(2))) { // 두번째 대표자정보
			repoD1100.save(d1100
					.setCstNm2(encryptName)
					.setCstEnm2(encryptEngName)
					.setNatyC2(dto.getNation())
					.setMaFemDc2(String.valueOf(dto.getGenderCode()))
			);
			ceoNum = 2;

		} else if (!StringUtils.hasText(d1100.getCstEnm3()) || (ceo != null && ceo.ceoNumber().equals(3))) { // 세번째 대표자정보
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

	private boolean isUpdateRealCeo(CardIssuanceInfo cardInfo) {
		return !ObjectUtils.isEmpty(cardInfo.stockholder())
				&& Boolean.FALSE.equals(cardInfo.stockholder().isStockHold25())
				&& Boolean.FALSE.equals(cardInfo.stockholder().isStockholderPersonal());
	}

	private String getBankName(String bankCode) {
		CommonCodeDetail commonCodeDetail = repoCodeDetail.getByCodeAndCode1(CommonCodeType.BANK_1, bankCode);
		if (commonCodeDetail != null) {
			return commonCodeDetail.value1();
		}
		return null;
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

		CardIssuanceInfo cardIssuanceInfo = findCardIssuanceInfo(user);
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
		Lotte_D1100 d1100 = getD1100(idx_corp);
		if (ObjectUtils.isEmpty(d1100)) {
			throw EntityNotFoundException.builder().entity("Lotte_D1100").build();
		}

		boolean isValidCeoInfo = !checkCeo(d1100.getCstNm(), d1100.getCstEnm(), d1100.getDgRrno(), dto)
				&& !checkCeo(d1100.getCstNm2(), d1100.getCstEnm2(), d1100.getDgRrno2(), dto)
				&& !checkCeo(d1100.getCstNm3(), d1100.getCstEnm3(), d1100.getDgRrno3(), dto);

		if (isValidCeoInfo) {
			throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, "MISMATCH_CEO");
		}
	}

	private boolean checkCeo(String korName, String engName, String idNum, CardIssuanceDto.CeoValidReq dto) {
		if (!StringUtils.hasText(idNum)) {
			return false;
		}

		idNum = Lotte_Seed128.decryptEcb(idNum);

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

	private CardIssuanceInfo findCardIssuanceInfo(User user) {
		return repoCardIssuance.findTopByUserAndDisabledFalseOrderByIdxDesc(user).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("CardIssuanceInfo")
						.build()
		);
	}

	private Lotte_D1100 getD1100(Long idx_corp) {
		return repoD1100.getTopByIdxCorpOrderByIdxDesc(idx_corp);
	}

	private ResAccount findResAccount(Long idx_resAccount) {
		return repoResAccount.findById(idx_resAccount).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("ResAccount")
						.idx(idx_resAccount)
						.build()
		);
	}

	private CardIssuanceInfo setStockholderByCeoInfo(CardIssuanceInfo cardIssuanceInfo, CeoInfo ceoInfo) {
		return cardIssuanceInfo.stockholder(cardIssuanceInfo.stockholder()
				.stockholderName(ceoInfo.name())
				.stockholderEngName(ceoInfo.engName())
				.stockholderBirth(ceoInfo.birth())
				.stockholderNation(ceoInfo.nationality())
				.stockRate("0000"));
	}

	private String getDriverLocalName(String code) {
		return repoCodeDetail.findFirstByValue1OrValue2AndCode(code, code, CommonCodeType.SHINHAN_DRIVER_LOCAL_CODE).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("CommonCodeDetail")
						.build()
		).value1();
	}

	private String getDriverLocalNumber(String code) {
		return repoCodeDetail.findFirstByValue1OrValue2AndCode(code, code, CommonCodeType.SHINHAN_DRIVER_LOCAL_CODE).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("CommonCodeDetail")
						.build()
		).value2();
	}
}
