package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.AdminDto;
import com.nomadconnection.dapp.api.dto.RiskDto;
import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.service.AdminService;
import com.nomadconnection.dapp.api.service.AuthService;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.api.service.shinhan.ResumeService;
import com.nomadconnection.dapp.core.annotation.ApiPageable;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.domain.repository.querydsl.AdminCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CorpCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.ResBatchListCustomRepository;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
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

import javax.validation.Valid;


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

		public static final String RISK = "/risk";        // 카드 리스크
		public static final String RISK_ID_NOWBALANCE = "/risk_id_nowbalance";        // 카드 현재 잔고
		public static final String RISK_ID_CALC = "/risk/id/calc";        // 11 한도 재계산 - 리스크 저장
		public static final String RISK_ID_LEVEL_CHANGE = "/risk/id/level_change";        // 1 등급 변경
		public static final String RISK_ID_E_STOP = "/risk/id/e_stop";        // 3 긴급중지
		public static final String RISK_ID_A_STOP = "/risk/id/a_stop";        // 4 일시정지
		public static final String RISK_ID_LIST1 = "/risk/id/list1";        // 13 CSV다운로드
		public static final String RISK_ID_LIST2 = "/risk/id/list2";        // 5 한도기록
		public static final String RISK_ID_LIST3 = "/risk/id/list3";        // 5 잔고기록
		public static final String RISK_ID_CALC2 = "/risk/id/calc2";        // 12 날짜별 리스크 재계산 - 리스크 저장

		public static final String CORP = "/corp";            // 법인 정보
		public static final String CORP_ID = "/corp/id";    // 법인 정보

		public static final String CASH = "/cash";        // 현금흐름
		public static final String CASH_ID_LIST = "/cash/id/list";        // 현금흐름

		public static final String SCRAPING = "/scraping";        // 계좌 스크래핑
		public static final String SCRAPING_UPDATE = "/scraping/update";        // 계좌 스크래핑 update

		public static final String ISSUANCE_1800 = "/issuance/1800";        // 1800 수동전송

		public static final String ERROR = "/error";    // 에러내역
		public static final String ERROR_ID = "/error/id";    // 에러내역
	}

	private final AdminService service;

	/*
	@GetMapping( URI.RISK + 1 )
	@ApiPageable
	public boolean genVid() throws Exception{

		return service.getVid();
	}

	*/

	@ApiOperation(value = "카드 리스크"
			, notes = "" + "\n"
			+ "법인ID idxCorp "  + "\n"
			+ "법인명 idxCorpName "  + "\n"
			+ "변경잔고 cardLimitNow "  + "\n"
			+ "부여한도 cardLimit "  + "\n"
			+ "부여한도 confirmedLimit "  + "\n"
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
		if (log.isInfoEnabled()) {
			log.info("([ riskList ]) $user='{}'", user);
		}
		return service.riskList(riskDto, user.idx(), pageable);
	}

	@ApiOperation(value = "현재 잔고" , notes = "" + "\n" + "")
    @GetMapping( URI.RISK_ID_NOWBALANCE )
    public ResponseEntity riskIdNowbalance(@ApiIgnore @CurrentUser CustomUser user, @RequestParam(required = false) Long idxCorp) {
        if (log.isInfoEnabled()) {
            log.info("([ riskIdNowbalance ]) $user='{}'", user);
        }
        return service.riskIdNowbalance( user.idx(), idxCorp);
    }



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
	@PostMapping( URI.RISK_ID_E_STOP )
	public ResponseEntity saveEmergencyStop(@ApiIgnore @CurrentUser CustomUser user,
											@RequestBody AdminDto.StopDto dto) {
		if (log.isInfoEnabled()) {
			log.info("([ saveEmergencyStop ]) $user='{}' $dto='{}'", user, dto);
		}
		return service.saveEmergencyStop(user.idx(), dto.idxCorp, dto.booleanValue);
	}

	@ApiOperation(value = "일시정지" , notes = "" + "\n booleanValue true / false ")
	@PostMapping( URI.RISK_ID_A_STOP )
	public ResponseEntity savePause(@ApiIgnore @CurrentUser CustomUser user,
									@RequestBody AdminDto.StopDto dto) {
		if (log.isInfoEnabled()) {
			log.info("([ savePause ]) $user='{} $dto='{}'", user, dto);
		}
		return service.savePause(user.idx(),  dto.idxCorp, dto.booleanValue);
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
			+ " confirmedLimit;    //승인 한도 \n"
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
		if (log.isInfoEnabled()) {
			log.info("([ riskListSelected ]) $user='{}'", user);
		}
		return service.riskListSelected(riskDto, user.idx(), idxCorp, pageable);
	}

	@ApiOperation(value = "법인 정보"
			, notes = "" + "\n"
			+ "idx : idx " + "\n "
			+ "법인명 : resCompanyNm " + "\n "
			+ "사업자등록번호 : resCompanyIdentityNo " + "\n "
			+ "대표자 : resUserNm " + "\n "
			+ "업태 : resBusinessItems " + "\n "
			+ "종목 : resBusinessTypes " + "\n "
			+ "createdAt : createdAt " + "\n "
			+ "대표이사 연대보증 여부 : ceoGuarantee " + "\n "
			+ "보증금 : depositGuarantee " + "\n "
			+ "카드발급여부 : cardIssuance " + "\n "
			+ "벤처인증 : ventureCertification " + "\n "
			+ "투자유치 : vcInvestment " + "\n "
			+ "에러건수가 있을경우 true : boolError " + "\n "
			+ "pause or emergencyStop 있을경우 true : boolPauseStop " + "\n "
			+ "\n\n\n\n 정렬 \n "
			+ " resCompanyNm " + " \n "
			+ " resCompanyIdentityNo " + " \n "
			+ " resUserNm " + " \n "
			+ " resBusinessItems " + " \n "
			+ " resBusinessTypes " + " \n "
			+ " riskConfig.ceoGuarantee " + " \n "
			+ " riskConfig.depositGuarantee " + " \n "
			+ " riskConfig.depositPayment " + " \n "
			+ " riskConfig.cardIssuance " + " \n "
			+ " riskConfig.ventureCertification " + " \n "
			+ " riskConfig.vcInvestment " + " \n "
			+ " -- boolError, boolPauseStop 는 안됨 " + " \n "
	)
	@GetMapping( URI.CORP )
	@ApiPageable
	public ResponseEntity corpList(@ModelAttribute CorpCustomRepository.SearchCorpDto CorpDto
			, @ApiIgnore @CurrentUser CustomUser user, @PageableDefault Pageable pageable) {
		if (log.isInfoEnabled()) {
			log.info("([ corpList ]) $user='{}'", user);
		}
		return service.corpList(CorpDto, user.idx(), pageable);
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

	@ApiOperation(value = "현금흐름"
			, notes = "" + "\n"
			+ "법인명 corpName" + "\n"
			+ "updateStatus true/false" + "\n"
	)
	@GetMapping( URI.CASH )
	@ApiPageable
	public ResponseEntity cashList(@ApiIgnore @CurrentUser CustomUser user, @PageableDefault Pageable pageable
			, @RequestParam(required = false) String corpName, @RequestParam(required = false) String updateStatus  ) {
		if (log.isInfoEnabled()) {
			log.info("([ cashList ]) $user='{}'", user);
		}
		return service.cashList(user.idx(), corpName, updateStatus,pageable);
	}

	@ApiOperation(value = "현금흐름 상세"
			, notes = "" + "\n"
			+ "법인id idxCorp" + "\n"
	)
	@GetMapping( URI.CASH_ID_LIST )
	public ResponseEntity cashIdList(@ApiIgnore @CurrentUser CustomUser user
			, @RequestParam(required = false) Long idxCorp) {
		if (log.isInfoEnabled()) {
			log.info("([ cashIdList ]) $user='{}'", user);
		}
		return service.cashIdList(user.idx(), idxCorp);
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

	@ApiOperation(value = "계좌 스크래핑 업데이트"
			, notes = "" + "\n"
			+ "법인id idxCorp" + "\n"
	)
	@GetMapping( URI.SCRAPING_UPDATE )
	public ResponseEntity scrapingUpdate(@ApiIgnore @CurrentUser CustomUser user,  @RequestParam(required = false) Long idxCorp) {
		if (log.isInfoEnabled()) {
			log.info("([ scrapingUpdate ]) $user='{}'", user);
		}
		return service.scrapingUpdate(user.idx(), idxCorp);
	}


	@ApiOperation(value = "에러내역"
			, notes = "" + "\n"
			+ "Sort 방식 " + "\n"
			+ "idxCorp  " + "\n"
			+ "updatedAt  " + "\n"
			+ "corpName  " + "\n"
			+ "bankName  " + "\n"
			+ "account  " + "\n"
			+ "accountDisplay  " + "\n"
			+ "errorMessage  " + "\n"
			+ "errorCode  " + "\n"
			+ "transactionId  " + "\n"
	)
	@GetMapping(URI.ERROR)
	@ApiPageable
	public ResponseEntity errorList(@ApiIgnore @CurrentUser CustomUser user, @PageableDefault Pageable pageable
			, @ModelAttribute ResBatchListCustomRepository.ErrorSearchDto dto
	) {
		if (log.isInfoEnabled()) {
			log.info("([ errorList ]) $user='{}'", user);
		}
		return service.errorList(user.idx(), pageable, dto);
	}

	private final ResumeService resumeService;

	@ApiOperation(value = "1800(전자서명) 수동전송"
			, notes = "" + "\n" + "1800(전자서명) 수동전송" + "\n"
	)
	@PostMapping(URI.ISSUANCE_1800)
	public ResponseEntity<ApiResponse.ApiResult> issuance1800(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody @Valid AdminDto.Issuance1800Req request) {

		if (log.isInfoEnabled()) {
			log.info("([ issuance1800 ]) $user='{}' $dto='{}'", user, request);
		}

		if (!service.isGowidAdmin(user.idx())) {
			throw new BadRequestException(ErrorCode.Api.NO_PERMISSION);
		}
		resumeService.procAdmin1800(request);

		log.info("### ADMIN 1800 END ###");
		return ResponseEntity.ok().body(ApiResponse.ApiResult.builder().code(Const.API_SHINHAN_RESULT_SUCCESS).build());
	}
}




