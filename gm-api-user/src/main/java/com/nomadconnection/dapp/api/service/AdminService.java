package com.nomadconnection.dapp.api.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.AdminDto;
import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.dto.CorpDto;
import com.nomadconnection.dapp.api.dto.RiskDto;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.core.domain.*;
import com.nomadconnection.dapp.core.domain.repository.*;
import com.nomadconnection.dapp.core.domain.repository.querydsl.AdminCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CorpCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.ResBatchListCustomRepository;
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
import java.util.stream.Collectors;

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
	private final AuthorityRepository repoAuthority;
	private final ResAccountRepository repoResAccount;
	private final ResBatchRepository repoResBatch;
	private final ResBatchListRepository repoResBatchList;	
	private final ResAccountHistoryRepository resAccountHistoryRepository;
	private final RiskConfigRepository repoRiskConfig;


	public Boolean isGowidMaster(Long idxUser){
		boolean boolV = false;

		User user = repoUser.findById(idxUser).orElseThrow(
				() -> UserNotFoundException.builder().build()
		);

		if(user.authorities().stream().anyMatch(o -> o.role().equals(Role.GOWID_ADMIN.toString()))) boolV = true;

		return boolV;
	}

	/**
	 * admin page 의 risk 리스트
	 * @param riskDto
	 * @param idxUser
	 * @param pageable
	 * @return
	 */
	public ResponseEntity riskList(AdminCustomRepository.SearchRiskDto riskDto, Long idxUser, Pageable pageable) {

		//	todo: idxUser Auth check GOWID_USER
		if(!isGowidMaster(idxUser))riskDto.setIdxCorpName("");

		Page<AdminDto.RiskDto> resAccountPage = repoRisk.riskList(riskDto, idxUser, pageable).map(AdminDto.RiskDto::from);

		if(isGowidMaster(idxUser))
			for (AdminDto.RiskDto x : resAccountPage.getContent()) x.setIdxCorpName("#" + x.idxCorp);

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

		RiskConfig riskConfig = repoRiskConfig.findByCorpAndEnabled(Corp.builder().idx(dto.idxCorp).build(), true)
				.orElseThrow(
						() -> CorpNotRegisteredException.builder().account(dto.idxCorp.toString()).build()
				);

		riskConfig.enabled(true);
		riskConfig.ceoGuarantee(dto.isCeoGuarantee());
		riskConfig.ventureCertification(dto.isVentureCertification());
		riskConfig.vcInvestment(dto.isVcInvestment());
		riskConfig.depositPayment(dto.isDepositPayment());
		riskConfig.depositGuarantee(dto.getDepositGuarantee());

		return ResponseEntity.ok().body(BusinessResponse.builder().data(repoRiskConfig.save(riskConfig)).build());
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
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity cashList(Long idx, String corpName, String updateStatus, Pageable pageable) {
		//	todo: idxUser Auth check

		Page<AdminDto.CashListDto> returnData = repoResAccount.cashList(corpName, updateStatus, pageable).map(AdminDto.CashListDto::from);

		returnData.getContent().stream().forEach(
				x ->{
					if(x.getIdxUser() != null) {

						List<Long> firstBalance = repoResAccount.findBalance(x.getIdxUser());

						Long longFirstBalance = 0L;
						Long longEndBalance = 0L;

						longFirstBalance = Long.valueOf(firstBalance.get(0));
						longEndBalance = Long.valueOf(firstBalance.get(3));

						Long BurnRate = (longFirstBalance - longEndBalance) / 3;
						Integer intMonth = 1;
						if (BurnRate > 0) {
							intMonth = (int) Math.ceil((double) longEndBalance / BurnRate);
						}

						x.setBurnRate(BurnRate);
						x.setRunWay(intMonth);
					}
				}
		);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(returnData).build());

	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity cashIdList(Long idxUser, Long idxCorp) {
		//	todo: idxUser Auth check

		List<AdminDto.CashListDetailDto> transactionList = null;

		if( idxCorp != null ){
			Corp corp = repoCorp.findById(idxCorp).orElseThrow(
					() -> CorpNotRegisteredException.builder().account(idxCorp.toString()).build()
			);

			log.debug(" user idx " + corp.user().idx());
			String startDate = GowidUtils.getMonth(-11);
			String endDate = GowidUtils.getMonth(0);

			transactionList = repoResAccount.findMonthHistory(startDate, endDate, corp.user().idx()).stream().map(AdminDto.CashListDetailDto::from).collect(Collectors.toList());

		}

		return ResponseEntity.ok().body(BusinessResponse.builder().data(transactionList).build());
	}

	public ResponseEntity scrapingList(Long idx, Pageable pageable) {

		Page<CorpCustomRepository.ScrapingResultDto> resAccountPage = repoCorp.scrapingList(pageable);

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

	public ResponseEntity errorList(Long idx, Pageable pageable, ResBatchListCustomRepository.ErrorSearchDto dto) {

		Page<AdminDto.ErrorResultDto> list = repoResBatchList.errorList(dto, pageable).map(AdminDto.ErrorResultDto::from);
		
		return ResponseEntity.ok().body(BusinessResponse.builder().data(list).build());
	}
}