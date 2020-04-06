package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.AdminDto;
import com.nomadconnection.dapp.api.dto.RiskDto;
import com.nomadconnection.dapp.api.service.AdminService;
import com.nomadconnection.dapp.api.service.AuthService;
import com.nomadconnection.dapp.api.service.RiskService;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.core.annotation.ApiPageable;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.domain.repository.querydsl.AdminCustomRepository;
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

import javax.security.cert.X509Certificate;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;


@Slf4j
@RestController
@RequestMapping(AdminController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "Admin", description = AdminController.URI.BASE)
@SuppressWarnings({"unused", "deprecation"})
public class AdminController {

	@SuppressWarnings("WeakerAccess")
	public static class URI {
		public static final String BASE = "/admin/v1";

		public static final String RISK = "/risk";		// 카드 리스크
		public static final String RISK_ID_CALC = "/risk/id/calc";		// 11 한도 재계산
		public static final String RISK_ID_LEVEL_CHANGE = "/risk/id/level_change";		// 1 등급 변경
		public static final String RISK_ID_E_STOP = "/risk/id/e_stop";		// 3 긴급중지
		public static final String RISK_ID_A_STOP = "/risk/id/a_stop";		// 4 일시정지
		public static final String RISK_ID_LIST1 = "/risk/id/list1";		// 13 CSV다운로드
		public static final String RISK_ID_LIST2 = "/risk/id/list2";		// 5 한도기록
		public static final String RISK_ID_LIST3 = "/risk/id/list3";		// 5 잔고기록
		public static final String RISK_ID_CALC2 = "/risk/id/calc2";		// 12 날짜별 리스크 재계산

		public static final String CORP = "/corp";			// 법인 정보
		public static final String CORP_ID = "/corp/id";	// 법인 정보

		public static final String CASH = "/cash";		// 현금흐름
		public static final String CASH_ID_LIST1 = "/cash/id/list1";		// 현금흐름
		public static final String CASH_ID_LIST2 = "/cash/id/list2";		// 현금흐름

		public static final String SCRAPING = "/scraping";		// 계좌 스크래핑
		public static final String SCRAPING_ID = "/scraping/id";		// 계좌 스크래핑

		public static final String ERROR = "/error";	// 에러내역
		public static final String ERROR_ID = "/error/id";	// 에러내역
	}

	private final Boolean boolDebug = true;
	private final AdminService service;
	private final AuthService serviceAuth;
	private final UserService serviceUser;

	@GetMapping( URI.RISK + 1 )
	@ApiPageable
	public boolean genVid() throws Exception{

		return service.getVid();
	}

	@ApiOperation(value = "리스크"
			, notes = "" + "\n"
			+ "법인별 카드리스크" + "\n"
	)
	@GetMapping( URI.RISK )
	@ApiPageable
	public ResponseEntity riskList(@ModelAttribute AdminCustomRepository.SearchRiskDto riskDto
			, @ApiIgnore @CurrentUser CustomUser user, @PageableDefault Pageable pageable) {
		if (log.isDebugEnabled()) {
			log.debug("([ getAuthInfo ]) $user='{}'", user);
		}
		return service.riskList(riskDto, user.idx(), pageable);
	}
}
