package com.nomadconnection.dapp.api.service.lotte;

import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.dto.lotte.enums.Lotte_CardKind;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.MismatchedException;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.service.CommonCardService;
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
import com.nomadconnection.dapp.core.domain.repository.corp.ManagerRepository;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.nomadconnection.dapp.api.util.CommonUtil.getValueOrDefault;

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
	private final ManagerRepository repoManager;

	private final IssuanceService shinhanIssuanceService;
	private final CommonCardService commonCardService;


	/**
	 * 법인정보 등록
	 *
	 * @param idxUser     등록하는 User idx
	 * @param dto          등록정보
	 * @param idxCardInfo CardIssuanceInfo idx
	 */
	@Transactional(rollbackFor = Exception.class)
	public CardIssuanceDto.CorporationRes updateCorporation(Long idxUser, CardIssuanceDto.RegisterCorporation dto, Long idxCardInfo, String depthKey) {
		User user = findUser(idxUser);

		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
		if (!cardInfo.idx().equals(idxCardInfo)) {
			throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
		}

		updateD1100Corp(user.corp().idx(), dto);
		D1000 shinhanD1000 = repoShinhanD1000.getTopByIdxCorpOrderByIdxDesc(user.corp().idx());

		Corp corp = repoCorp.save(user.corp()
				.resCompanyEngNm(dto.getEngCorName())
				.resCompanyNumber(dto.getCorNumber())
				.resCompanyZipCode(dto.getCorZipCode())
				.resCompanyAddr(dto.getCorAddr())
				.resCompanyAddrDt(dto.getCorAddrDt())
				.resCompanyBuildingCode(dto.getCorBuildingCode())
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
	 * @param idxUser     등록하는 User idx
	 * @param dto          등록정보
	 * @param idxCardInfo CardIssuanceInfo idx
	 */
	@Transactional(rollbackFor = Exception.class)
	public CardIssuanceDto.CorporationExtendRes updateCorporationExtend(Long idxUser, CardIssuanceDto.RegisterCorporationExtend dto, Long idxCardInfo, String depthKey) {
		User user = findUser(idxUser);

		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
		if (!cardInfo.idx().equals(idxCardInfo)) {
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

	private Lotte_D1100 updateD1100CorpExtend(Long idxCorp, CardIssuanceDto.RegisterCorporationExtend dto) {
		Lotte_D1100 d1100 = getD1100(idxCorp);
		if (ObjectUtils.isEmpty(d1100)) {
			return d1100;
		}

		return repoD1100.save(d1100
						.setVtCurTtEnpYn(dto.getIsVirtualCurrency() ? "Y" : "N")
				//.setListStexC(dto.getIsListedCompany() ? dto.getListedCompanyCode() : null)
		);
	}

	private String getListedCompanyName(String ListedCompanyCode) {
		CommonCodeDetail commonCodeDetail = repoCodeDetail.getByCodeAndCode1(CommonCodeType.LOTTE_LISTED_EXCHANGE, ListedCompanyCode);
		if (ObjectUtils.isEmpty(commonCodeDetail)) {
			return null;
		}
		return commonCodeDetail.value1();
	}


	private Lotte_D1100 updateD1100Corp(Long idxCorp, CardIssuanceDto.RegisterCorporation dto) {
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

	/**
	 * 벤처기업정보 등록
	 *
	 * @param idxUser     등록하는 User idx
	 * @param dto          등록정보
	 * @param idxCardInfo CardIssuanceInfo idx
	 * @return 등록 정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public CardIssuanceDto.VentureRes registerVenture(Long idxUser, CardIssuanceDto.RegisterVenture dto, Long idxCardInfo, String depthKey) {
		User user = findUser(idxUser);
		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
		if (!cardInfo.idx().equals(idxCardInfo)) {
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

	private Lotte_D1100 updateD1100Venture(Long idxCorp, CardIssuanceDto.RegisterVenture dto) {
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
	 * @param idxUser     등록하는 User idx
	 * @param dto          등록정보
	 * @param idxCardInfo CardIssuanceInfo idx
	 * @return 등록 정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public CardIssuanceDto.VentureRes registerStockholder(Long idxUser, CardIssuanceDto.RegisterStockholder dto, Long idxCardInfo, String depthKey) {
		User user = findUser(idxUser);
		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
		if (!cardInfo.idx().equals(idxCardInfo)) {
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

		List<CeoInfo> ceoInfos = repoCeo.getByCardIssuanceInfo(cardInfo);
		for (CeoInfo ceoInfo : ceoInfos) {
			if (commonCardService.isRealOwnerConvertCeo(cardInfo, ceoInfo)) {
				cardInfo = setStockholderByCeoInfo(cardInfo, ceoInfo);
				break;
			}
		}

		updateRiskConfigStockholder(user, dto);
		updateD1100Stockholder(user.corp().idx(), cardInfo, ceoInfos, dto);

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

	private Lotte_D1100 updateD1100Stockholder(Long idxCorp, CardIssuanceInfo cardInfo, List<CeoInfo> ceoInfos,
											   CardIssuanceDto.RegisterStockholder dto) {
		Lotte_D1100 d1100 = getD1100(idxCorp);
		if (ObjectUtils.isEmpty(d1100)) {
			return d1100;
		}

		for (CeoInfo ceoInfo : ceoInfos) {
			if (commonCardService.isRealOwnerConvertCeo(cardInfo, ceoInfo)) {
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

	/**
	 * 카드 희망한도 저장
	 *
	 * @param idxUser 등록하는 User idx
	 * @param dto      등록정보
	 * @return 등록 정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public CardIssuanceDto.CardRes saveHopeLimit(Long idxUser, CardIssuanceDto.HopeLimitReq dto, String depthKey) {
		User user = findUser(idxUser);
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
	 * @param idxUser     등록하는 User idx
	 * @param dto          등록정보
	 * @param idxCardInfo CardIssuanceInfo idx
	 * @return 등록 정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public CardIssuanceDto.CardRes registerCard(Long idxUser, CardIssuanceDto.RegisterCard dto, Long idxCardInfo, String depthKey) {
		User user = findUser(idxUser);
		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
		if (!cardInfo.idx().equals(idxCardInfo)) {
			throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
		}

		if (ObjectUtils.isEmpty(dto.getGreenCount()) && ObjectUtils.isEmpty(dto.getBlackCount())) {
			throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, "Green or Black, One of them must exist");
		}

		Double cardLimitNow = repoRisk.findCardLimitNowFirst(idxUser, CommonUtil.getNowYYYYMMDD());
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

		d1100 = Lotte_CardKind.initCardKindInLotte_D1100(d1100);

		if (!ObjectUtils.isEmpty(dto.getGreenCount()) && !ObjectUtils.isEmpty(dto.getBlackCount())) {
			int seq = 1;
			if (dto.getGreenCount() > 0) {
				d1100 = Lotte_CardKind.setCardKindInLotte_D1100(d1100, Lotte_CardKind.GREEN, getCardReqCount(dto.getGreenCount()), seq);
				seq++;
			}
			if (dto.getBlackCount() > 0) {
				d1100 = Lotte_CardKind.setCardKindInLotte_D1100(d1100, Lotte_CardKind.BLACK, getCardReqCount(dto.getBlackCount()), seq);
			}
		}

		return repoD1100.save(d1100);
	}

	private String getCardReqCount(Long count) { // 롯데카드 신청수량이 N개인 경우 N-1개로 세팅
		return String.valueOf(count - 1);
	}

	/**
	 * 결제 계좌정보 등록
	 *
	 * @param idxUser     등록하는 User idx
	 * @param dto          등록정보
	 * @param idxCardInfo CardIssuanceInfo idx
	 * @return 등록 정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public CardIssuanceDto.AccountRes registerAccount(Long idxUser, CardIssuanceDto.RegisterAccount dto, Long idxCardInfo, String depthKey) {
		User user = findUser(idxUser);
		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
		if (!cardInfo.idx().equals(idxCardInfo)) {
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

	private Lotte_D1100 updateD1100Account(Long idxCorp, ResAccount account) {
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

	/**
	 * 대표자 정보
	 *
	 * @param idxUser 조회하는 User idx
	 * @return 등록 정보
	 */
	@Transactional(readOnly = true)
	public CardIssuanceDto.CeoTypeRes getCeoType(Long idxUser) {
		User user = findUser(idxUser);
		if (user.corp() == null) {
			throw EntityNotFoundException.builder().entity("Corp").build();
		}
		Lotte_D1100 d1100 = getD1100(user.corp().idx());
		Integer count = user.corp().ceoCount();
		CeoType ceoType = CeoType.SINGLE;
		if (d1100 != null) {
			// 신한전문에서 대표자 정보 가져오기
			D1000 shinhanD1000 = repoShinhanD1000.getTopByIdxCorpOrderByIdxDesc(user.corp().idx());
			ceoType = CeoType.fromLotte(CeoType.convertShinhanToLotte(getShinhanCeoCode(shinhanD1000)));
		}
		return CardIssuanceDto.CeoTypeRes.builder()
				.type(ceoType)
				.count(count)
				.build();
	}


	private String getShinhanCeoCode(D1000 shinhanD1000) {
		if (!ObjectUtils.isEmpty(shinhanD1000)) {
			return shinhanD1000.getD009();
		}
		return "";
	}

	/**
	 * 대표자 신분증 저장
	 *
	 * @param idxUser 등록하는 User idx
	 * @param dto      등록정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public void saveCeoIdentification(HttpServletRequest request, Long idxUser, CardIssuanceDto.IdentificationReq dto, String depthKey) {
		User user = findUser(idxUser);

		Map<String, String> decryptData;
		if (dto.getIdentityType().equals(CertificationType.DRIVER)) {
			decryptData = SecuKeypad.decrypt(request, "encryptData", new String[]{EncryptParam.IDENTIFICATION_NUMBER, EncryptParam.DRIVER_NUMBER});
			dto.setDriverLocal(commonCardService.findShinhanDriverLocalCode(dto.getDriverLocal()));
		} else {
			decryptData = SecuKeypad.decrypt(request, "encryptData", new String[]{EncryptParam.IDENTIFICATION_NUMBER});
		}

		shinhanIssuanceService.verifyCeo(idxUser, dto, decryptData);
		Lotte_D1100 d1100 = getD1100(user.corp().idx());
		updateD1100Identification(d1100, dto, decryptData);

		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
		if (StringUtils.hasText(depthKey)) {
			repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
		}
	}

	/**
	 * 관리책임자 변경
	 *
	 * @param idxUser     등록하는 User idx
	 * @param dto          등록정보
	 * @param idxCardInfo CardIssuanceInfo idx
	 * @return 등록 정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public CardIssuanceDto.ManagerRes registerManager(Long idxUser, CardIssuanceDto.RegisterManager dto, Long idxCardInfo, String depthKey) {
		User user = findUser(idxUser);
		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
		if (!cardInfo.idx().equals(idxCardInfo)) {
			throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
		}

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

		ManagerInfo manager = ManagerInfo.builder()
				.cardIssuanceInfo(cardInfo)
				.engName(dto.getEngName())
				.name(dto.getName())
				.nation(dto.getNation())
				.phoneNumber(dto.getPhoneNumber())
				.genderCode(dto.getGenderCode())
				.birth(dto.getBirth())
				.build();

		if (StringUtils.hasText(depthKey)) {
			repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
		}

		return CardIssuanceDto.ManagerRes.from(repoManager.save(manager));
	}

	private void updateD1100Manager(Lotte_D1100 d1100, User user, CardIssuanceDto.RegisterManager dto, String idNum) {
		if (d1100 != null) {
			String[] corNumber = user.corp().resCompanyNumber().split("-");
			repoD1100.save(d1100
					.setTkpNm(dto.getName()) // 수령자명
					.setTkpEnm(dto.getEngName()) // 수령자영문명
					.setTkpRrno(idNum) // 수령자주민번호
					.setTkpDpnm(getValueOrDefault(dto.getDepartment(), "대표이사")) // 수령자부서명
					.setTkpPsiNm(getValueOrDefault(dto.getTitle(), "대표이사")) // 수령자직위명
					.setTkpNatyC(dto.getNation()) // 수령자국적코드
					.setTkpMlId(user.email()) // 수령자이메일
					.setTkpDdd(corNumber[0]) // 수령자전화지역번호
					.setTkpExno(corNumber[1]) // 수령자전화국번
					.setTkpTlno(corNumber[2]) // 수령자전화개별번호
                    .setTkpMbzNo(Lotte_Seed128.encryptEcb(dto.getPhoneNumber().substring(0, 3))) // 수령자이동사업자번호
                    .setTkpMexno(Lotte_Seed128.encryptEcb(dto.getPhoneNumber().substring(3, 7))) // 수령자이동전화국번
                    .setTkpMtlno(Lotte_Seed128.encryptEcb(dto.getPhoneNumber().substring(7))) // 수령자이동전화개별번호
			);
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
		if ("0".equals(dto.getCeoSeqNo())) {
			d1100.setTkpRrno(encryptIdNum);
		} else if ("1".equals(dto.getCeoSeqNo())) {
			d1100.setIdfKndcNm(dto.getIdentityType().getLotteCode());
			d1100.setIdfNo2(idfNo2);
            d1100.setTkpRrno(encryptIdNum);
            d1100.setDgRrno(encryptIdNum);
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
	 * @param idxUser     등록하는 User idx
	 * @param dto          등록정보
	 * @param idxCardInfo CardIssuanceInfo idx
	 * @return 등록 정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public CardIssuanceDto.CeoRes registerCeo(Long idxUser, CardIssuanceDto.RegisterCeo dto, Long idxCardInfo, String depthKey) {
		User user = findUser(idxUser);
		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
		if (!cardInfo.idx().equals(idxCardInfo)) {
			throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
		}

		CeoInfo ceo = null;
		Integer ceoNum = 0;
		if (!ObjectUtils.isEmpty(dto.getCeoIdx())) {
			ceo = findCeoInfo(dto.getCeoIdx());
			if (!cardInfo.ceoInfos().contains(ceo)) {
				throw MismatchedException.builder().category(MismatchedException.Category.CEO).build();
			}
			if (ceo.ceoNumber() > 0) {
				ceoNum = ceo.ceoNumber();
			}
		}

		D1000 shinhanD1000 = repoShinhanD1000.getTopByIdxCorpOrderByIdxDesc(user.corp().idx());
		String ceoTypeCode = CeoType.convertShinhanToLotte(getShinhanCeoCode(shinhanD1000)); // 대표자 유형

		Lotte_D1100 d1100 = getD1100(user.corp().idx());
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

		if (commonCardService.isStockholderUpdateCeo(cardInfo)) {
			cardInfo = setStockholderByCeoInfo(cardInfo, ceo);
		}

		if (StringUtils.hasText(depthKey)) {
			repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
		}

		return CardIssuanceDto.CeoRes.from(repoCeo.save(ceo)).setDeviceId("");
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
					.setMaFemDc(String.valueOf(dto.getGenderCode()))
					.setTkpNm(encryptName)
					.setTkpEnm(encryptEngName)
					.setTkpNatyC(dto.getNation())
					.setTkpMbzNo(Lotte_Seed128.encryptEcb(dto.getPhoneNumber().substring(0, 3)))
					.setTkpMexno(Lotte_Seed128.encryptEcb(dto.getPhoneNumber().substring(3, 7)))
					.setTkpMtlno(Lotte_Seed128.encryptEcb(dto.getPhoneNumber().substring(7)));

			if (commonCardService.isStockholderUpdateCeo(cardInfo)) {
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
	 * @param idxUser 조회하는 User idx
	 * @param dto      대표자 타당성 확인 정보
	 */
	@Deprecated
	@Transactional(rollbackFor = Exception.class)
	public void verifyValidCeo(Long idxUser, CardIssuanceDto.CeoValidReq dto, String depthKey) {
		User user = findUser(idxUser);
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

	public void verifyCorrespondCeo(Long idxCorp, CardIssuanceDto.CeoValidReq dto) {
		D1000 shinhanD1000 = repoShinhanD1000.getTopByIdxCorpOrderByIdxDesc(idxCorp);
		if (ObjectUtils.isEmpty(shinhanD1000)) {
			throw EntityNotFoundException.builder().entity("shinhanD1000").build();
		}

		boolean isValidCeoInfo = !checkCeo(shinhanD1000.getD010(), shinhanD1000.getD012(), shinhanD1000.getD011(), dto)
				&& !checkCeo(shinhanD1000.getD014(), shinhanD1000.getD016(), shinhanD1000.getD015(), dto)
				&& !checkCeo(shinhanD1000.getD018(), shinhanD1000.getD020(), shinhanD1000.getD019(), dto);

		// TODO: 스크래핑시 롯데전문으로 받는 로직으로 바뀌면 아래와 같이 코드 수정
//		{
//			Lotte_D1100 d1100 = getD1100(idxCorp);
//			if (ObjectUtils.isEmpty(d1100)) {
//				throw EntityNotFoundException.builder().entity("Lotte_D1100").build();
//			}
//
//			boolean isValidCeoInfo = !checkCeo(d1100.getCstNm(), d1100.getCstEnm(), d1100.getDgRrno(), dto)
//					&& !checkCeo(d1100.getCstNm2(), d1100.getCstEnm2(), d1100.getDgRrno2(), dto)
//					&& !checkCeo(d1100.getCstNm3(), d1100.getCstEnm3(), d1100.getDgRrno3(), dto);
//		}

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


	private User findUser(Long idxUser) {
		return repoUser.findById(idxUser).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("User")
						.idx(idxUser)
						.build()
		);
	}

	private CeoInfo findCeoInfo(Long idxCeo) {
		return repoCeo.findById(idxCeo).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("CeoInfo")
						.idx(idxCeo)
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

	private Lotte_D1100 getD1100(Long idxCorp) {
		return repoD1100.getTopByIdxCorpOrderByIdxDesc(idxCorp);
	}

	private ResAccount findResAccount(Long idxResAccount) {
		return repoResAccount.findById(idxResAccount).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("ResAccount")
						.idx(idxResAccount)
						.build()
		);
	}

	private CardIssuanceInfo setStockholderByCeoInfo(CardIssuanceInfo cardIssuanceInfo, CeoInfo ceoInfo) {
		return cardIssuanceInfo.stockholder(cardIssuanceInfo.stockholder()
				.stockholderName(ceoInfo.name())
				.stockholderEngName(ceoInfo.engName())
				.stockholderBirth(ceoInfo.birth())
				.stockholderNation(ceoInfo.nationality())
				.stockRate("000"));
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
}
