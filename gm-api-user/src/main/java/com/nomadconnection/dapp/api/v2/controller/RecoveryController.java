package com.nomadconnection.dapp.api.v2.controller;

import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.service.shinhan.IssuanceService;
import com.nomadconnection.dapp.api.v2.dto.ImageReqDto;
import com.nomadconnection.dapp.api.v2.service.scraping.FinancialStatementsService;
import com.nomadconnection.dapp.codef.io.helper.ResponseCode;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/recovery")
@Api(tags = "CodeF 수동처리 스크래핑")
public class RecoveryController {

	private final FinancialStatementsService financialStatementsService;
	private final IssuanceService issuanceService;

	@ApiOperation(value = "재무제표 수동 스크래핑")
	@PostMapping("/scrap/financial/{userIdx}")
	public ApiResponse<?> scrapFinancialStatements(@PathVariable Long userIdx,
	                                               @RequestBody ConnectedMngDto.CorpInfo dto) throws Exception {

		financialStatementsService.scrap(userIdx, dto);
		return ApiResponse.builder()
			.result(ApiResponse.ApiResult.builder()
				.code(ResponseCode.CF00000.getCode())
				.desc(ResponseCode.CF00000.getMessage())
				.build())
			.build();
	}

	@ApiOperation(value = "이미지 수동 전송")
	@PostMapping("/image/financial/{userIdx}")
	public ApiResponse<?> sendImage(@PathVariable Long userIdx, @RequestBody ImageReqDto dto) throws Exception {
		if(CardCompany.isShinhan(dto.getCardCompany())){
			issuanceService.sendImage(userIdx);
		}

		//      3. check GW Image Logic
		//      4. check Shinhan / Lotte ( Shinhan : BPR / Lotte : zip )
		return ApiResponse.OK(null);
	}

	@ApiOperation(value = "재무제표 전문 수동 발송")
	@PostMapping("/fulltext/financial/{userIdx}")
	public ApiResponse<?> sendFinancial(@PathVariable Long userIdx) throws Exception {
		issuanceService.send1520(userIdx);
		return ApiResponse.OK(null);
	}

}
