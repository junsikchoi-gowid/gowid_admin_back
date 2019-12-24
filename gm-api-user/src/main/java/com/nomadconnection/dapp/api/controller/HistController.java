package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.CardTransactionDto;
import com.nomadconnection.dapp.api.security.CustomUser;
import com.nomadconnection.dapp.api.service.HistService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CardTransactionCustomRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(HistController.URI.BASE)
@RequiredArgsConstructor
@Api(tags = "이용내역조회", description = HistController.URI.BASE)
@SuppressWarnings({"unused", "deprecation"})
public class HistController {

	@SuppressWarnings("WeakerAccess")
	public static class URI {

		public static final String BASE = "/history/v1";
		public static final String MONTHSUM = "/monthsum";
		public static final String MONTHSUMCARD = "/monthsumcard";
		public static final String DAYHEADER = "/dayheader";
		public static final String DAYDETAILS = "/daydetails";
		public static final String DAYDETAILS_DAYDETAIL = "/daydetails/{daydetail}";
	}

	private final HistService service;

	//==================================================================================================================
	//
	//	D-1. 해당달의 전체금액 - 권한에 따라 다른 결과
	//	input : 월
	//	output : 권한, 총합계
	//
	//==================================================================================================================

	@ApiOperation(value = "해당달의 전체금액", notes = "권한에 따라 다른 결과" +
			"\n ### Remarks" +
			"\n" +
			"\n - " +
			"\n")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "year", value = "검색년도", defaultValue =  "2019",dataType = "Integer"),
			@ApiImplicitParam(name = "month", value = "검색월", defaultValue = "12", dataType = "Integer"),
			@ApiImplicitParam(name = "cards", value = "카드정보 리스트 idx ( 전체 or 선택한 카드정보들) ", dataType = "Integer", allowMultiple = true )
	})
	@GetMapping(URI.MONTHSUM)
	public Long getMonthSum(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam Integer year,
			@RequestParam Integer month,
			@RequestParam(required = false) List<Long> cards
	){
		return service.monthSum(user.idx(), year, month, cards);
	}

	//==================================================================================================================
	//
	//	D-1. 카드리스트 정보로 이용내역 일자별 합계 - 종류별
	//	input : 월, 카드리스트
	//	output : List - 일자, 요일, 건수, 일자별합계
	//
	//==================================================================================================================

	@ApiOperation(value = "이용내역 일자별 합계", notes = "카드리스트 정보로 이용내역 일자별 합계 - 종류별" +
			"\n ### Remarks" +
			"\n" +
			"\n - " +
			"\n")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "year", value = "검색년도", defaultValue =  "2019", dataType = "Integer"),
			@ApiImplicitParam(name = "month", value = "검색월", defaultValue = "12",  dataType = "Integer"),
			@ApiImplicitParam(name = "cards", value = "카드정보 리스트 idx ( 전체 or 선택한 카드정보들) ", dataType = "Integer", allowMultiple = true )
	})
	@GetMapping(URI.DAYHEADER)
	// public List<CardTransactionDto.HistHeaders> getHistoryByDate(
	public List<CardTransactionCustomRepository.PerDailyDto> getHistoryByDate(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam Integer year,
			@RequestParam Integer month,
			@RequestParam(required = false) List<Long> cards
	){

		// return service.getCardList(user.idx(), year, month);
		return service.historyByDate(user.idx(), year, month, cards);
	}











	//==================================================================================================================
	//
	//	D-2. 해당달의 전체금액 = 관리자일경우 리스트 출력
	//	input : 월
	//	output : List - 사용자명, 부서명, 카드번호, 합계
	//
	//==================================================================================================================
//
//	@ApiOperation(value = "해당달의 전체금액 .. ", notes = "관리자일경우 리스트 출력" +
//			"\n ### Remarks" +
//			"\n" +
//			"\n - " +
//			"\n")
//	@ApiImplicitParams({
//			@ApiImplicitParam(name = "year", value = "검색년도", dataType = "Integer", required = true),
//			@ApiImplicitParam(name = "month", value = "검색월정보", dataType = "Integer", required = true)
//	})
//	@GetMapping(URI.MONTHSUMCARD)
//	public List<CardTransactionDto.MonthSumCard> getMonthSumCard(
//			// @ApiIgnore @CurrentUser CustomUser user,
//			@RequestBody CardTransactionDto.MonthSum dto
//	){
//
//		// return service.getCardList(user.idx(), year, month);
//		return null;
//	}



	//==================================================================================================================
	//
	//	D-1. 카드리스트 정보로 이용내역 상세정보 - 종류별
	//	input : 일, 카드리스트
	//	output : List - 시간, 항목, 사용자명, 부서명, 위치, 국내, 정상, 금액
	//
	//==================================================================================================================

	@ApiOperation(value = " 카드리스트 정보로 이용내역 상세정보", notes = "카드리스트 정보로 이용내역 일자별 합계 - 종류별" +
			"\n ### Remarks" +
			"\n" +
			"\n - " +
			"\n")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "year", value = "검색년도", dataType = "Integer", required = true),
			@ApiImplicitParam(name = "month", value = "검색월", dataType = "Integer", required = true),
			@ApiImplicitParam(name = "cards", value = "카드정보 리스트 ( 전체 or 선택한 카드정보들) ", dataType = "String", allowMultiple = true )
	})
	@GetMapping(URI.DAYDETAILS)
	public List<CardTransactionDto.DayHeader> getDayDetails(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable Integer year,
			@PathVariable Integer month,
			@PathVariable String cards
	){

		// return service.getCardList(user.idx(), year, month);
		return null;
	}

	//==================================================================================================================
	//
	//	D-1. 결재정보 1건의 상세검색
	//	input : 결재정보
	//	output : 제출여부, 승인일시, 승인번호, 거래유형, 할부, 공급가액, 부가세, 상세 승인 내역, 가맹점명, 사업자번호
	//
	//==================================================================================================================

	@ApiOperation(value = " 카드리스트 정보로 이용내역 상세정보", notes = "카드리스트 정보로 이용내역 일자별 합계 - 종류별" +
			"\n ### Remarks" +
			"\n" +
			"\n - " +
			"\n")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "year", value = "결재정보", dataType = "String", required = true),
			@ApiImplicitParam(name = "month", value = "검색월", dataType = "Integer", required = true),
			@ApiImplicitParam(name = "cards", value = "카드정보 리스트 ( 전체 or 선택한 카드정보들) ", dataType = "String", allowMultiple = true )
	})
	@GetMapping(URI.DAYDETAILS_DAYDETAIL)
	public CardTransactionDto.DayHeader getDayDetail(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable Integer year,
			@PathVariable Integer month,
			@PathVariable String cards
	){

		// return service.getCardList(user.idx(), year, month);
		return null;
	}

}