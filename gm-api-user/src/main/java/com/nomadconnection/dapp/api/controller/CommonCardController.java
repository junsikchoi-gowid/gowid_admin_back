package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.service.CommonCardService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping(CommonCardController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "법인카드 발급 공통", description = CommonCardController.URI.BASE)
public class CommonCardController {

	public static class URI {
		public static final String BASE = "/issuance/v1";
		public static final String CORPORATION_TYPE = "/corporation/type";
		public static final String VENTURE = "/venture";
		public static final String STOCKHOLDER_FILES = "/stockholder/files";
		public static final String STOCKHOLDER_FILES_IDX = "/stockholder/files/{idxFile}";
		public static final String ISSUANCE = "/issuance";
		public static final String EXCHANGE = "/exchange";
		public static final String CARD_TYPE = "/card/type";
		public static final String ISSUANCE_DEPTH = "/issuance/depth";
	}

	private final CommonCardService service;

	@ApiOperation("법인정보 업종종류 조회")
	@GetMapping(URI.CORPORATION_TYPE)
	public ResponseEntity<List<CardIssuanceDto.BusinessType>> getBusinessType(
			@ApiIgnore @CurrentUser CustomUser user) {
		if (log.isInfoEnabled()) {
			log.info("([ getBusinessType ]) $user='{}'", user);
		}

		return ResponseEntity.ok().body(service.getBusinessType());
	}

	@ApiOperation("주주명부 파일 등록")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "fileType", value = "BASIC:주주명부, MAJOR:1대주주명부", dataType = "String")
	})
	@PostMapping(URI.STOCKHOLDER_FILES)
	public ResponseEntity<List<CardIssuanceDto.StockholderFileRes>> uploadStockholderFile(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam(required = false) String depthKey,
			@RequestParam Long idxCardInfo,
			@RequestParam String fileType,
			@RequestPart MultipartFile[] file_1,
			@RequestPart MultipartFile[] file_2) throws IOException {
		if (log.isInfoEnabled()) {
			log.info("([ uploadStockholderFile ]) $user='{}' $file_1='{}' $file_2='{}' $idx_cardInfo='{}'", user, file_1, file_2, idxCardInfo);
		}

		return ResponseEntity.ok().body(service.registerStockholderFile(user.idx(), file_1, file_2, fileType, idxCardInfo, depthKey));
	}

	@ApiOperation("주주명부 파일 삭제")
	@DeleteMapping(URI.STOCKHOLDER_FILES_IDX)
	public ResponseEntity<ResponseEntity.BodyBuilder> deleteStockholderFile(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam(required = false) String depthKey,
			@RequestParam Long idxCardInfo,
			@PathVariable Long idxFile) throws IOException {
		if (log.isInfoEnabled()) {
			log.info("([ deleteStockholderFile ]) $user='{}' $idx_file='{}' $idx_cardInfo='{}'", user, idxFile, idxCardInfo);
		}

		service.deleteStockholderFile(user.idx(), idxFile, idxCardInfo, depthKey);
		return ResponseEntity.ok().build();
	}

	@ApiOperation("카드발급정보 전체조회")
	@GetMapping(URI.ISSUANCE)
	public ResponseEntity<CardIssuanceDto.CardIssuanceInfoRes> getCardIssuanceByUser(
			@ApiIgnore @CurrentUser CustomUser user) {
		if (log.isInfoEnabled()) {
			log.info("([ getCardIssuanceByUser ]) $user='{}'", user);
		}

		return ResponseEntity.ok().body(service.getCardIssuanceInfoByUser(user.idx()));
	}

	@ApiOperation("벤처기업사 조회")
	@GetMapping(URI.VENTURE)
	public ResponseEntity<List<String>> getVenture(
			@ApiIgnore @CurrentUser CustomUser user) {
		if (log.isInfoEnabled()) {
			log.info("([ getCardIssuanceByUser ]) $user='{}'", user);
		}

		return ResponseEntity.ok().body(service.getVentureBusiness());
	}

	@ApiOperation("상장거래소 조회")
	@GetMapping(URI.EXCHANGE)
	public ResponseEntity<List<CardIssuanceDto.ExchangeType>> getListedExchangeType(
			@ApiIgnore @CurrentUser CustomUser user) {
		if (log.isInfoEnabled()) {
			log.info("([ getListedExchangeType ]) $user='{}'", user);
		}

		return ResponseEntity.ok().body(service.getListedExchangeType());
	}

	@ApiOperation("발급가능한 카드사 조회")
	@GetMapping(URI.CARD_TYPE)
	public ResponseEntity<List<CardIssuanceDto.IssuanceCardType>> getIssuanceCardType(
			@ApiIgnore @CurrentUser CustomUser user) {
		if (log.isInfoEnabled()) {
			log.info("([ getIssuanceCardType ]) $user='{}'", user);
		}

		return ResponseEntity.ok().body(service.getIssuanceCardType());
	}

	@ApiOperation("발급화면 진행상황 저장")
	@PostMapping(URI.ISSUANCE_DEPTH)
	public ResponseEntity saveIssuanceDepth(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam String depthKey) {
		if (log.isInfoEnabled()) {
			log.info("([ saveIssuanceDepth ]) $user='{}' $depthKey='{}'", user, depthKey);
		}

		service.saveIssuanceDepth(user.idx(), depthKey);
		return ResponseEntity.ok().build();
	}
}
