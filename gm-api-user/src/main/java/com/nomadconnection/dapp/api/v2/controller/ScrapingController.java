package com.nomadconnection.dapp.api.v2.controller;

import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.api.dto.ConnectedMngDto.AccountNt;
import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.v2.service.scraping.FinancialStatementsService;
import com.nomadconnection.dapp.api.v2.service.scraping.ScrapingService;
import com.nomadconnection.dapp.codef.io.helper.ResponseCode;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController("ScrapingV2Controller")
@CrossOrigin(allowCredentials = "true")
@RequiredArgsConstructor
@RequestMapping("/codef/v2")
@Api(tags = "CodeF 스크래핑 V2")
public class ScrapingController {

	private final ScrapingService scrapingService;
	private final FinancialStatementsService financialStatementsService;

	@ApiOperation(value = "인증서 등록(커넥티드아이디 발급) 국세청 관련 등록")
	@PostMapping("/account/create/nt")
	public ApiResponse<?> createNt(
		@ApiIgnore @CurrentUser CustomUser user,
		@RequestBody AccountNt dto) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("([ createNt ]) $user='{}', $dto='{}'", user, dto);
		}
		Long userIdx = user.idx();
		scrapingService.scrap(userIdx, dto);

		return ApiResponse.builder()
			.result(ApiResponse.ApiResult.builder()
				.code(ResponseCode.CF00000.getCode())
				.desc(ResponseCode.CF00000.getMessage())
				.build())
			.build();
	}

	@ApiOperation(value = "재무제표 스크래핑 및 이미지 저장 ")
	@PostMapping("/account/register/corp")
	public ApiResponse<?> scrapFinancialStatements(
		@ApiIgnore @CurrentUser CustomUser user,
		@RequestBody ConnectedMngDto.CorpInfo dto
	) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("([ scrapFinancialStatements ]) $user='{}', $dto='{}'", user, dto);
		}
		Long userIdx = user.idx();
		ApiResponse.ApiResult response = financialStatementsService.scrapAndSaveFullText(userIdx, dto);

		return ApiResponse.builder()
			.result(response)
			.build();
	}
}
