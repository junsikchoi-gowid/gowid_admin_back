package com.nomadconnection.dapp.api.v2.controller.scraping;

import com.nomadconnection.dapp.api.controller.UserEtcController;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.service.lotte.LotteIssuanceService;
import com.nomadconnection.dapp.api.service.shinhan.IssuanceService;
import com.nomadconnection.dapp.api.v2.controller.flow.FlowBaseController;
import com.nomadconnection.dapp.api.v2.dto.ImageReqDto;
import com.nomadconnection.dapp.api.v2.enums.ScrapingType;
import com.nomadconnection.dapp.api.v2.service.scraping.RecoveryService;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.common.SignatureHistory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(RecoveryController.URI.BASE)
@Api(tags = "[03] CodeF 수동처리 스크래핑" , description = RecoveryController.URI.BASE)
public class RecoveryController {
	public static class URI {
		public static final String BASE = "/recovery";
	}

	private final RecoveryService recoveryService;

	private final LotteIssuanceService lotteIssuanceService;

	private final IssuanceService issuanceService;

	@ApiOperation(value = "수동 스크래핑")
	@PostMapping("/scrap/{userIdx}")
	public ApiResponse<?> scrapByScrapingType(@PathVariable Long userIdx,
	                                          @RequestParam ScrapingType scrapingType,
	                                          @RequestParam(required = false) String resClosingStandards,
	                                          @RequestParam CardType cardType) throws Exception {
		recoveryService.scrapByScrapingType(userIdx, scrapingType, resClosingStandards, cardType);
		return ApiResponse.OK();
	}

	@ApiOperation(value = "전문 수동 발송")
	@PostMapping("/fulltext/{userIdx}")
	public ApiResponse<?> sendFullText(@PathVariable Long userIdx,
	                                   @RequestParam ScrapingType scrapingType) throws Exception {
		recoveryService.sendFullText(userIdx, scrapingType);
		return ApiResponse.OK();
	}

	@ApiOperation(value = "이미지 수동 전송")
	@PostMapping("/image/{userIdx}")
	public ApiResponse<?> sendImage(@PathVariable Long userIdx, @RequestBody ImageReqDto dto) {
		recoveryService.sendImage(userIdx, dto);
		return ApiResponse.OK();
	}

	@ApiOperation(value = "수동 전자서명")
	@PostMapping("/apply/{userIdx}")
	public ApiResponse<?> application(@PathVariable Long userIdx,
	                                  @RequestParam CardCompany cardCompany,
	                                  @ModelAttribute @Valid CardIssuanceDto.IssuanceReq request) throws Exception {
		if(CardCompany.isShinhan(cardCompany)){
			SignatureHistory signatureHistory = issuanceService.verifySignedBinaryAndSave(userIdx, request.getSignedBinaryString());
			issuanceService.issuance(userIdx, request, signatureHistory.getIdx());
		} else if(CardCompany.isLotte(cardCompany)){
			SignatureHistory signatureHistory = lotteIssuanceService.verifySignedBinaryAndSave(userIdx, request.getSignedBinaryString());
			lotteIssuanceService.issuance(userIdx, request, signatureHistory.getIdx());
		}

		return ApiResponse.OK();
	}

}
