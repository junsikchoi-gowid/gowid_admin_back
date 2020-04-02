package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.RiskDto;
import com.nomadconnection.dapp.core.domain.*;
import com.nomadconnection.dapp.core.domain.repository.*;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.ITemplateEngine;

import javax.sound.midi.MidiSystem;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class RiskService {

	private final EmailConfig config;
	private final ITemplateEngine templateEngine;

	private final JwtService jwt;
	private final JavaMailSenderImpl sender;

	private final UserService serviceUser;
	private final RiskRepository repoRisk;
	private final RiskConfigRepository repoRiskConfig;
	private final ResAccountRepository repoResAccount;

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity saveRiskConfig(RiskDto.RiskConfigDto riskConfig){
		return ResponseEntity.ok().body(BusinessResponse.builder().data(repoRiskConfig.save(
				RiskConfig.builder()
						.idxUser(riskConfig.getIdxUser())
						.ceoGuarantee(riskConfig.isCeoGuarantee())
						.depositGuarantee(riskConfig.getDepositGuarantee())
						.depositPayment(riskConfig.isDepositPayment())
						.cardIssuance(riskConfig.isCardIssuance())
						.ventureCertification(riskConfig.isVentureCertification())
						.vcInvestment(riskConfig.isVcInvestment())
						.build()
		)).build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity saveRisk(Long idxUser, String calcDate) {

		if(calcDate == null || calcDate.isEmpty()) calcDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

		RiskConfig riskconfig = repoRiskConfig.findByIdxUser(idxUser).orElse(
				RiskConfig.builder()
						.depositGuarantee(0F)
						.depositPayment(false)
						.vcInvestment(false)
						.ventureCertification(false)
						.cardIssuance(false)
						.ceoGuarantee(false)
						.idxUser(idxUser)
						.build()
		);

		Risk risk = repoRisk.findByIdxUserAndDate(idxUser, calcDate).orElse(
				Risk.builder()
						.idxUser(idxUser)
						.date(calcDate)
						.ceoGuarantee(riskconfig.ceoGuarantee())
						.depositGuarantee(riskconfig.depositGuarantee())
						.depositPayment(riskconfig.depositPayment())
						.cardIssuance(riskconfig.cardIssuance())
						.ventureCertification(riskconfig.ventureCertification())
						.vcInvestment(riskconfig.vcInvestment())
						.build()
		);

		// 46일
		List<ResAccountRepository.CRisk> cRisk45days = repoResAccount.find45dayValance(idxUser, calcDate);

		System.out.println(cRisk45days.size());
		// 45일
		Stream<ResAccountRepository.CRisk> cRisk45daysTemp = cRisk45days.stream().filter(cRisk -> cRisk.getDsc() > 0);
		Stream<ResAccountRepository.CRisk> cRisk45daysTemp2 = cRisk45days.stream().filter(cRisk -> cRisk.getDsc() > 0);

		if(risk.ventureCertification() && risk.vcInvestment()){
			risk.grade("A");
			risk.gradeLimitPercentage(10);
			risk.minStartCash((float) 100000000);
			risk.minCashNeed((float) 50000000);
		}else if(risk.ventureCertification() && !risk.vcInvestment()){
			risk.grade("B");
			risk.gradeLimitPercentage(5);
			risk.minStartCash((float) 100000000);
			risk.minCashNeed((float) 50000000);
		}else{
			risk.grade("C");
			risk.gradeLimitPercentage(5);
			risk.minStartCash((float) 500000000);
			risk.minCashNeed((float) 100000000);
		}

		// currentBalance
		risk.currentBalance(0F);
		if(cRisk45days.size() > 0 ){
			risk.currentBalance(cRisk45days.get(1).getCurrentBalance());
		}


		// Error

		risk.error(repoRisk.findErrCount(idxUser));

		// 45DMA
 		List<Float> arrList = new ArrayList<>();
		cRisk45daysTemp.forEach( cRisk -> { arrList.add(cRisk.getCurrentBalance()); });
//		float avg = (float) arrList.stream()
//				.filter(n -> n%2 == 0)
//				.mapToDouble(Float::floatValue)
//				.average()
//				.orElse(0);
		float avg = 0F;
		for( ResAccountRepository.CRisk cRisk: cRisk45days){
			if(cRisk.getDsc() > 0 )	avg += cRisk.getCurrentBalance();
		}
		risk.dma45(avg/45);

		// 45DMM
		AtomicInteger i = new AtomicInteger(1);
		arrList.stream().sorted().forEach( l -> {
			log.debug("sort order $={} $={}" , i.getAndIncrement(), l);
			if(i.get() == 23){
				risk.dmm45(l);
			}
		});

		// ActualBalance
		if(risk.depositPayment()){
			risk.actualBalance(risk.currentBalance());
		}else {
			risk.actualBalance(risk.currentBalance()-risk.depositGuarantee());
		}

		// CashBalance
		ArrayList<Float> cashBalance = new ArrayList<Float>();
		cashBalance.add(risk.dma45());
		cashBalance.add(risk.dmm45());
		cashBalance.add(risk.actualBalance());
		risk.cashBalance(Collections.max(cashBalance));

		// CardAvailable
		if(risk.cashBalance() >= risk.minStartCash()){
			risk.cardAvailable(true);
		}else{
			risk.cardAvailable(false);
		}

		// CardLimitCalculation
		risk.cardLimitCalculation( risk.cashBalance() * risk.gradeLimitPercentage());

		// RealtimeLimit
		risk.realtimeLimit((float) (Math.floor(risk.cardLimitCalculation() / 1000000) * 1000000));

		// CardLimit
		risk.cardLimit(Math.max(risk.depositGuarantee(),risk.realtimeLimit()));

		// EmergencyStop
		if(risk.cashBalance() < risk.minCashNeed()){
			risk.emergencyStop(true);
		}else{
			risk.emergencyStop(false);
		}

		// CardLimitNow
		if(risk.emergencyStop()){
			risk.cardLimitNow(risk.depositGuarantee());
		}else {
			Float cardLimitNow = repoRisk.findCardLimitNow(idxUser);
			if(repoRisk.findCardLimitNow(idxUser) == null ){
				risk.cardLimitNow(risk.realtimeLimit());
			}else {
				risk.cardLimitNow(cardLimitNow);
			}
		}

		// CardRestartCount
		risk.cardRestartCount((int) cRisk45daysTemp2.filter(cRisk -> cRisk.getCurrentBalance() > risk.minCashNeed()).count());

		// CardRestart
		if(risk.cardRestartCount() >= 45){
			risk.cardRestart(true);
		}else{
			risk.cardRestart(false);
		}

		repoRisk.save(risk);

		return ResponseEntity.ok().body(BusinessResponse.builder().build());
	}
}