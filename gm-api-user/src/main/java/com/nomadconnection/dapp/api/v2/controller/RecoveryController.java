package com.nomadconnection.dapp.api.v2.controller;

import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.service.lotte.LotteIssuanceService;
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
	private final IssuanceService shinhanIssuanceService;
	private final LotteIssuanceService lotteIssuanceService;

	@ApiOperation(value = "재무제표 수동 스크래핑")
	@PostMapping("/scrap/financial/{userIdx}")
	public ApiResponse<?> scrapFinancialStatements(@PathVariable Long userIdx,
	                                               @RequestBody ConnectedMngDto.CorpInfo dto) throws Exception {
		financialStatementsService.scrapByHand(userIdx, dto);
		return ApiResponse.builder()
			.result(ApiResponse.ApiResult.builder()
				.code(ResponseCode.CF00000.getCode())
				.desc(ResponseCode.CF00000.getMessage())
				.build())
			.build();
	}

	@ApiOperation(value = "이미지 수동 전송")
	@PostMapping("/image/{userIdx}")
	public ApiResponse<?> sendImage(@PathVariable Long userIdx, @RequestBody ImageReqDto dto) {
		if(CardCompany.isShinhan(dto.getCardCompany())){
			shinhanIssuanceService.sendImageByHand(userIdx, dto.getImageFileType().getFileType());
		} else if(CardCompany.isLotte(dto.getCardCompany())){
			lotteIssuanceService.procImageZipByHand(userIdx);
		}
		return ApiResponse.OK(null);
	}

	@ApiOperation(value = "재무제표 전문 수동 발송")
	@PostMapping("/fulltext/financial/{userIdx}")
	public ApiResponse<?> sendFinancial(@PathVariable Long userIdx) {
		shinhanIssuanceService.send1520ByHand(userIdx);
		return ApiResponse.OK(null);
	}

}
