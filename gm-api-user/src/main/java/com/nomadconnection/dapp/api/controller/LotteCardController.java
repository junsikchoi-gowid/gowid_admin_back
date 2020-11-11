package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.dto.lotte.StatusDto;
import com.nomadconnection.dapp.api.service.lotte.LotteCardService;
import com.nomadconnection.dapp.api.service.lotte.LotteIssuanceService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.domain.common.SignatureHistory;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping(LotteCardController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "롯데 법인카드 발급", description = LotteCardController.URI.BASE)
public class LotteCardController {

	public static class URI {
		public static final String BASE = "/issuance/v1/lotte";
		public static final String CORPORATION = "/corporation";
		public static final String CORPORATION_EXTEND = "/corporation/extend";
		public static final String VENTURE = "/venture";
		public static final String STOCKHOLDER = "/stockholder";
		public static final String ACCOUNT = "/account";
		public static final String ISSUANCE = "/issuance";
		public static final String CARD = "/card";
		public static final String CARD_NEW = "/card/new";
		public static final String CARD_HOPE_LIMIT = "/card/hopeLimit";
		public static final String CEO = "/ceo";
		public static final String CEO_ID = "/ceo/identification";
		public static final String CEO_CORRESPOND = "/ceo/correspond";
		public static final String MANAGER = "/manager";
	}

	private final LotteCardService service;
	private final LotteIssuanceService issuanceService;

	@ApiOperation("법인정보 수정")
	@PutMapping(URI.CORPORATION)
	public ResponseEntity<CardIssuanceDto.CorporationRes> updateCorporation(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam Long idxCardInfo,
			@RequestParam(required = false) String depthKey,
			@RequestBody @Valid CardIssuanceDto.RegisterCorporation dto) {
		if (log.isInfoEnabled()) {
			log.info("([ updateCorporation ]) $user='{}' $dto='{}' $idx_cardInfo='{}'", user, dto, idxCardInfo);
		}

		return ResponseEntity.ok().body(service.updateCorporation(user.idx(), dto, idxCardInfo, depthKey));
	}

	@ApiOperation("법인추가정보 등록")
	@PostMapping(URI.CORPORATION_EXTEND)
	public ResponseEntity<CardIssuanceDto.CorporationExtendRes> updateCorporationExtend(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam Long idxCardInfo,
			@RequestParam(required = false) String depthKey,
			@RequestBody @Valid CardIssuanceDto.RegisterCorporationExtend dto) {
		if (log.isInfoEnabled()) {
			log.info("([ updateCorporationExtend ]) $user='{}' $dto='{}' $idx_cardInfo='{}'", user, dto, idxCardInfo);
		}

		return ResponseEntity.ok().body(service.updateCorporationExtend(user.idx(), dto, idxCardInfo, depthKey));
	}

	@ApiOperation("벤처기업정보 등록")
	@PostMapping(URI.VENTURE)
	public ResponseEntity<CardIssuanceDto.VentureRes> registerVenture(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam Long idxCardInfo,
			@RequestParam(required = false) String depthKey,
			@RequestBody @Valid CardIssuanceDto.RegisterVenture dto) {
		if (log.isInfoEnabled()) {
			log.info("([ registerVenture ]) $user='{}' $dto='{}' $idx_cardInfo='{}'", user, dto, idxCardInfo);
		}

		return ResponseEntity.ok().body(service.registerVenture(user.idx(), dto, idxCardInfo, depthKey));
	}

	@ApiOperation("주주명부 등록")
	@PostMapping(URI.STOCKHOLDER)
	public ResponseEntity<CardIssuanceDto.VentureRes> registerStockholder(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam Long idxCardInfo,
			@RequestParam(required = false) String depthKey,
			@RequestBody @Valid CardIssuanceDto.RegisterStockholder dto) {
		if (log.isInfoEnabled()) {
			log.info("([ registerStockholder ]) $user='{}' $dto='{}' $idx_cardInfo='{}'", user, dto, idxCardInfo);
		}

		return ResponseEntity.ok().body(service.registerStockholder(user.idx(), dto, idxCardInfo, depthKey));
	}

	@ApiOperation("카드 희망한도 저장")
	@PostMapping(URI.CARD_HOPE_LIMIT)
	public ResponseEntity<CardIssuanceDto.CardRes> saveHopeLimit(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam(required = false) String depthKey,
			@RequestBody @Valid CardIssuanceDto.HopeLimitReq dto) {
		if (log.isInfoEnabled()) {
			log.info("([ saveHopeLimit ]) $user='{}' $dto='{}'", user, dto);
		}

		return ResponseEntity.ok().body(service.saveHopeLimit(user.idx(), dto, depthKey));
	}

	@ApiOperation("카드발급정보 등록")
	@PostMapping(URI.ISSUANCE)
	public ResponseEntity<CardIssuanceDto.CardRes> registerCard(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam Long idxCardInfo,
			@RequestParam(required = false) String depthKey,
			@RequestBody @Valid CardIssuanceDto.RegisterCard dto) {
		if (log.isInfoEnabled()) {
			log.info("([ registerCard ]) $user='{}' $dto='{}' $idx_cardInfo='{}'", user, dto, idxCardInfo);
		}

		return ResponseEntity.ok().body(service.registerCard(user.idx(), dto, idxCardInfo, depthKey));
	}

	@ApiOperation("결제계좌 등록")
	@PostMapping(URI.ACCOUNT)
	public ResponseEntity<CardIssuanceDto.AccountRes> registerAccount(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam Long idxCardInfo,
			@RequestParam(required = false) String depthKey,
			@RequestBody @Valid CardIssuanceDto.RegisterAccount dto) {
		if (log.isInfoEnabled()) {
			log.info("([ registerAccount ]) $user='{}' $dto='{}' $idx_cardInfo='{}'", user, dto, idxCardInfo);
		}

		return ResponseEntity.ok().body(service.registerAccount(user.idx(), dto, idxCardInfo, depthKey));
	}

	@ApiOperation("대표자 종류")
	@GetMapping(URI.CEO)
	public ResponseEntity<CardIssuanceDto.CeoTypeRes> getCeo(
			@ApiIgnore @CurrentUser CustomUser user) {
		if (log.isInfoEnabled()) {
			log.info("([ getCeo ]) $user='{}'", user);
		}

		return ResponseEntity.ok().body(service.getCeoType(user.idx()));
	}

	@ApiOperation("대표자 등록")
	@PostMapping(URI.CEO)
	public ResponseEntity<CardIssuanceDto.CeoRes> registerCEO(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam Long idxCardInfo,
			@RequestParam(required = false) String depthKey,
			@RequestBody @Valid CardIssuanceDto.RegisterCeo dto) {
		if (log.isInfoEnabled()) {
			log.info("([ registerCEO ]) $user='{}' $dto='{}' $idx_cardInfo='{}'", user, dto, idxCardInfo);
		}

		return ResponseEntity.ok().body(service.registerCeo(user.idx(), dto, idxCardInfo, depthKey));
	}

	@ApiOperation("관리책임자 업데이트")
	@PostMapping(URI.MANAGER)
	public ResponseEntity<?> updateManager(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam Long idxCardInfo,
			@RequestParam(required = false) String depthKey,
			@RequestBody @Valid CardIssuanceDto.UpdateManager dto) {
		if (log.isInfoEnabled()) {
			log.info("([ updateManager ]) $user='{}' $dto='{}' $idx_cardInfo='{}'", user, dto, idxCardInfo);
		}

		service.updateManager(user.idx(), dto, idxCardInfo, depthKey);
		return ResponseEntity.ok().build();
	}

	@ApiOperation(value = "신분증 본인 확인")
	@PostMapping(URI.CEO_ID)
	public ResponseEntity<?> saveCeoIdentification(
			HttpServletRequest request,
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam(required = false) String depthKey,
			@ModelAttribute @Valid CardIssuanceDto.IdentificationReq dto) {
		if (log.isInfoEnabled()) {
			log.info("([ saveCeoIdentification ]) $user='{}' $dto='{}'", user, dto);
		}

		service.saveCeoIdentification(request, user.idx(), dto, depthKey);
		return ResponseEntity.ok().build();
	}

	@ApiOperation(value = "법인카드 발급 신규대상자 확인")
	@PostMapping(URI.CARD_NEW)
	public ResponseEntity<StatusDto> verifyNewMember(
			@ApiIgnore @CurrentUser CustomUser user) {
		if (log.isInfoEnabled()) {
			log.info("([ verifyNewMember ]) $user='{}'", user);
		}
		return ResponseEntity.ok().body(issuanceService.verifyNewMember(user.idx()));
	}

	@ApiOperation(value = "법인카드 발급 신청")
	@PostMapping(URI.CARD)
	public ResponseEntity<CardIssuanceDto.IssuanceRes> application(
			@ApiIgnore @CurrentUser CustomUser user,
			@ModelAttribute @Valid CardIssuanceDto.IssuanceReq request) {
		if (log.isInfoEnabled()) {
			log.info("([ application ]) $user='{}' $dto='{}'", user, request);
		}
		SignatureHistory signatureHistory = issuanceService.verifySignedBinaryAndSave(user.idx(), request.getSignedBinaryString());
		issuanceService.issuance(user.idx(), request, signatureHistory.getIdx());

		return ResponseEntity.ok().build();
	}

	@Deprecated
	@ApiOperation(value = "대표자 일치 확인")
	@PostMapping(URI.CEO_CORRESPOND)
	public ResponseEntity<?> verifyCorrespondCeo(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam(required = false) String depthKey,
			@RequestBody @Valid CardIssuanceDto.CeoValidReq dto) {
		if (log.isInfoEnabled()) {
			log.info("([ verifyCorrespondCeo ]) $user='{}' $dto='{}'", user, dto);
		}

		service.verifyValidCeo(user.idx(), dto, depthKey);
		return ResponseEntity.ok().build();
	}
}
