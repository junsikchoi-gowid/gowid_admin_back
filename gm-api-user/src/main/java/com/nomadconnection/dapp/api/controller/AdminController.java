package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.AdminDto;
import com.nomadconnection.dapp.api.dto.RiskDto;
import com.nomadconnection.dapp.api.service.AdminService;
import com.nomadconnection.dapp.api.service.shinhan.ResumeService;
import com.nomadconnection.dapp.core.annotation.ApiPageable;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.domain.repository.querydsl.AdminCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CorpCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.ResBatchListCustomRepository;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping(AdminController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "Admin", description = AdminController.URI.BASE)
public class AdminController {

	public static class URI {
		public static final String BASE = "/admin/v1";

		public static final String SCRAPING = "/scraping";        // 계좌 스크래핑
		public static final String CORP_ID = "/corp/id";    // 법인 정보
		public static final String ORIGINAL_LIST = "/risk/original"; // GET 한도 기록 (리스트)
		public static final String CORP_INFO = "/corp/info" ;   // GET	법인별 - 리스크 관련 상세정보
		public static final String SCRAP_CORP_LIST = "/scrap/corp/list" ;  // GET	계좌스크래핑 - 계좌목록
		public static final String SCRAP_ACCOUNT_LIST = "/scrap/account/list" ;  // GET	계좌스크래핑 - 스크래핑결과
		public static final String RISK_ID_EMERGENCY_STOP = "/risk/id/e_stop";        // 긴급중지
		public static final String RISK_ID_LEVEL_CHANGE = "/risk/id/level_change";        // 등급 변경
	}

	private final AdminService service;
	private final ResumeService resumeService;

	@ApiOperation(value = "등급 변경" , notes = "" + "\n")
	@PostMapping( URI.RISK_ID_LEVEL_CHANGE )
	public ResponseEntity riskIdLevelChange(@ApiIgnore @CurrentUser CustomUser user,
											@RequestBody RiskDto.RiskConfigDto dto) {
		if (log.isInfoEnabled()) {
			log.info("([ riskIdLevelChange ]) $user='{}' $dto='{}'", user, dto);
		}
		return service.riskIdLevelChange(user.idx(), dto);
	}

	@ApiOperation(value = "긴급중지" , notes = "" + "\n booleanValue true / false ")
	@PostMapping( URI.RISK_ID_EMERGENCY_STOP )
	public ResponseEntity saveEmergencyStop(@ApiIgnore @CurrentUser CustomUser user,
											@RequestBody AdminDto.StopDto dto) {
		if (log.isInfoEnabled()) {
			log.info("([ saveEmergencyStop ]) $user='{}' $dto='{}'", user, dto);
		}
		return service.saveEmergencyStop(user.idx(), dto.idxCorp, dto.booleanValue);
	}

	@ApiOperation(value = "법인 정보 상세"
			, notes = "" + "\n"
	)
	@GetMapping( URI.CORP_ID )
	public ResponseEntity corpId(@ApiIgnore @CurrentUser CustomUser user, @RequestParam Long idxCorp ) {
		if (log.isInfoEnabled()) {
			log.info("([ corpId ]) $user='{}'", user);
		}
		return service.corpId(user.idx(), idxCorp);
	}

	@ApiOperation(value = "계좌 스크래핑"
			, notes = "" + "\n"
			+ "법인명 corpName" + "\n"
			+ "updateStatus true/false" + "\n"
	)
	@GetMapping( URI.SCRAPING )
	@ApiPageable
	public ResponseEntity scrapingList(@ApiIgnore @CurrentUser CustomUser user, @PageableDefault Pageable pageable,
		@RequestParam(required = false) Long idxCorp) {
		if (log.isInfoEnabled()) {
			log.info("([ scrapingList ]) $user='{}'", user);
		}
		return service.scrapingList(user.idx(), pageable);
	}

	@ApiOperation(value = "카드 리스크(신규) - 한도기록"
			, notes = "" + "\n"
			+ "Sort 방식 " + "\n"
			+ "idxCorp  " + "\n"
	)
	@GetMapping( URI.ORIGINAL_LIST )
	@ApiPageable
	public ResponseEntity originalList(@ModelAttribute AdminCustomRepository.RiskOriginal riskOriginal
			, @ApiIgnore @CurrentUser CustomUser user, @PageableDefault Pageable pageable) {
		if (log.isInfoEnabled()) {
			log.info("([ originalList ]) $dto='{}'", riskOriginal);
		}
		return service.originalList(riskOriginal, user.idx(), pageable);
	}

	@ApiOperation(value = "계좌스크래핑 - 계좌 목록"
			, notes = "" + "\n"
			+ "Sort 방식 " + "\n"
			+ "idxCorp  " + "\n"
	)
	@GetMapping( URI.SCRAP_CORP_LIST )
	@ApiPageable
	public ResponseEntity scrapCorpList(@ModelAttribute CorpCustomRepository.ScrapCorpDto dto
			, @ApiIgnore @CurrentUser CustomUser user, @PageableDefault Pageable pageable) {
		if (log.isInfoEnabled()) {
			log.info("([ scrapCorpList ]) $dto='{}'", dto);
		}
		return service.scrapCorpList(dto, user.idx(), pageable);
	}

	@ApiOperation(value = "계좌스크래핑 - 계좌 목록"
			, notes = "" + "\n"
			+ "Sort 방식 " + "\n"
			+ "idxCorp  " + "\n"
	)
	@GetMapping( URI.SCRAP_ACCOUNT_LIST )
	@ApiPageable
	public ResponseEntity scrapAccountList(@ModelAttribute ResBatchListCustomRepository.ScrapAccountDto dto
			, @ApiIgnore @CurrentUser CustomUser user, @PageableDefault Pageable pageable) {
		if (log.isInfoEnabled()) {
			log.info("([ scrapAccountList ]) $dto='{}'", dto);
		}
		return service.scrapAccountList(dto, user.idx(), pageable);
	}

	@ApiOperation(value = "법인별 - 리스크 관련 상세정보"
			, notes = "" + "\n"
			+ "Sort 방식 " + "\n"
			+ "idxCorp  " + "\n"
	)
	@GetMapping( URI.CORP_INFO )
	public ResponseEntity corpInfo(@RequestParam(required = false) Long idxCorp
			, @ApiIgnore @CurrentUser CustomUser user) {
		if (log.isInfoEnabled()) {
			log.info("([ scrapAccountList ]) $dto='{}'", idxCorp);
		}
		return service.corpInfo(idxCorp, user.idx());
	}
}




