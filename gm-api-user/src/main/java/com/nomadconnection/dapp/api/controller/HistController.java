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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
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
		public static final String CARDLIST = "/monthusedcard";
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
	public Collection<CardTransactionCustomRepository.PerDailyDto> getHistoryByDate(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam Integer year,
			@RequestParam Integer month,
			@RequestParam(required = false) List<Long> cards
	){
		return service.historyByDate(user.idx(), year, month, cards);
	}



	//==================================================================================================================
	//
	//	D-1. 카드리스트 정보로 이용내역 상세정보 - 종류별
	//	input : 종류, 일, 카드리스트
	//	output : List - 시간, 항목, 사용자명, 부서명, 위치, 국내, 정상, 금액
	//
	//==================================================================================================================

	@ApiOperation(value = " 카드리스트 정보로 이용내역 상세정보 - 종류별 ", notes = "카드리스트 정보로 이용내역 일자별 합계 - 종류별" +
			"\n ### Remarks" +
			"\n" +
			"\n - " +
			"\n")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "date", value = "검색년월일", defaultValue =  "20191211", dataType = "Integer"),
			@ApiImplicitParam(name = "cards", value = "카드정보 리스트 ( 전체 or 선택한 카드정보들) \n ", dataType = "String", allowMultiple = true ),
			@ApiImplicitParam(name = "type", value = "검색타입" +
					"날짜별 Value : 0 \n" +
					"항목별 Value : 1 \n" +
					"지역별 Value : 2 ", defaultValue =  "0", dataType = "Integer")
	})
	@GetMapping(URI.DAYDETAILS)
	public Page<CardTransactionCustomRepository.PerDailyDetailDto> getDayDetails(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam Integer date,
			@RequestParam(required = false) List<Long> cards,
			@RequestParam Integer type,
			@ApiIgnore Pageable pageable)
	{
		return service.historyByDateUseType(user.idx(), date, cards, type, pageable);
	}



	//==================================================================================================================
	//
	//	D-1. 카드리스트 - 카드별 사용금액 group by
	//	input : 사용자정보
	//	output : 사용자이름, 부서이름, 카드정보 idx, 카드별 사용금액 group by
	//
	//==================================================================================================================

	@ApiOperation(value = " 카드리스트 - 카드별 사용금액 ", notes = "카드리스트 - 카드별 사용금액 group by " +
			"\n ### Remarks" +
			"\n" +
			"\n - " +
			"\n")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "yearmonth", value = "검색년도", defaultValue =  "201912", dataType = "Integer")
	})
	@GetMapping(URI.CARDLIST)
	public Collection<CardTransactionCustomRepository.CardListDto> getMonthUsedCard( @ApiIgnore @CurrentUser CustomUser user, @RequestParam int yearmonth){
		if (log.isDebugEnabled()) {
			log.debug("([ putDept ]) $user='{}', $yearmonth='{}'", user, yearmonth);
		}
		return service.MonthUsedCard(user.idx(), yearmonth);
	}

	//==================================================================================================================
	//
	//	D-1. 결재정보 1건의 상세검색
	//	input : 결재정보
	//	output : 제출여부, 승인일시, 승인번호, 거래유형, 할부, 공급가액, 부가세, 상세 승인 내역, 가맹점명, 사업자번호
	//
	//==================================================================================================================

	@ApiOperation(value = " 결재정보 1건의 상세검색 ", notes = "결재정보 1건의 상세검색" +
			"\n ### Remarks" +
			"\n" +
			"\n - " +
			"\n")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "iYearMon", value = "검색년월", defaultValue =  "201912", dataType = "Integer"),
			@ApiImplicitParam(name = "card", value = "카드정보 ", dataType = "String", allowMultiple = true )
	})
	@GetMapping(URI.DAYDETAILS_DAYDETAIL)
	public CardTransactionDto.UsedInfo getUseDetail(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam Integer iYearMon,
			@RequestParam String card
	){
 		// return service.usedDetail(user, iYearMon, card);
		return null;
	}

}