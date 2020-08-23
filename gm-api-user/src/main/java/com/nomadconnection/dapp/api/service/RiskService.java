package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.RiskDto;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResAccountRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskConfigRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.*;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.risk.Risk;
import com.nomadconnection.dapp.core.domain.risk.RiskConfig;
import com.nomadconnection.dapp.core.domain.shinhan.D1000;
import com.nomadconnection.dapp.core.domain.shinhan.D1400;
import com.nomadconnection.dapp.core.domain.user.Role;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.ITemplateEngine;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiskService {

	private final EmailConfig config;
	private final ITemplateEngine templateEngine;

	private final JwtService jwt;
	private final JavaMailSenderImpl sender;

	private final CorpRepository repoCorp;
	private final UserService serviceUser;
	private final RiskRepository repoRisk;
	private final UserRepository repoUser;
	private final RiskConfigRepository repoRiskConfig;
	private final ResAccountRepository repoResAccount;

	private final D1000Repository repoD1000;
	private final D1100Repository repoD1100;
	private final D1400Repository repoD1400;
	private final D1510Repository repoD1510;
	private final D1520Repository repoD1520;
	private final D1530Repository repoD1530;

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

			Long finalIdxUser1 = idxUser;
			user = findUser(finalIdxUser1);

			corp = user.corp();
		}

		if(calcDate == null || calcDate.isEmpty()){
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

		Optional<RiskConfig> riskConfigOptional = repoRiskConfig.findByUserAndEnabled(user, true);
		RiskConfig riskconfig ;

		if(riskConfigOptional.isPresent()){
			riskconfig = RiskConfig.builder()
					.depositGuarantee(riskConfigOptional.get().depositGuarantee())
					.depositPayment(riskConfigOptional.get().depositPayment())
					.vcInvestment(riskConfigOptional.get().vcInvestment())
					.ventureCertification(riskConfigOptional.get().ventureCertification())
					.cardIssuance(riskConfigOptional.get().cardIssuance())
					.ceoGuarantee(riskConfigOptional.get().ceoGuarantee())
					.user(user)
					.corp(corp)
					.build();
		}else{
			riskconfig = RiskConfig.builder()
					.depositGuarantee(0F)
					.depositPayment(false)
					.vcInvestment(false)
					.ventureCertification(false)
					.cardIssuance(false)
					.ceoGuarantee(false)
					.enabled(true)
					.user(user)
					.corp(corp)
					.build();
		}

		// 최초가입시 가입후 회사정보 변경으로 인한 자동 적용
		if(riskconfig.user() == null || riskconfig.corp() == null ){
			repoRiskConfig.modifyRiskConfig(riskconfig.idx(), user.idx(), corp.idx());
		}

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

//		Corp finalCorp2 = corp;
//		Corp corpConfig = repoCorp.findById(user.corp().idx()).orElseThrow(
//				() -> UserNotFoundException.builder().id(finalCorp2.user().idx()).build()
//		);
//		corpConfig.riskConfig(riskconfig);
//		corpConfig.user(user);


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
			risk.minStartCash(50000000);
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

		// CardAvailable
//		if(risk.cashBalance() >= risk.minCashNeed()){
//			risk.cardAvailable(true);
//		}else{
//			risk.cardAvailable(false);
//		}

		boolean isEnoughBalance = risk.cashBalance() >= risk.minCashNeed();
		risk.cardAvailable(isEnoughBalance);

		// CardLimitCalculation
		risk.cardLimitCalculation( risk.cashBalance() * risk.gradeLimitPercentage()/100);

		// RealtimeLimit
		risk.realtimeLimit(Math.floor(risk.cardLimitCalculation() / 1000000) * 1000000);

		// CardLimit
		risk.cardLimit(Math.max(risk.depositGuarantee(),risk.realtimeLimit()));

		// EmergencyStop
//		if(risk.cashBalance() < risk.minCashNeed() || risk.recentBalance() < risk.cardLimitNow()){
//			risk.emergencyStop(true);
//		}else{
//			risk.emergencyStop(false);
//		}

		boolean needToStop = risk.cashBalance() < risk.minCashNeed() || risk.recentBalance() < risk.cardLimitNow();
		risk.emergencyStop(needToStop);

		// CardLimitNow
		Double cardLimitNow = repoRisk.findCardLimitNow(idxUser,calcDate);
		Double cardLimitNowFirst = repoRisk.findCardLimitNowFirst(idxUser,calcDate);
		if(risk.emergencyStop()){
			risk.cardLimitNow(risk.depositGuarantee());
		}else {
			if(cardLimitNow == null ){
				if(cardLimitNowFirst == null){
					risk.cardLimitNow(risk.cardLimit());
				}else{
					risk.cardLimitNow(cardLimitNowFirst);
				}
			}else {
				risk.cardLimitNow(cardLimitNow);
			}
		}

		// CardRestartCount
		AtomicInteger iCardRestartCount = new AtomicInteger();
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

		repoRisk.save(risk);

		return ResponseEntity.ok().body(BusinessResponse.builder().build());
	}

	@Transactional(readOnly = true)
	public String getCardLimit(Long idx_user) {
		return repoRisk.findCardLimitNowFirst(idx_user, CommonUtil.getNowYYYYMMDD()) + "";
	}

	@Transactional(readOnly = true)
	private User findUser(Long idx_user) {
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

			Long finalIdxUser1 = idxUser;
			user = findUser(finalIdxUser1);

			corp = user.corp();
		}

		if(calcDate == null || calcDate.isEmpty()){
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

		Optional<RiskConfig> riskConfigOptional = repoRiskConfig.findByUserAndEnabled(user, true);
		RiskConfig riskconfig ;

		if(riskConfigOptional.isPresent()){
			riskconfig = RiskConfig.builder()
					.depositGuarantee(riskConfigOptional.get().depositGuarantee())
					.depositPayment(riskConfigOptional.get().depositPayment())
					.vcInvestment(riskConfigOptional.get().vcInvestment())
					.ventureCertification(riskConfigOptional.get().ventureCertification())
					.cardIssuance(riskConfigOptional.get().cardIssuance())
					.ceoGuarantee(riskConfigOptional.get().ceoGuarantee())
					.user(user)
					.corp(corp)
					.build();
		}else{
			riskconfig = RiskConfig.builder()
					.depositGuarantee(0F)
					.depositPayment(false)
					.vcInvestment(false)
					.ventureCertification(false)
					.cardIssuance(false)
					.ceoGuarantee(false)
					.enabled(true)
					.user(user)
					.corp(corp)
					.build();
		}

		// 최초가입시 가입후 회사정보 변경으로 인한 자동 적용
		if(riskconfig.user() == null || riskconfig.corp() == null ){
			repoRiskConfig.modifyRiskConfig(riskconfig.idx(), user.idx(), corp.idx());
		}

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

//		Corp finalCorp2 = corp;
//		Corp corpConfig = repoCorp.findById(user.corp().idx()).orElseThrow(
//				() -> UserNotFoundException.builder().id(finalCorp2.user().idx()).build()
//		);
//		corpConfig.riskConfig(riskconfig);
//		corpConfig.user(user);


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
			risk.minStartCash(50000000);
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

		// CardAvailable
//		if(risk.cashBalance() >= risk.minCashNeed()){
//			risk.cardAvailable(true);
//		}else{
//			risk.cardAvailable(false);
//		}

		boolean isEnoughBalance = risk.cashBalance() >= risk.minCashNeed();
		risk.cardAvailable(isEnoughBalance);

		// CardLimitCalculation
		risk.cardLimitCalculation( risk.cashBalance() * risk.gradeLimitPercentage()/100);

		// RealtimeLimit
		risk.realtimeLimit(Math.floor(risk.cardLimitCalculation() / 1000000) * 1000000);

		// CardLimit
		risk.cardLimit(Math.max(risk.depositGuarantee(),risk.realtimeLimit()));

		// EmergencyStop
//		if(risk.cashBalance() < risk.minCashNeed() || risk.recentBalance() < risk.cardLimitNow()){
//			risk.emergencyStop(true);
//		}else{
//			risk.emergencyStop(false);
//		}

		boolean needToStop = risk.cashBalance() < risk.minCashNeed() || risk.recentBalance() < risk.cardLimitNow();
		risk.emergencyStop(needToStop);

		// CardLimitNow
		Double cardLimitNow = repoRisk.findCardLimitNow(idxUser,calcDate);
		Double cardLimitNowFirst = repoRisk.findCardLimitNowFirst(idxUser,calcDate);
		if(risk.emergencyStop()){
			risk.cardLimitNow(risk.depositGuarantee());
		}else {
			if(cardLimitNow == null ){
				if(cardLimitNowFirst == null){
					risk.cardLimitNow(risk.cardLimit());
				}else{
					risk.cardLimitNow(cardLimitNowFirst);
				}
			}else {
				risk.cardLimitNow(cardLimitNow);
			}
		}

		// CardRestartCount
		AtomicInteger iCardRestartCount = new AtomicInteger();
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

		repoRisk.save(risk);


		D1000 d1000 = repoD1000.findFirstByIdxCorpOrderByUpdatedAtDesc(corp.idx());
		D1400 d1400 = repoD1400.findFirstByIdxCorpOrderByUpdatedAtDesc(corp.idx());

		String strVentureCertification;
		String strVcInvestment;
		String strGrade;
		if(riskconfig.ventureCertification()) {
			strVentureCertification = "1";
		}else{
			strVentureCertification = "0";
		}

		if(riskconfig.vcInvestment()){
			strVcInvestment = "1";
		}else{
			strVcInvestment = "0";
		}

		strGrade = risk.grade();

		repoD1000.save(d1000);

		d1400.setD014(String.valueOf(Math.round(risk.cardLimit())));

		repoD1400.save(d1400);

		return ResponseEntity.ok().body(BusinessResponse.builder().build());
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
}