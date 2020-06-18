package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.service.AuthService;
import com.nomadconnection.dapp.api.service.BankService;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;


@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping(BankController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "은행", description = BankController.URI.BASE)
public class BankController {

	public static class URI {
		public static final String BASE = "/bank/v1";

		public static final String CHECK_ACCOUNT 		= "/check/account";			// 계좌 + 계좌리스트 스크래핑
		public static final String CHECK_ACCOUNTLIST	= "/check/accountlist";         // 계좌리스트 스크래핑
		public static final String CHECK_ACCOUNTLIST45	= "/check/accountlist45";         // 계좌리스트 스크래핑 45일간
		public static final String CHECK_REFRESH		= "/check/refresh";             // 새로고침
		public static final String CHECK_S_R			= "/check/scraping_risk";             // 새로고침


		public static final String DAY_BALANCE	 	= "/balance/day";	// (기간별) 일별 입출금 잔고
		public static final String MONTH_BALANCE 	= "/balance/month";	// (기간별) 월별 입출금 잔고
		public static final String MONTH_BALANCE_EXT 	= "/balance/month_ext";	// (기간별) 월별 입출금 잔고
		public static final String MONTH_INOUTSUM 	= "/monthinoutsum";	// 월 총 입출금

		public static final String BURN_RATE 	 	= "/burn-rate";		// Burn Rate
		public static final String ACCOUNT_LIST		= "/account";		// 계좌정보 리스트
		public static final String TRANSACTION_LIST	= "/accounts";		// 계좌별 거래내역
		public static final String NICKNAME 		= "/nickname";    	// 계좌 별명수정

	}

	private final Boolean boolDebug = true;
	private final BankService service;
	private final AuthService serviceAuth;
	private final UserService serviceUser;

	@ApiOperation(value = "계좌정보", notes = "" + "\n")
	@GetMapping( URI.ACCOUNT_LIST )
	public ResponseEntity AccountList(@ApiIgnore @CurrentUser CustomUser user, @RequestParam(required = false)  Long idxCorp) {
		if (log.isDebugEnabled()) {
			// log.debug("([TransactionList]) $dto='{}'", dto);
		}
		return service.accountList(user.idx(), idxCorp);
	}

	@ApiOperation(value = "계좌별 거래내역", notes = "" + "\n")
	@GetMapping( URI.TRANSACTION_LIST )
	public ResponseEntity TransactionList(@ApiIgnore @CurrentUser CustomUser user, @ModelAttribute BankDto.TransactionList dto
			,@RequestParam Integer page, @RequestParam Integer pageSize , @RequestParam(required = false)  Long idxCorp) {
		if (log.isDebugEnabled()) {
			log.debug("([TransactionList]) $dto='{}'", dto);
		}
		return service.transactionList(dto, user.idx(), page, pageSize, idxCorp);
	}

	@ApiOperation(value = "(기간별) 일별 입출금 잔고", notes = "" + "\n")
	@GetMapping( URI.DAY_BALANCE )
	public ResponseEntity DayBalance(@ApiIgnore @CurrentUser CustomUser user,@ModelAttribute BankDto.DayBalance dto) {
		if (log.isDebugEnabled()) {
			log.debug("([DayBalance]) $dto='{}'", dto);
		}
		return service.dayBalance(dto, user.idx());
	}

	@ApiOperation(value = "(기간별) 월별 입출금 잔고", notes = "" + "\n")
	@GetMapping( URI.MONTH_BALANCE )
	public ResponseEntity MonthBalance(@ApiIgnore @CurrentUser CustomUser user,@ModelAttribute BankDto.MonthBalance dto) {
		if (log.isDebugEnabled()) {
			log.debug("([MonthBalance]) $dto='{}'", dto);
		}
		return service.monthBalance(dto, user.idx());
	}

	@ApiOperation(value = "(기간별) 월별 입출금 잔고 외부", notes = "" + "\n")
	@GetMapping( URI.MONTH_BALANCE_EXT )
	public ResponseEntity MonthBalance(@RequestParam String id,
									   @RequestParam String pw,
									   @RequestParam String startDate,
									   @RequestParam String endDate,
									   @RequestParam String companyId) {
		if (log.isDebugEnabled()) {
			log.debug("([MonthBalance]) $dto='{}'", id);
		}
		return service.findMonthHistory_External(id , pw, startDate , endDate , companyId);
	}

	@ApiOperation(value = "입출금 합계", notes = "" + "\n")
	@GetMapping( URI.MONTH_INOUTSUM )
	public ResponseEntity MonthInOutSum(@ApiIgnore @CurrentUser CustomUser user,@ModelAttribute BankDto.MonthInOutSum dto, @RequestParam(required = false) Long idxCorp) {
		if (log.isDebugEnabled()) {
			log.debug("([MonthInOutSum]) $dto='{}'", dto);
		}
		return service.monthInOutSum(dto, user.idx(), idxCorp);
	}

	@ApiOperation(value = "Burn Rate", notes = "" + "\n")
	@GetMapping( URI.BURN_RATE )
	public ResponseEntity BurnRate(@ApiIgnore @CurrentUser CustomUser user,
								@RequestParam(required = false) Long idxCorp) {
		return service.burnRate(user.idx(), idxCorp);
	}

	@ApiOperation(value = "계좌 별명수정", notes = "" + "\n")
	@PostMapping( URI.NICKNAME )
	public ResponseEntity Nickname(@ApiIgnore @CurrentUser CustomUser user,@RequestBody BankDto.Nickname dto) {
		if (log.isDebugEnabled()) {
			log.debug("([Nickname]) $dto='{}'", dto);
		}
		return service.nickname(dto, user.idx());
	}

	@ApiOperation(value = "계좌 + 거래내역 스크래핑", notes = "" + "\n")
	@GetMapping( URI.CHECK_ACCOUNT )
	public ResponseEntity CheckAccount(@ApiIgnore @CurrentUser CustomUser user) throws IOException, InterruptedException {
		return service.checkAccount(user.idx());
	}

	@ApiOperation(value = " 거래내역 스크래핑", notes = "" + "\n")
	@GetMapping( URI.CHECK_ACCOUNTLIST )
	public ResponseEntity CheckAccountList(@ApiIgnore @CurrentUser CustomUser user) throws IOException, InterruptedException {
		return service.checkAccountList(user.idx());
	}

	@ApiOperation(value = "계좌 + 거래내역 스크래핑 45일간만", notes = "" + "\n")
	@GetMapping( URI.CHECK_ACCOUNTLIST45 )
	public ResponseEntity checkAccountList45(@ApiIgnore @CurrentUser CustomUser user) throws IOException, InterruptedException {
		return service.checkAccountList45(user.idx());
	}

	@ApiOperation(value = "새로고침", notes = "" + "\n")
	@GetMapping( URI.CHECK_REFRESH )
	public ResponseEntity checkRefresh(@ApiIgnore @CurrentUser CustomUser user, @RequestParam(required = false) Long idxCorp) {

		return service.refresh(user.idx(), idxCorp);
	}

	@ApiOperation(value = "스크래핑 상태확인 및 리스크 저장 확인", notes = "" + "\n")
	@GetMapping( URI.CHECK_S_R )
	public ResponseEntity check_scraping_risk(@ApiIgnore @CurrentUser CustomUser user, @RequestParam(required = false) Long idxCorp) {

		return service.check_scraping_risk(user.idx(), idxCorp);
	}
}
