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
import com.nomadconnection.dapp.core.domain.repository.querydsl.CorpCustomRepository;
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
		public static final String RISK_ID_CALC = "/risk/id/calc";		// 11 한도 재계산 - 리스크 저장
		public static final String RISK_ID_LEVEL_CHANGE = "/risk/id/level_change";		// 1 등급 변경
		public static final String RISK_ID_E_STOP = "/risk/id/e_stop";		// 3 긴급중지
		public static final String RISK_ID_A_STOP = "/risk/id/a_stop";		// 4 일시정지
		public static final String RISK_ID_LIST1 = "/risk/id/list1";		// 13 CSV다운로드
		public static final String RISK_ID_LIST2 = "/risk/id/list2";		// 5 한도기록
		public static final String RISK_ID_LIST3 = "/risk/id/list3";		// 5 잔고기록
		public static final String RISK_ID_CALC2 = "/risk/id/calc2";		// 12 날짜별 리스크 재계산 - 리스크 저장

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

	/*
	@GetMapping( URI.RISK + 1 )
	@ApiPageable
	public boolean genVid() throws Exception{

		return service.getVid();
	}

	*/

	@ApiOperation(value = "카드 리스크"
			, notes = "" + "\n"
			+ "법인명 idxCorpName "  + "\n"
			+ "변경잔고 cardLimitNow "  + "\n"
			+ "부여한도 cardLimit "  + "\n"
			+ "법인등급 grade "  + "\n"
			+ "최신잔고 balance "  + "\n"
			+ "현재잔고 currentBalance "  + "\n"
			+ "유지기간 cardRestartCount "  + "\n"
			+ "현재잔고 currentBalance "  + "\n"
			+ "긴급중지 emergencyStop "  + "\n"
			+ "카드발급여부 cardIssuance "  + "\n"
			+ "updatedAt updatedAt "  + "\n"
			+ "errCode errCode "  + "\n"
	)
	@GetMapping( URI.RISK )
	@ApiPageable
	public ResponseEntity riskList(@ModelAttribute AdminCustomRepository.SearchRiskDto riskDto
			, @ApiIgnore @CurrentUser CustomUser user, @PageableDefault Pageable pageable) {
		if (log.isDebugEnabled()) {
			log.debug("([ riskList ]) $user='{}'", user.idx());
		}
		return service.riskList(riskDto, user.idx(), pageable);
	}

	@ApiOperation(value = "등급 변경" , notes = "" + "\n")
	@PostMapping( URI.RISK_ID_LEVEL_CHANGE )
	public ResponseEntity riskIdLevelChange(@ApiIgnore @CurrentUser CustomUser user,
											@ModelAttribute RiskDto.RiskConfigDto dto) {
		if (log.isDebugEnabled()) {
			log.debug("([ riskIdLevelChange ]) $user='{}'", user.idx());
		}
		return service.riskIdLevelChange(user.idx(), dto);
	}

	@ApiOperation(value = "긴급중지" , notes = "" + "\n booleanValue true / false ")
	@PostMapping( URI.RISK_ID_E_STOP )
	public ResponseEntity saveEmergencyStop(@ApiIgnore @CurrentUser CustomUser user,
											@RequestParam(required = false) Long idxCorp,
											@RequestParam(required = false) String booleanValue) {
		if (log.isDebugEnabled()) {
			log.debug("([ saveEmergencyStop ]) $user='{}'", user.idx());
		}
		return service.saveEmergencyStop(user.idx(), idxCorp, booleanValue);
	}

	@ApiOperation(value = "일시정지" , notes = "" + "\n booleanValue true / false ")
	@PostMapping( URI.RISK_ID_A_STOP )
	public ResponseEntity savePause(@ApiIgnore @CurrentUser CustomUser user,
									@RequestParam(required = false) Long idxCorp,
									@RequestParam(required = false) String booleanValue) {
		if (log.isDebugEnabled()) {
			log.debug("([ savePause ]) $user='{}'", user.idx());
		}
		return service.savePause(user.idx(), idxCorp, booleanValue);
	}

	@ApiOperation(value = "한도기록 ."
			, notes = "" + "\n"
			+ " date; // 날짜 \n"
			+ " ceoGuarantee; //대표이사 연대보증 여부 \n"
			+ " depositGuarantee;    //요구 보증금 \n"
			+ " depositPayment;    //보증금 납입 여부 \n"
			+ " cardIssuance;    //카드발급여부 \n"
			+ " ventureCertification;    //벤처인증여부 \n"
			+ " vcInvestment;    //투자여부 \n"
			+ " grade;    //법인 등급 \n"
			+ " gradeLimitPercentage;    //등급별 한도율 \n"
			+ " minStartCash;    //최소 잔고 \n"
			+ " minCashNeed;    //최소 유지 잔고 \n"
			+ " currentBalance;    //현재잔고 \n"
			+ " error;    //계좌 스크래핑 오류발생 여부 \n"
			+ " dma45;    //잔고의 45일 평균값 \n"
			+ " dmm45;    //잔고의 45일 중간값 \n"
			+ " actualBalance;    //보증금제외 현재잔고 \n"
			+ " cashBalance;    //한도기준잔고 \n"
			+ " cardAvailable;    //발급가능여부 \n"
			+ " cardLimitCalculation;    //한도계산값 \n"
			+ " realtimeLimit;    //실시간 한도 \n"
			+ " cardLimit;    //부여 한도 \n"
			+ " cardLimitNow;    //변경 잔고 \n"
			+ " emergencyStop;    // 긴급중지 \n"
			+ " cardRestartCount;    //숫자 \n"
			+ " cardRestart;    // 카드재시작? \n"
			+ " pause;    // 일시정지 \n"
			+ " recentBalance;    // 최근 잔고 \n"
			+ " errCode; // 에러코드 일부값 \n"
	)
	@GetMapping( URI.RISK_ID_LIST1 )
	@ApiPageable
	public ResponseEntity riskListSelected(@ModelAttribute AdminCustomRepository.SearchRiskDto riskDto
			, @ApiIgnore @CurrentUser CustomUser user
			, @RequestParam Long idxCorp
			, @PageableDefault Pageable pageable) {
		if (log.isDebugEnabled()) {
			log.debug("([ riskListSelected ]) $user='{}'", user.idx());
		}
		return service.riskListSelected(riskDto, user.idx(), idxCorp, pageable);
	}



	@ApiOperation(value = "법인 정보"
			, notes = "" + "\n"
			+ "법인명 resCompanyNm "  + "\n"
			+ "사업자등록번호 resConpanyIdentityNo "  + "\n"
			+ "대표자 resUserNm "  + "\n"
			+ "업태 resCompanyNm "  + "\n"
			+ "법인명 resBusinessItems "  + "\n"
			+ "종목 resCompanyNm "  + "\n"
			+ "법인명 resBusinessTypes "  + "\n"
			+ "createdAt createdAt "  + "\n"
			+ "대표이사 연대보증 여부 ceoGuarantee "  + "\n"
			+ "보증금 depositGuarantee "  + "\n"
			+ "카드발급여부 cardIssuance "  + "\n"
			+ "벤처인증 ventureCertification "  + "\n"
			+ "투자유치 vcInvestment "  + "\n"
	)
	@GetMapping( URI.CORP )
	@ApiPageable
	public ResponseEntity corpList(@ModelAttribute CorpCustomRepository.SearchCorpDto CorpDto
			, @ApiIgnore @CurrentUser CustomUser user, @PageableDefault Pageable pageable) {
		if (log.isDebugEnabled()) {
			log.debug("([ corpList ]) $user='{}'", user.idx());
		}
		return service.corpList(CorpDto, user.idx(), pageable);
	}

	@ApiOperation(value = "법인 정보 상세"
			, notes = "" + "\n"
	)
	@GetMapping( URI.CORP_ID )
	public ResponseEntity corpId(@ApiIgnore @CurrentUser CustomUser user, @RequestParam Long idxCorp ) {
		if (log.isDebugEnabled()) {
			log.debug("([ corpId ]) $user='{}'", user.idx());
		}
		return service.corpId(user.idx(), idxCorp);
	}
}
