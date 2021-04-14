package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.BenefitDto;
import com.nomadconnection.dapp.api.service.BenefitService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
import java.util.List;

@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping(BenefitController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "멤버십 혜택", description = BenefitController.URI.BASE)
public class BenefitController {

	public static class URI {

		// Base
		public static final String BASE = "/benefit/v1";

		// Benefit
		public static final String BENEFITS = "/benefits";
		public static final String BENEFIT = "/benefits/{idx}";

		// Benefit Payment
		public static final String BENEFIT_PAYMENTS = "/payments";
		public static final String BENEFIT_PAYMENT = "/payments/{idx}";

		// Benefit Category
		public static final String BENEFIT_CATEGORIES = "/categories";

		// Benefit SerchText
		public static final String BENEFIT_SEARCH = "/search";
	}

	private final BenefitService service;

	@ApiOperation("베네핏 목록 조회")
	@GetMapping(URI.BENEFITS)
	public ResponseEntity getBenefits(@PageableDefault Pageable pageable,
									  @RequestParam(required = false) String showAll) {
		if (log.isInfoEnabled()) {
			log.info("([ getBenefits ])");
		}
		return service.getBenefits(pageable, showAll);
	}


	@ApiOperation("베네핏 상세정보 조회")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "idx", value = "식별자(베네핏 ID)", dataType = "Long")
	})
	@GetMapping(URI.BENEFIT)
	public ResponseEntity getBenefit(@PathVariable Long idx) {

		if (log.isInfoEnabled()) {
			log.info("([ getBenefit ]) $idx_benefit='{}'", idx);
		}
		return service.getBenefit(idx);
	}


	@ApiOperation(value = "베네핏 결제 정보 저장")
	@PostMapping(URI.BENEFIT_PAYMENTS)
	public ResponseEntity saveBenefitPaymentHistory(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody BenefitDto.BenefitPaymentHistoryReq dto) {

		if (log.isDebugEnabled()) {
			log.debug("([saveBenefitPaymentHistory]) $user='{}', $dto='{}'", user, dto.toString());
		}

		return service.saveBenefitPaymentHistory(user.idx(), dto);
	}


	@ApiOperation("베네핏 결제 목록 조회")
	@GetMapping(URI.BENEFIT_PAYMENTS)
	public ResponseEntity getBenefitPaymentHistories(
			@ApiIgnore @CurrentUser CustomUser user,
			@PageableDefault Pageable pageable) {

		if (log.isInfoEnabled()) {
			log.info("([ getBenefitPaymentHistories ]) $user='{}'", user);
		}

		return service.getBenefitPaymentHistories(user.idx(), pageable);
	}


	@ApiOperation("베네핏 결제 상세정보 조회")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "idx", value = "식별자(결제 ID)", dataType = "Long")
	})
	@GetMapping(URI.BENEFIT_PAYMENT)
	public ResponseEntity getBenefitPaymentHistory(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable Long idx) {

		if (log.isInfoEnabled()) {
			log.info("([ getBenefitPaymentHistory ]) $user='{}'", user);
		}
		return service.getBenefitPaymentHistory(idx);
	}


	@Deprecated
	@ApiOperation("베네핏 카테고리 목록 조회")
	@GetMapping(URI.BENEFIT_CATEGORIES)
	public ResponseEntity getBenefitCategories() {
		if (log.isInfoEnabled()) {
			log.info("([ getBenefitCategories ])");
		}
		return service.getBenefitCategories();
	}


	@Deprecated
	@ApiOperation(value = "베네핏 검색어 저장")
	@PostMapping(URI.BENEFIT_SEARCH)
	public ResponseEntity saveBenefitSearchHistory(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody BenefitDto.BenefitSearchHistoryReq dto
			) {

		if (log.isDebugEnabled()) {
			log.debug("([saveBenefitSearchHistory]) $dto='{}'", dto);
		}

		return service.saveBenefitSearchHistory(user, dto);
	}


	@ApiOperation(value = "베네핏 저장")
	@PostMapping(URI.BENEFITS)
	public ResponseEntity saveBenefit(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody BenefitDto.SaveBenefitReq dto) {

		if (log.isDebugEnabled()) {
			log.debug("([saveBenefit]) $dto='{}'", dto);
		}

		return service.saveBenefit(user.idx(), dto);
	}


	@ApiOperation(value = "혜택 삭제")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "idx", value = "식별자(베네핏 ID)", dataType = "Long")
	})
	@DeleteMapping(URI.BENEFIT)
	public ResponseEntity deleteBenefit(@ApiIgnore @CurrentUser CustomUser user,
								 @PathVariable Long idx) {

		if (log.isInfoEnabled()) {
			log.info("([ deleteBenefit ]) $user='{}' $idxBenefit='{}'", user, idx);
		}

		return service.deleteBenefit(user.idx(), idx);
	}


	@ApiOperation(value = "베네핏 수정")
	@PutMapping(URI.BENEFIT)
	public ResponseEntity updateBenefit(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable Long idx,
			@RequestBody @Valid BenefitDto.SaveBenefitReq dto) {

		if (log.isInfoEnabled()) {
			log.info("([ updateBenefit ]) $user='{}' $idxBenefit='{}' $dto='{}'", user, idx, dto);
		}
		return service.updateBenefit(user.idx(), idx, dto);
	}


	@ApiOperation(value = "베네핏 수정(List)")
	@PutMapping(URI.BENEFITS)
	public ResponseEntity updateBenefitList(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody @Valid List<BenefitDto.UpdateBenefitListReq> dto) {

		if (log.isInfoEnabled()) {
			log.info("([ updateBenefitList ]) $user='{}' $dto='{}'", user, dto);
		}
		return service.updateBenefitList(user.idx(), dto);
	}
}