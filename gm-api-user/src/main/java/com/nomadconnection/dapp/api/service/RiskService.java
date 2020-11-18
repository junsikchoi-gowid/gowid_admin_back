package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.RiskDto;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.common.IssuanceProgress;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.common.IssuanceProgressRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResAccountRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskConfigRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1000Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1400Repository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.risk.Risk;
import com.nomadconnection.dapp.core.domain.risk.RiskConfig;
import com.nomadconnection.dapp.core.domain.shinhan.D1000;
import com.nomadconnection.dapp.core.domain.shinhan.D1400;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

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

	private final D1000Repository repoD1000;
	private final D1400Repository repoD1400;

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity saveRiskConfig(RiskDto.RiskConfigDto riskConfig){
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
	public ResponseEntity saveRisk(Long idxUser, Long idxCorp, String calcDate) {

		Corp corp;
		Long finalIdxUser = idxUser;
		User userAuth = findUser(finalIdxUser);
		User user;

		if(idxCorp != null && userAuth.authorities().stream().noneMatch(o-> o.role().equals(Role.GOWID_ADMIN))) {
			throw new RuntimeException("DOES NOT HAVE GOWID-ADMIN");
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
			calcDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);
		}

		String calcDatePlus = LocalDate.of(Integer.parseInt(calcDate.substring(0,4))
				, Integer.parseInt(calcDate.substring(4,6))
				, Integer.parseInt(calcDate.substring(6,8))
		).plusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);

		String calcDateMinus = LocalDate.of(Integer.parseInt(calcDate.substring(0,4))
				, Integer.parseInt(calcDate.substring(4,6))
				, Integer.parseInt(calcDate.substring(6,8))
		).minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);

		RiskConfig riskconfig = repoRiskConfig.findByUserAndEnabled(user, true).orElseGet(
				() -> RiskConfig.builder()
						.depositGuarantee(0F)
						.depositPayment(false)
						.vcInvestment(false)
						.ventureCertification(false)
						.cardIssuance(false)
						.ceoGuarantee(false)
						.enabled(true)
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
			risk.ventureCertification(riskconfig.ventureCertification());
			risk.vcInvestment(riskconfig.vcInvestment());
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

			risk.ventureCertification(riskconfig.ventureCertification());
			risk.vcInvestment(riskconfig.vcInvestment());
		}

		risk.user(user);
		risk.corp(corp);

		// 46일
		List<ResAccountRepository.CRisk> cRisk45days = repoResAccount.find45dayValance(idxUser, calcDate);

		// 45일
		Stream<ResAccountRepository.CRisk> cRisk45daysTemp = cRisk45days.stream();

		if(riskconfig.ventureCertification() && riskconfig.vcInvestment()){
			risk.grade("A");
			risk.gradeLimitPercentage(10);
			risk.minStartCash(100000000);
			risk.minCashNeed(50000000);
		}else if(riskconfig.ventureCertification() && !riskconfig.vcInvestment()){
			risk.grade("B");
			risk.gradeLimitPercentage(5);
			risk.minStartCash(100000000);
			risk.minCashNeed(50000000);
		}else{
			risk.grade("C");
			risk.gradeLimitPercentage(5);
			risk.minStartCash(500000000);
			risk.minCashNeed(50000000);
		}

		if(cRisk45days.size() > 0 ){
			risk.currentBalance(cRisk45days.get(0).getCurrentBalance());
			risk.errCode(cRisk45days.get(0).getErrCode());
		}else{
			risk.currentBalance(0F);
		}


		// Error
		risk.error(repoRisk.findErrCount(idxUser));

		// 45DMA
		List<Double> arrList = new ArrayList<>();
		cRisk45daysTemp.forEach( cRisk -> arrList.add((double) cRisk.getCurrentBalance()));

		risk.dma45(arrList.stream().mapToDouble(Double::doubleValue).average().orElse(0));

		// 45DMM
		AtomicInteger i = new AtomicInteger(0);
		arrList.stream().sorted().forEach( l -> {
			log.debug("sort order $={} $={}" , i.getAndIncrement(), l);
			if(i.get() == 23){
				risk.dmm45(l);
			}
		});

		if(repoResAccount.findRecentBalance(idxUser, calcDatePlus) != null ) {
			risk.recentBalance(repoResAccount.findRecentBalance(idxUser, calcDatePlus));
		}

		// ActualBalance
		if(risk.depositPayment()){
			risk.actualBalance(risk.recentBalance()-risk.depositGuarantee());
		}else {
			risk.actualBalance(risk.recentBalance());
		}

		// CashBalance
		ArrayList<Double> cashBalance = new ArrayList<>();
		cashBalance.add(risk.dma45());
		cashBalance.add(risk.dmm45());
		cashBalance.add(risk.actualBalance());
		risk.cashBalance(Collections.min(cashBalance));

		// CardLimitCalculation
		risk.cardLimitCalculation( risk.cashBalance() * risk.gradeLimitPercentage()/100);

		// RealtimeLimit
		risk.realtimeLimit(Math.floor(risk.cardLimitCalculation() / 1000000) * 1000000);

		// CardLimit
		risk.cardLimit(Math.max(risk.depositGuarantee(),risk.realtimeLimit()));

		boolean needToStop = risk.cashBalance() < risk.minCashNeed() || risk.recentBalance() < risk.cardLimitNow();
		risk.emergencyStop(needToStop);

		// CardLimitNow
		Double cardLimitNow = repoRisk.findCardLimitNow(idxUser,calcDate);
		Double cardLimitNowFirst = repoRisk.findCardLimitNowFirst(idxUser,calcDate);

		boolean isIssuanceSuccess = false;

		if(issuanceProgress != null){
			isIssuanceSuccess = isIssuanceSuccess(issuanceProgress.getProgress().name(), issuanceProgress.getStatus().name());
		}

		risk.cardLimitNow(getCardLimitNow(
				issuanceProgress
				,isIssuanceSuccess
				,risk.emergencyStop()
				,risk.depositGuarantee()
				,cardLimitNow
				,cardLimitNowFirst
				,risk.cardLimit()
				,risk.realtimeLimit()
		));

		// CardRestartCount
		Risk risk1 =repoRisk.findByUserAndDate(User.builder().idx(idxUser).build(),calcDateMinus).orElse(
				Risk.builder()
						.cardRestartCount(0)
						.confirmedLimit(0).build()
		);

		if(risk.currentBalance()>  risk.minCashNeed()){
			risk.cardRestartCount(risk1.cardRestartCount() + 1);
		}else{
			risk.cardRestartCount(0);
		}

		Double confirmedLimit = risk1.confirmedLimit();
		if (confirmedLimit != null) {
			risk.confirmedLimit(risk1.confirmedLimit());
		} else {
			risk.confirmedLimit(0);
		}

		// CardRestart
		if(risk.cardRestartCount() >= 45){
			risk.cardRestart(true);
		}else{
			risk.cardRestart(false);
		}


		if(issuanceProgress != null && isIssuanceSuccess(issuanceProgress.getProgress().name(), issuanceProgress.getStatus().name())){
			riskconfig.cardIssuance(true);
			risk.cardIssuance(true);
		}

		repoRisk.save(risk);

		corp.riskConfig(repoRiskConfig.save(riskconfig));
		repoCorp.save(corp);

		return ResponseEntity.ok().body(BusinessResponse.builder().build());
	}

	@Transactional(readOnly = true)
	public String getCardLimit(Long idx_user) {
		return repoRisk.findCardLimitNowFirst(idx_user, CommonUtil.getNowYYYYMMDD()) + "";
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
	public ResponseEntity saveRisk45(Long idxUser, Long idxCorp, String calcDate) {

		Corp corp;
		Long finalIdxUser = idxUser;
		User userAuth = findUser(finalIdxUser);
		User user;

		if(idxCorp != null && userAuth.authorities().stream().noneMatch(o-> o.role().equals(Role.GOWID_ADMIN))) {
			throw new RuntimeException("DOES NOT HAVE GOWID-ADMIN");
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
			calcDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);
		}

		String calcDatePlus = LocalDate.of(Integer.parseInt(calcDate.substring(0,4))
				, Integer.parseInt(calcDate.substring(4,6))
				, Integer.parseInt(calcDate.substring(6,8))
		).plusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);

		String calcDateMinus = LocalDate.of(Integer.parseInt(calcDate.substring(0,4))
				, Integer.parseInt(calcDate.substring(4,6))
				, Integer.parseInt(calcDate.substring(6,8))
		).minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);

		RiskConfig riskconfig = repoRiskConfig.findByUserAndEnabled(user, true).orElseGet(
				() -> RiskConfig.builder()
						.depositGuarantee(0F)
						.depositPayment(false)
						.vcInvestment(false)
						.ventureCertification(false)
						.cardIssuance(false)
						.ceoGuarantee(false)
						.enabled(true)
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
			risk.ventureCertification(riskconfig.ventureCertification());
			risk.vcInvestment(riskconfig.vcInvestment());
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

			risk.ventureCertification(riskconfig.ventureCertification());
			risk.vcInvestment(riskconfig.vcInvestment());

		}

		risk.user(user);
		risk.corp(corp);

		// 46일
		List<ResAccountRepository.CRisk> cRisk45days = repoResAccount.find45dayValance(idxUser, calcDate);

		// 45일
		Stream<ResAccountRepository.CRisk> cRisk45daysTemp = cRisk45days.stream();

		if(riskconfig.ventureCertification() && riskconfig.vcInvestment()){
			risk.grade("A");
			risk.gradeLimitPercentage(10);
			risk.minStartCash(100000000);
			risk.minCashNeed(50000000);
		}else if(riskconfig.ventureCertification() && !riskconfig.vcInvestment()){
			risk.grade("B");
			risk.gradeLimitPercentage(5);
			risk.minStartCash(100000000);
			risk.minCashNeed(50000000);
		}else{
			risk.grade("C");
			risk.gradeLimitPercentage(5);
			risk.minStartCash(500000000);
			risk.minCashNeed(50000000);
		}

		// currentBalance

		if(cRisk45days.size() > 0 ){
			risk.currentBalance(cRisk45days.get(0).getCurrentBalance());
			risk.errCode(cRisk45days.get(0).getErrCode());
		}else{
			risk.currentBalance(0F);
		}


		// Error

		risk.error(repoRisk.findErrCount(idxUser));

		// 45DMA
		List<Double> arrList = new ArrayList<>();
		cRisk45daysTemp.forEach( cRisk -> arrList.add((double) cRisk.getCurrentBalance()));

		risk.dma45(arrList.stream().mapToDouble(Double::doubleValue).average().orElse(0));

		// 45DMM
		AtomicInteger i = new AtomicInteger(0);
		arrList.stream().sorted().forEach( l -> {
			log.debug("sort order $={} $={}" , i.getAndIncrement(), l);
			if(i.get() == 23){
				risk.dmm45(l);
			}
		});

		if(repoResAccount.findRecentBalance(idxUser, calcDatePlus) != null ) {
			risk.recentBalance(repoResAccount.findRecentBalance(idxUser, calcDatePlus));
		}

		// ActualBalance
		if(risk.depositPayment()){
			risk.actualBalance(risk.recentBalance()-risk.depositGuarantee());
		}else {
			risk.actualBalance(risk.recentBalance());
		}

		// CashBalance
		ArrayList<Double> cashBalance = new ArrayList<>();
		cashBalance.add(risk.dma45());
		cashBalance.add(risk.dmm45());
		cashBalance.add(risk.actualBalance());
		risk.cashBalance(Collections.min(cashBalance));

		// CardLimitCalculation
		risk.cardLimitCalculation( risk.cashBalance() * risk.gradeLimitPercentage()/100);

		// RealtimeLimit
		risk.realtimeLimit(Math.floor(risk.cardLimitCalculation() / 1000000) * 1000000);

		// CardLimit
		risk.cardLimit(Math.max(risk.depositGuarantee(),risk.realtimeLimit()));

		// EmergencyStop
		boolean needToStop = risk.cashBalance() < risk.minCashNeed() || risk.recentBalance() < risk.cardLimitNow();
		risk.emergencyStop(needToStop);

		// CardLimitNow
		Double cardLimitNow = repoRisk.findCardLimitNow(idxUser,calcDate);
		Double cardLimitNowFirst = repoRisk.findCardLimitNowFirst(idxUser,calcDate);

		boolean isIssuanceSuccess = false;

		if(issuanceProgress != null){
			isIssuanceSuccess = isIssuanceSuccess(issuanceProgress.getProgress().name(), issuanceProgress.getStatus().name());
		}

		risk.cardLimitNow(getCardLimitNow(
				issuanceProgress
				,isIssuanceSuccess
				,risk.emergencyStop()
				,risk.depositGuarantee()
				,cardLimitNow
				,cardLimitNowFirst
				,risk.cardLimit()
				,risk.realtimeLimit()
		));

		// CardRestartCount
		Risk risk1 =repoRisk.findByUserAndDate(User.builder().idx(idxUser).build(),calcDateMinus).orElse(
				Risk.builder()
						.cardRestartCount(0)
						.confirmedLimit(0).build()
		);

		if(risk.currentBalance()>  risk.minCashNeed()){
			risk.cardRestartCount(risk1.cardRestartCount() + 1);
		}else{
			risk.cardRestartCount(0);
		}

		Double confirmedLimit = risk1.confirmedLimit();
		if (confirmedLimit != null) {
			risk.confirmedLimit(risk1.confirmedLimit());
		} else {
			risk.confirmedLimit(0);
		}

		// CardRestart
		if(risk.cardRestartCount() >= 45){
			risk.cardRestart(true);
		}else{
			risk.cardRestart(false);
		}

		if(issuanceProgress != null && isIssuanceSuccess(issuanceProgress.getProgress().name(), issuanceProgress.getStatus().name())){
			riskconfig.cardIssuance(true);
			risk.cardIssuance(true);
		}

		repoRisk.save(risk);

		D1000 d1000 = repoD1000.findFirstByIdxCorpOrderByUpdatedAtDesc(user.corp().idx()).orElseThrow(
				() -> CorpNotRegisteredException.builder().build()
		);
		D1400 d1400 = repoD1400.findFirstByIdxCorpOrderByUpdatedAtDesc(user.corp().idx()).orElseThrow(
				() -> CorpNotRegisteredException.builder().build()
		);

		corp.riskConfig(repoRiskConfig.save(riskconfig));
		repoCorp.save(corp);

		repoD1000.save(d1000);

		d1400.setD014(String.valueOf(Math.round(risk.cardLimit())));

		repoD1400.save(d1400);

		return ResponseEntity.ok().body(BusinessResponse.builder().build());
	}

	private double getCardLimitNow(IssuanceProgress issuanceProgress, boolean issuanceSuccess, boolean emergencyStop, double depositGuarantee, Double cardLimitNow, Double cardLimitNowFirst, double cardLimit, double realtimeLimit) {

		double value = 0L;

		if(issuanceProgress != null && issuanceSuccess){
			if(emergencyStop){
				value = depositGuarantee;
			}else {
				if(cardLimitNow == null ){
					if(cardLimitNowFirst == null){
						value = cardLimit;
					}else{
						value = cardLimitNowFirst;
					}
				}else {
					value = cardLimitNow;
				}
			}
		}else{
			value = Math.max(depositGuarantee,realtimeLimit);
		}

		return value;
	}

	@Transactional(readOnly = true)
	public ResponseEntity getGrantLimit(Long idxUser) {

		User user = repoUser.findById(idxUser).orElseThrow(
				() -> UserNotFoundException.builder().build()
		);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(
				repoRiskConfig.getTopByCorpAndEnabled(user.corp(),true).grantLimit()
		).build());
	}


	@Transactional(readOnly = true)
	public ResponseEntity getRiskConfig(Long idxUser, Long idxCorp) {
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
		return (P_1800.equals(progress) || LP_ZIP.equals(progress)) && SUCCESS.equals(status);
	}

}


