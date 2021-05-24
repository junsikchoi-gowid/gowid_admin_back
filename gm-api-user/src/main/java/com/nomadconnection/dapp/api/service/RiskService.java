package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.common.CommonCodeDetailRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResAccountHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResAccountRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskConfigRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1000Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1400Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1530Repository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.res.ResAccountHistory;
import com.nomadconnection.dapp.core.domain.risk.Risk;
import com.nomadconnection.dapp.core.domain.risk.RiskConfig;
import com.nomadconnection.dapp.core.domain.shinhan.D1530;
import com.nomadconnection.dapp.core.domain.user.Role;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiskService {

	private final CorpRepository repoCorp;
	private final RiskRepository repoRisk;
	private final UserRepository repoUser;
	private final RiskConfigRepository repoRiskConfig;
	private final ResAccountRepository repoResAccount;
	private final ResAccountHistoryRepository repoResAccountHistory;
	private final CommonCodeDetailRepository repoCommonCodeDetail;
	private final D1000Repository repoD1000;
	private final D1400Repository repoD1400;
	private final D1530Repository repoD1530;
	private final CardIssuanceInfoService cardIssuanceInfoService;
	private final CardIssuanceInfoRepository cardIssuanceInfoRepository;

	final List<String> ACCOUNT_TYPE = Arrays.asList("10", "11", "12", "13", "14");
	final Double minCashBalance = 50000000d;
	final Double maxLimit = 200000000d;


	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity saveRisk(Long idxUser, Long idxCorp, String calcDate) {

		return ResponseEntity.ok().body(
				BusinessResponse.builder()
						.data(saveRiskData(idxUser,idxCorp,calcDate))
						.build());
	}

	@Transactional(rollbackFor = Exception.class)
	public Risk saveRiskData(Long idxUser, Long idxCorp, String calcDate) {
		Corp corp;
		Long finalIdxUser = idxUser;
		User userAuth = findUser(finalIdxUser);
		User user;

		if(idxCorp != null && userAuth.authorities().stream().noneMatch(o-> o.role().equals(Role.GOWID_ADMIN))) {
			throw UserNotFoundException.builder().build();
		}else if(idxCorp != null && userAuth.authorities().stream().anyMatch(o-> o.role().equals(Role.GOWID_ADMIN))){
			corp = repoCorp.findById(idxCorp).orElseThrow(
					() -> CorpNotRegisteredException.builder().account(idxCorp.toString()).build()
			);
			Corp finalCorp = corp;
			user = repoUser.findById(repoCorp.searchIdxUser(idxCorp)).orElseThrow(
					() -> UserNotFoundException.builder().id(finalCorp.user().idx()).build()
			);

			idxUser = repoCorp.searchIdxUser(idxCorp);
		}else{
			user = repoUser.findById(idxUser).orElseThrow(
					() -> UserNotFoundException.builder()
							.id(finalIdxUser)
							.build()
			);
			corp = user.corp();
		}

		if(StringUtils.isEmpty(calcDate)){
			calcDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		}

		RiskConfig riskconfig = repoRiskConfig.findByUserAndEnabled(user, true).orElseGet(
				() -> RiskConfig.builder()
						.depositGuarantee(0F)
						.depositPayment(false)
						.vcInvestment(false)
						.ventureCertification(false)
						.cardIssuance(false)
						.ceoGuarantee(false)
						.enabled(true)
						.calculatedLimit("0")
						.grantLimit("0")
						.hopeLimit("0")
						.user(user)
						.corp(corp)
						.build()
		);

		riskconfig.user(user);
		riskconfig.corp(corp);

		Optional<Risk> riskOptional = repoRisk.findByUserAndDate(User.builder().idx(idxUser).build(), calcDate);
		Risk risk ;
		if(riskOptional.isPresent()){
			risk = riskOptional.get();
		}else{
			risk = Risk.builder()
					.user(user)
					.corp(user.corp())
					.date(calcDate)
					.ceoGuarantee(riskconfig.ceoGuarantee())
					.depositGuarantee(riskconfig.depositGuarantee())
					.depositPayment(riskconfig.depositPayment())
					.cardIssuance(riskconfig.cardIssuance())
					.ventureCertification(riskconfig.ventureCertification())
					.vcInvestment(riskconfig.vcInvestment())
					.recentBalance(0)
					.build();

		}
		risk.ventureCertification(riskconfig.ventureCertification());
		risk.vcInvestment(riskconfig.vcInvestment());

		risk.user(user);
		risk.corp(corp);

		// 당일 한도는 계좌 정보중 상태값이 Normal 의 경우만 가져와서 합산함
		if( !ObjectUtils.isEmpty(calcDate) && calcDate.equals(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE).replace("-",""))){
			risk.recentBalance(repoResAccount.findRecentBalanceToDay(corp.idx()));
		}else {
			risk.recentBalance(repoResAccount.findRecentBalance(idxUser, calcDate));
		}

		risk.actualBalance(risk.recentBalance());

		CardIssuanceInfo cardIssuanceInfo = cardIssuanceInfoService.findByUserOrElseThrow(user, CardType.GOWID);

		if(cardIssuanceInfo.issuanceStatus().equals(IssuanceStatus.APPLY) || cardIssuanceInfo.issuanceStatus().equals(IssuanceStatus.ISSUED)){
			riskconfig.cardIssuance(true);
			risk.cardIssuance(true);
			risk.confirmedLimit(Double.parseDouble(riskconfig.grantLimit()));
		}

		// D1530 d1530 = repoD1530.findTopByIdxCorp(corp.idx()).orElseThrow(() -> new RuntimeException("D1530 corp not found"));
		D1530 d1530 = repoD1530.findTopByIdxCorp(corp.idx()).orElse(null);


		//최신잔고를 구함
		risk.currentBalance(risk.recentBalance());
		risk.cashBalance(risk.recentBalance());
		risk.recentBalance(getRecentBalance(corp, d1530, risk.recentBalance()));

		//Grade
		risk.grade(getGrade(d1530, riskconfig, risk.recentBalance(), corp.resBusinessCode()));

		//실제계산금액
		risk.cardLimitNow(getCardLimitNowVer2(risk.grade(),risk.recentBalance(),riskconfig.depositGuarantee()));

		boolean needToStop = risk.cashBalance() < risk.minCashNeed() || risk.recentBalance() < risk.cardLimitNow();
		risk.emergencyStop(needToStop);

		repoRisk.save(risk);
		corp.riskConfig(repoRiskConfig.save(riskconfig));
		repoCorp.save(corp);

		return risk;
	}

	private double getCardLimitNowVer2(String grade, double recentBalance, double depositGuarantee) {

		// 계산값은 보증금을 최우선으로 함
		if( depositGuarantee>0){
			return depositGuarantee;
		}else{
			if(getCardLimitNowPercent(grade) > 0 ){
				return Math.min(Math.max(
						(Math.floor((recentBalance * getCardLimitNowPercent(grade) / 100 ) / 1000000) * 1000000), depositGuarantee)
						,maxLimit);
			}else {
				return 0d;
			}
		}
	}

	@Transactional(readOnly = true)
	public String getCardLimit(Long idxUser) {
		User user = findUser(idxUser);
		double cardLimit = repoRisk.findCardLimitNowFirst(idxUser, CommonUtil.getNowYYYYMMDD());
		double maxLimit = Double.parseDouble(
			repoCommonCodeDetail.getByCodeAndCode1(CommonCodeType.CARD_LIMIT, user.cardCompany().getName()).value1());
		if (cardLimit > maxLimit) {
			cardLimit = maxLimit;
		}

		return cardLimit + "";
	}

	@Transactional(readOnly = true)
	User findUser(Long idx_user) {
		return repoUser.findById(idx_user).orElseThrow(
				() -> UserNotFoundException.builder()
						.id(idx_user)
						.build()
		);
	}


	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> saveRisk45(Long idxUser, Long idxCorp, String calcDate) {

		Risk risk = saveRiskData(idxUser, idxCorp, calcDate);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(risk).build());
	}

	@Transactional(readOnly = true)
	public ResponseEntity<?> getGrantLimit(Long idxUser) {

		User user = repoUser.findById(idxUser).orElseThrow(
				() -> UserNotFoundException.builder().build()
		);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(
				repoRiskConfig.getTopByCorpAndEnabled(user.corp(),true).grantLimit()
		).build());
	}

	private Integer getCardLimitNowPercent(String grade ) {
		String strPercent = repoCommonCodeDetail.findFirstByCode1AndCode(grade, CommonCodeType.RISK_GRADE).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("CommonCodeDetail")
						.build()
		).value1();

		return Integer.parseInt(strPercent);
	}

	private String getGrade(D1530 d1530, RiskConfig riskConfig, Double balance, String businessCode) {

		boolean isConstructionSector = businessCode.toUpperCase().startsWith("F41") || businessCode.toUpperCase().startsWith("F42");

		if (minCashBalance > balance) {
			return "F";
		}else if(isConstructionSector){
			return "G";
		}else if( d1530 != null &&
				Integer.parseInt(LocalDate.now().minusMonths(3).format(DateTimeFormatter.BASIC_ISO_DATE)) < Integer.parseInt(d1530.getD057())){
			return "E";
		}else if(riskConfig.ventureCertification() && riskConfig.vcInvestment()){
			return "A";
		}else if(!riskConfig.ventureCertification() && riskConfig.vcInvestment()){
			return "B";
		}else if(riskConfig.ventureCertification() && !riskConfig.vcInvestment()){
			return "C";
		}else{
			return "D";
		}
	}



	private double getRecentBalance(Corp corp, D1530 d1530, Double balance) {
		AtomicReference<Double> recentBalance = new AtomicReference<>(balance);

		if( d1530 != null){
			List<BankDto.ResAccountDto> listResAccount = repoResAccount.findResAccount(corp.user().idx()).stream()
					.map(account -> BankDto.ResAccountDto.from(account, false))
					.collect(Collectors.toList());

			Double ceoRecentBalance = 0d;

			for(BankDto.ResAccountDto resAccount : listResAccount){
				if( ACCOUNT_TYPE.contains(resAccount.getResAccountDeposit())){
					ceoRecentBalance += getMinusRecentBalance(resAccount, d1530);
					log.info("[getRecentBalance] $ceoRecentBalance = {}", ceoRecentBalance);
				}
			}
			log.info("[getRecentBalance] $ceoRecentBalance = {}", ceoRecentBalance);

			recentBalance.set(recentBalance.get() - ceoRecentBalance );
		}

		return recentBalance.get() ;
	}

	private Double getMinusRecentBalance(BankDto.ResAccountDto resAccount, D1530 d1530){

		if(d1530.getD057() != null
				&& Integer.parseInt(LocalDate.now().minusMonths(3).format(DateTimeFormatter.BASIC_ISO_DATE)) > Integer.parseInt(d1530.getD057())){

			String[] ceoList = new String[]{d1530.getD046(), d1530.getD050(), d1530.getD050()};
			List<Long> idxList = new ArrayList<>();
			List<Long> resultList;

			for (String ceo: ceoList) {
				if(!ceo.isEmpty()){
					List<ResAccountHistory> resAccountHistoryDesc1 = repoResAccountHistory.findByResAccountAndResAccountTrDateBetweenAndResAccountInGreaterThanAndResAccountDesc1(
							resAccount.getResAccount(),
							LocalDate.now().minusMonths(1).format(DateTimeFormatter.BASIC_ISO_DATE),
							CommonUtil.getNowYYYYMMDD(),
							"0",
							ceo
					);

					List<ResAccountHistory> resAccountHistoryDesc2 = repoResAccountHistory.findByResAccountAndResAccountTrDateBetweenAndResAccountInGreaterThanAndResAccountDesc2(
							resAccount.getResAccount(),
							LocalDate.now().minusMonths(1).format(DateTimeFormatter.BASIC_ISO_DATE),
							CommonUtil.getNowYYYYMMDD(),
							"0",
							ceo
					);

					List<ResAccountHistory> resAccountHistoryDesc3 = repoResAccountHistory.findByResAccountAndResAccountTrDateBetweenAndResAccountInGreaterThanAndResAccountDesc3(
							resAccount.getResAccount(),
							LocalDate.now().minusMonths(1).format(DateTimeFormatter.BASIC_ISO_DATE),
							CommonUtil.getNowYYYYMMDD(),
							"0",
							ceo
					);

					List<ResAccountHistory> resAccountHistoryDesc4 = repoResAccountHistory.findByResAccountAndResAccountTrDateBetweenAndResAccountInGreaterThanAndResAccountDesc4(
							resAccount.getResAccount(),
							LocalDate.now().minusMonths(1).format(DateTimeFormatter.BASIC_ISO_DATE),
							CommonUtil.getNowYYYYMMDD(),
							"0",
							ceo
					);

					for(ResAccountHistory accountHistory :resAccountHistoryDesc1){
						idxList.add(accountHistory.idx());
					}

					for(ResAccountHistory accountHistory :resAccountHistoryDesc2){
						idxList.add(accountHistory.idx());
					}

					for(ResAccountHistory accountHistory :resAccountHistoryDesc3){
						idxList.add(accountHistory.idx());
					}

					for(ResAccountHistory accountHistory :resAccountHistoryDesc4){
						idxList.add(accountHistory.idx());
					}
				}
			}

			if( idxList.size() > 0 ){
				resultList = idxList.stream().distinct().collect(Collectors.toList());
				String ceoInBalance = repoResAccountHistory.sumCeoInBalance(resultList);
				log.info("[getMinusRecentBalance] $ceoInBalance={}", ceoInBalance);
				return Double.parseDouble(ceoInBalance)/2;
			}
			return 0d;
		}

		return 0.0;
	}

	public Risk findRiskByUserAndDateLessThanEqual(User user, String date) {
		return repoRisk.findTopByUserAndDateLessThanEqualOrderByDateDesc(user, date).orElseThrow(
			() -> EntityNotFoundException.builder()
				.entity("Risk")
				.build()
		);
	}

}


