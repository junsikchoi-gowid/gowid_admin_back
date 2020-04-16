package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.AdminDto;
import com.nomadconnection.dapp.api.dto.CorpDto;
import com.nomadconnection.dapp.api.dto.RiskDto;
import com.nomadconnection.dapp.core.domain.*;
import com.nomadconnection.dapp.core.domain.repository.*;
import com.nomadconnection.dapp.core.domain.repository.querydsl.AdminCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CorpCustomRepository;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.ITemplateEngine;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AdminService {

	private final EmailConfig config;
	private final ITemplateEngine templateEngine;

	private final JwtService jwt;
	private final JavaMailSenderImpl sender;

	private final UserService serviceUser;
	private final UserRepository repoUser;
	private final RiskRepository repoRisk;
	private final CorpRepository repoCorp;
	private final ResAccountRepository repoResAccount;
	private final ResBatchRepository repoResBatch;


	private final ResAccountHistoryRepository resAccountHistoryRepository;
	private final RiskConfigRepository repoRiskConfig;


	public ResponseEntity riskList(AdminCustomRepository.SearchRiskDto riskDto, Long idxUser, Pageable pageable) {

		//	todo: idxUser Auth check

		Page<AdminCustomRepository.SearchRiskResultDto> resAccountPage = repoRisk.riskList(riskDto, idxUser, pageable);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(resAccountPage).build());
	}

	public ResponseEntity riskIdNowbalance(Long idxUser, Long idxCorp) {

		//	todo: idxUser Auth check

		Double doubleBalance = repoResAccount.findNowBalance(idxCorp);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(
				AdminDto.RiskBalanceDto.builder().riskBalance(doubleBalance).build()
		).build());
	}


	public ResponseEntity corpList(CorpCustomRepository.SearchCorpDto corpDto, Long idxUser, Pageable pageable) {

		//	todo: idxUser Auth check

		Page<CorpCustomRepository.SearchCorpResultDto> page = repoCorp.corpList(corpDto, idxUser, pageable);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(page).build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity riskIdLevelChange(Long idxUser, RiskDto.RiskConfigDto dto) {

		//	todo: idxUser Auth check

		Optional<RiskConfig> riskConfig = repoRiskConfig.findByUserAndEnabled(User.builder().idx(idxUser).build(), true);

		Boolean cardIssuance = false;

		if(riskConfig.isPresent()){
			riskConfig.ifPresent( x -> repoRiskConfig.save(
					RiskConfig.builder()
							.idx(x.idx())
							.enabled(false)
							.build()
			));

			cardIssuance = riskConfig.get().cardIssuance();
		}

		return ResponseEntity.ok().body(BusinessResponse.builder().data(
				repoRiskConfig.save(RiskConfig.builder()
						.user(User.builder().idx(idxUser).build())
						.enabled(true)
						.ceoGuarantee(dto.isCeoGuarantee())
						.cardIssuance(cardIssuance)
						.ventureCertification(dto.isVentureCertification())
						.vcInvestment(dto.isVcInvestment())
						.depositPayment(dto.isDepositPayment())
						.corp(User.builder().idx(idxUser).build().corp())
						.depositGuarantee(dto.getDepositGuarantee())
						.build())
		).build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity saveEmergencyStop(Long idxUser, Long idxCorp, String booleanValue) {

		//	todo: idxUser Auth check

		String calcDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);

		Risk risk = repoRisk.findByCorpAndDate(Corp.builder().idx(idxCorp).build(), calcDate).orElseThrow(
				() -> new RuntimeException("Empty Data")
		);

		if(booleanValue != null){
			if(booleanValue.toLowerCase().equals("true")){
				risk.emergencyStop(true);
			}else{
				risk.emergencyStop(false);
			}
		}

		repoRisk.save(risk);

		return ResponseEntity.ok().body(BusinessResponse.builder().build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity savePause(Long idxUser, Long idxCorp, String booleanValue) {

		//	todo: idxUser Auth check

		String calcDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);

		Risk risk = repoRisk.findByCorpAndDate(Corp.builder().idx(idxCorp).build(), calcDate).orElseThrow(
				() -> new RuntimeException("Empty Data")
		);

		if(booleanValue != null){
			if(booleanValue.toLowerCase().equals("true")){
				risk.pause(true);
			}else{
				risk.pause(false);
			}
		}

		repoRisk.save(risk);

		return ResponseEntity.ok().body(BusinessResponse.builder().build());
	}

	public ResponseEntity riskListSelected(AdminCustomRepository.SearchRiskDto riskDto, Long idx, Long idxCorp, Pageable pageable) {

		Corp corp = repoCorp.findById(idxCorp).orElseThrow(
				() -> new RuntimeException("Bad idxCorp request.")
		);

		Page<RiskDto> result = repoRisk.findByCorp(corp, pageable).map(RiskDto::from);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(result).build());
	}

	public ResponseEntity corpId(Long idx, Long idxCorp) {

		Optional<CorpDto> corp = repoCorp.findById(idxCorp).map(CorpDto::from);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(corp.get()).build());
	}

	/**
	 * 현금흐름 리스트
	 *
	 */
	public ResponseEntity cashList(Long idx, String corpName, String updateStatus, Pageable pageable) {
		//	todo: idxUser Auth check

		Page<AdminCustomRepository.CashResultDto> resAccountPage = repoRisk.cashList(corpName, updateStatus, idx, pageable);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(resAccountPage).build());

	}

	public ResponseEntity cashIdList(Long idx, String idxCorp) {
		return null;
	}

	public ResponseEntity scrapingList(Long idx, Pageable pageable) {

		Page<AdminCustomRepository.ScrapingResultDto> resAccountPage = repoRisk.scrapingList(pageable);

		resAccountPage.getContent().stream().forEach(
				x ->{
					List<ResBatchRepository.CResBatchDto> returnData = repoResBatch.findRefresh(x.idxUser);
					x.setSuccessAccountCnt(returnData.get(0).getProgressCnt());
					x.setAllAccountCnt(returnData.get(0).getTotal());
					log.debug(" err " + Integer.parseInt(returnData.get(0).getErrorCnt())  );
					x.setSuccessPercent( 100.0 - (Double.parseDouble(returnData.get(0).getErrorCnt()) / Double.parseDouble(x.getAllAccountCnt()) * 100 ) );
				}
		);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(resAccountPage).build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity scrapingUpdate(Long idx, Long idxCorp) {
		Optional<Corp> corp = Optional.ofNullable(repoCorp.findById(idxCorp).orElseThrow(
				() -> new RuntimeException("idxCopr Check")
		));
		repoUser.findByCorp(corp.get());
		return ResponseEntity.ok().body(BusinessResponse.builder().data(repoResBatch.endBatchUser(idxCorp)).build());
	}

	public ResponseEntity errorList(Long idx, Pageable pageable, AdminCustomRepository.ErrorSearchDto riskDto) {
		return null;
	}

	public ResponseEntity errorCorp(Long idx, String idxCorp) {
		return null;
	}
}