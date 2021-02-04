package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.dto.RiskDto;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.common.IssuanceProgress;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.common.CommonCodeDetailRepository;
import com.nomadconnection.dapp.core.domain.repository.common.IssuanceProgressRepository;
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
import com.nomadconnection.dapp.core.domain.shinhan.D1000;
import com.nomadconnection.dapp.core.domain.shinhan.D1400;
import com.nomadconnection.dapp.core.domain.shinhan.D1530;
import com.nomadconnection.dapp.core.domain.user.Role;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.nomadconnection.dapp.core.domain.common.IssuanceProgressType.LP_ZIP;
import static com.nomadconnection.dapp.core.domain.common.IssuanceProgressType.P_1800;
import static com.nomadconnection.dapp.core.domain.common.IssuanceStatusType.SUCCESS;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiskService {

	private final CorpRepository repoCorp;
	private final RiskRepository repoRisk;
	private final UserRepository repoUser;
	private final RiskConfigRepository repoRiskConfig;
	private final ResAccountRepository repoResAccount;
	private final IssuanceProgressRepository repoIssuanceProgress;
	private final ResAccountHistoryRepository repoResAccountHistory;
	private final CommonCodeDetailRepository repoCommonCodeDetail;
	private final D1000Repository repoD1000;
	private final D1400Repository repoD1400;
	private final D1530Repository repoD1530;

	final List<String> ACCOUNT_TYPE = Arrays.asList("10", "11", "12", "13", "14");
	final Double minBalance = 10000000d;
	final Double minCashBalance = 50000000d;
	final Double maxLimit = 200000000d;

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> saveRiskConfig(RiskDto.RiskConfigDto riskConfig){
		return ResponseEntity.ok().body(BusinessResponse.builder().data(repoRiskConfig.save(
				RiskConfig.builder()
						.user(User.builder().idx(riskConfig.getIdxUser()).build())
						.ceoGuarantee(riskConfig.isCeoGuarantee())
						.depositGuarantee(riskConfig.getDepositGuarantee())
						.depositPayment(riskConfig.isDepositPayment())
						.cardIssuance(riskConfig.isCardIssuance())
						.ventureCertification(riskConfig.isVentureCertification())
						.vcInvestment(riskConfig.isVcInvestment())
						.enabled(riskConfig.isEnabled())
						.build()
		)).build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> saveRisk(Long idxUser, Long idxCorp, String calcDate) {

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

		IssuanceProgress issuanceProgress = repoIssuanceProgress.findById(user.idx()).orElse(null);

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

		// 46일
		// List<ResAccountRepository.CRisk> cRisk45days = repoResAccount.find45dayValance(idxUser, calcDate);

		// 45일
		// Stream<ResAccountRepository.CRisk> cRisk45daysTemp = cRisk45days.stream();

//		if(riskconfig.ventureCertification() && riskconfig.vcInvestment()){
//			risk.grade("A");
//			risk.gradeLimitPercentage(10);
//			risk.minStartCash(100000000);
//			risk.minCashNeed(50000000);
//		}else if(riskconfig.ventureCertification() && !riskconfig.vcInvestment()){
//			risk.grade("B");
//			risk.gradeLimitPercentage(5);
//			risk.minStartCash(100000000);
//			risk.minCashNeed(50000000);
//		}else{
//			risk.grade("C");
//			risk.gradeLimitPercentage(5);
//			risk.minStartCash(500000000);
//			risk.minCashNeed(50000000);
//		}

//		if(cRisk45days.size() > 0 ){
//			risk.currentBalance(cRisk45days.get(0).getCurrentBalance());
//			risk.errCode(cRisk45days.get(0).getErrCode());
//		}else{
//			risk.currentBalance(0F);
//		}


		// Error
//		risk.error(repoRisk.findErrCount(idxUser));

//		// 45DMA
//		List<Double> arrList = new ArrayList<>();
//		cRisk45daysTemp.forEach( cRisk -> arrList.add((double) cRisk.getCurrentBalance()));
//
//		risk.dma45(arrList.stream().mapToDouble(Double::doubleValue).average().orElse(0));
//
//		// 45DMM
//		AtomicInteger i = new AtomicInteger(0);
//		arrList.stream().sorted().forEach( l -> {
//			log.debug("sort order $={} $={}" , i.getAndIncrement(), l);
//			if(i.get() == 23){
//				risk.dmm45(l);
//			}
//		});

//		if(repoResAccount.findRecentBalance(idxUser, calcDate) != null ) {
//			risk.recentBalance(repoResAccount.findRecentBalance(idxUser, calcDate));
//		}
//
//		// ActualBalance
//		if(risk.depositPayment()){
//			risk.actualBalance(risk.recentBalance()-risk.depositGuarantee());
//		}else {
//			risk.actualBalance(risk.recentBalance());
//		}
//
//		// CashBalance
//		ArrayList<Double> cashBalance = new ArrayList<>();
//		cashBalance.add(risk.dma45());
//		cashBalance.add(risk.dmm45());
//		cashBalance.add(risk.actualBalance());
//		risk.cashBalance(Collections.min(cashBalance));

		// CardLimitCalculation
//		risk.cardLimitCalculation( risk.cashBalance() * risk.gradeLimitPercentage()/100);

		// RealtimeLimit
//		risk.realtimeLimit(Math.floor(risk.cardLimitCalculation() / 1000000) * 1000000);

		// CardLimit
//		risk.cardLimit(Math.max(risk.depositGuarantee(),risk.realtimeLimit()));

//		boolean needToStop = risk.cashBalance() < risk.minCashNeed() || risk.recentBalance() < risk.cardLimitNow();
//		risk.emergencyStop(needToStop);

		// CardLimitNow
//		Double cardLimitNow = repoRisk.findCardLimitNow(idxUser,calcDate);
//		Double cardLimitNow = 0.0;
//		Double cardLimitNowFirst = repoRisk.findCardLimitNowFirst(idxUser,calcDate);

		risk.recentBalance(repoResAccount.findRecentBalance(idxUser, calcDate));
		risk.actualBalance(risk.recentBalance());


		boolean isIssuanceSuccess = false;

		if(issuanceProgress != null){
			isIssuanceSuccess = isIssuanceSuccess(issuanceProgress.getProgress().name(), issuanceProgress.getStatus().name());
		}

		// risk.cardLimitNow(Math.max(risk.depositGuarantee(),risk.realtimeLimit()));

		// CardRestart
		// risk.cardRestart(risk.cardRestartCount() >= 45);

		if(issuanceProgress != null && isIssuanceSuccess(issuanceProgress.getProgress().name(), issuanceProgress.getStatus().name())){
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
		risk.grade(getGrade(d1530, riskconfig, risk.recentBalance()));

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

		try{
			D1000 d1000 = repoD1000.findFirstByIdxCorpOrderByUpdatedAtDesc(risk.corp().user().idx())
					.orElseThrow(() -> CorpNotRegisteredException.builder().build());
			repoD1000.save(d1000);

			D1400 d1400 = repoD1400.findFirstByIdxCorpOrderByUpdatedAtDesc(risk.corp().user().idx())
					.orElseThrow(() -> CorpNotRegisteredException.builder().build());
			d1400.setD014(String.valueOf(Math.round(risk.cardLimit())));
			repoD1400.save(d1400);
		}catch (Exception e){
			log.error("[saveRisk45] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage());
		}

		return ResponseEntity.ok().body(BusinessResponse.builder().data(risk).build());
	}

	private double getCardLimitNow(boolean issuanceSuccess, boolean emergencyStop, double depositGuarantee, double cardLimit, double realtimeLimit) {

		double value;

		if(issuanceSuccess){
			if(emergencyStop){
				value = depositGuarantee;
			}else {
				value = cardLimit;
			}
		}else{
			value = Math.max(depositGuarantee,realtimeLimit);
		}

		return value;
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


	@Transactional(readOnly = true)
	public ResponseEntity<?> getRiskConfig(Long idxUser, Long idxCorp) {
		User user = repoUser.findById(idxUser).orElseThrow(
				() -> UserNotFoundException.builder().build()
		);

		if(idxCorp == null){
			idxCorp = user.corp().idx();
		}

		Long finalIdxCorp = idxCorp;

		RiskDto.RiskConfigDto riskConfig = repoRiskConfig.findByCorpAndEnabled(Corp.builder().idx(finalIdxCorp).build(), true).map(RiskDto.RiskConfigDto::from)
				.orElseThrow(
						() -> CorpNotRegisteredException.builder().account(finalIdxCorp.toString()).build()
				);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(riskConfig).build());
	}

	private boolean isIssuanceSuccess(String progress, String status){
		return (P_1800.name().equals(progress) || LP_ZIP.name().equals(progress)) && SUCCESS.name().equals(status);
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> saveRiskVer2(Long idxCorp, String calcDate) {

		Corp corp = repoCorp.findById(idxCorp).orElseThrow(() -> CorpNotRegisteredException.builder().build());

		RiskConfig riskConfig = CheckSettingRiskConfig(corp);

		Risk risk = saveRisk30(idxCorp, calcDate, riskConfig);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(risk).build());
	}

	private RiskConfig CheckSettingRiskConfig(Corp corp) {
		return repoRiskConfig.findByUserAndEnabled(corp.user(), true).orElseGet(
				() -> RiskConfig.builder()
						.depositGuarantee(0F)
						.depositPayment(false)
						.vcInvestment(false)
						.ventureCertification(false)
						.cardIssuance(false)
						.ceoGuarantee(false)
						.enabled(true)
						.user(corp.user())
						.corp(corp)
						.build()
		);
	}

	@Transactional(rollbackFor = Exception.class)
	public Risk saveRisk30(Long idxCorp, String calcDate, RiskConfig riskConfig) {
		Corp corp = repoCorp.findById(idxCorp).orElseThrow(() -> CorpNotRegisteredException.builder().build());
		IssuanceProgress issuanceProgress = repoIssuanceProgress.findById(corp.user().idx()).orElse(null);
		D1530 d1530 = repoD1530.findTopByIdxCorp(corp.idx()).orElseThrow(() -> new RuntimeException("D1530 corp not found"));

		Risk risk = repoRisk.findByUserAndDate(corp.user(), calcDate).orElseGet(
				() -> Risk.builder().build()
		);

		risk.user(corp.user());
		risk.corp(corp);
		risk.date(calcDate);
		risk.ceoGuarantee(riskConfig.ceoGuarantee());
		risk.depositGuarantee(riskConfig.depositGuarantee());
		risk.depositPayment(riskConfig.depositPayment());
		risk.cardIssuance(riskConfig.cardIssuance());
		risk.ventureCertification(riskConfig.ventureCertification());
		risk.vcInvestment(riskConfig.vcInvestment());
		risk.ventureCertification(riskConfig.ventureCertification());
		risk.vcInvestment(riskConfig.vcInvestment());

		//최신잔고를 구함
		risk.recentBalance(getRecentBalance(corp, d1530, risk.recentBalance()));
		//Grade
		risk.grade(getGrade(d1530, riskConfig, risk.recentBalance()));
		//실제계산금액
		risk.cardLimitNow( risk.recentBalance() * getCardLimitNowPercent(risk.grade()) / 100 );

		if(issuanceProgress != null &&
				isIssuanceSuccess(issuanceProgress.getProgress().name(), issuanceProgress.getStatus().name())){
			risk.cardIssuance(true);
		}

		return risk;
	}

	private Integer getCardLimitNowPercent(String grade ) {
		String strPercent = repoCommonCodeDetail.findFirstByCode1AndCode(grade, CommonCodeType.RISK_GRADE).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("CommonCodeDetail")
						.build()
		).value1();

		return Integer.parseInt(strPercent);
	}

	private String getGrade(D1530 d1530, RiskConfig riskConfig, Double balance) {
		if(minCashBalance > balance){
			return "F";
		}else if( d1530 != null && Double.parseDouble(d1530.getD042()) < minBalance &&
				Integer.parseInt(LocalDate.now().minusMonths(1).format(DateTimeFormatter.BASIC_ISO_DATE)) < Integer.parseInt(d1530.getD057())){
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

}


