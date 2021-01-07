package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.SaasTrackerDto;
import com.nomadconnection.dapp.api.service.SaasTrackerService;
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

import javax.validation.Valid;

@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping(SaasTrackerController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "SaaS Tracker", description = SaasTrackerController.URI.BASE)
public class SaasTrackerController {

	public static class URI {

		// Base
		public static final String BASE = "/saas/v1/tracker";

		// Reports
		public static final String REPORTS = "/reports";

		// 월별 총 SaaS 결제 금액
		public static final String USAGE = "/usage";
		public static final String USAGE_SUMS = "/usage/sums";
		public static final String USAGE_SUMS_DETAILS = "/usage/sums/details";

		// 카테고리 별 SaaS 결제 금액
		public static final String USAGE_CATEGORIES = "/usage/categories";
		public static final String USAGE_CATEGORIES_DETAILS = "/usage/categories/details";

		// 카테고리별 사용 SaaS 목록
		public static final String CATEGORIES = "/categories";

		// 구독 중/구독 만료인 SaaS 목록
		public static final String SUBSCRIPTIONS = "/subscriptions";

		// 사용중인 SaaS 정보 수정
		public static final String INFO_DETAILS = "/infos/{idx}";

		// 결제 예정 목록
		public static final String SCHEDULES = "/schedules";
		public static final String SCHEDULES_CALENDAR = "/schedules/calendars";

		// Insight
		public static final String INSIGHTS = "/insights";
		public static final String INSIGHTS_DETAIL = "/insights/{idx}";

	}

	private final SaasTrackerService service;

	@ApiOperation(value = "잘못된 정보 제보")
	@PostMapping(URI.REPORTS)
	public ResponseEntity saveSaasTrackerReports(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody @Valid SaasTrackerDto.SaasTrackerReportsReq dto) {

		if (log.isDebugEnabled()) {
			log.debug("([saveSaasTrackerReports]) $user='{}', $dto='{}'", user, dto.toString());
		}

		return service.saveSaasTrackerReports(user.idx(), dto);
	}


	@ApiOperation("월별 총 SaaS 결제 금액 조회")
	@GetMapping(URI.USAGE_SUMS)
	public ResponseEntity getUsageSums(@ApiIgnore @CurrentUser CustomUser user,
									   @RequestParam(required = true) String fromDt,
									   @RequestParam(required = true) String toDt) {
		if (log.isInfoEnabled()) {
			log.info("([ getUsageSums ])");
		}
		return service.getUsageSums(user.idx(), fromDt, toDt);
	}

	@ApiOperation("해당 월 SaaS 결제 금액 조회")
	@GetMapping(URI.USAGE_SUMS_DETAILS)
	public ResponseEntity getUsageSumsDetails(@ApiIgnore @CurrentUser CustomUser user,
									   @RequestParam(required = true) String searchDt) {
		if (log.isInfoEnabled()) {
			log.info("([ getUsageSumsDetails ])");
		}
		return service.getUsageSumsDetails(user.idx(), searchDt);
	}

	@ApiOperation("해당 월 SaaS 결제 금액 조회(List)")
	@GetMapping(URI.USAGE)
	public ResponseEntity getUsage(@ApiIgnore @CurrentUser CustomUser user,
									@RequestParam(required = true) String searchDt) {

		if (log.isInfoEnabled()) {
			log.info("([ getUsage ])");
		}
		return service.getUsage(user.idx(), searchDt);
	}

	@ApiOperation("월별 총 SaaS 카테고리 결제 비율 조회")
	@GetMapping(URI.USAGE_CATEGORIES)
	public ResponseEntity getUsageCategories(@ApiIgnore @CurrentUser CustomUser user,
									         @RequestParam(required = true) String fromDt,
									         @RequestParam(required = true) String toDt) {
		if (log.isInfoEnabled()) {
			log.info("([ getUsageCategories ])");
		}
		return service.getUsageCategories(user.idx(), fromDt, toDt);
	}

	@ApiOperation("월별 총 SaaS 카테고리 결제 비율 조회")
	@GetMapping(URI.USAGE_CATEGORIES_DETAILS)
	public ResponseEntity getUsageCategoriesDetails(@ApiIgnore @CurrentUser CustomUser user,
											        @RequestParam(required = true) String fromDt,
											        @RequestParam(required = true) String toDt,
													@RequestParam(required = true) Long idxSaasCategoty) {
		if (log.isInfoEnabled()) {
			log.info("([ getUsageCategoriesDetails ])");
		}
		return service.getUsageCategoriesDetails(user.idx(), idxSaasCategoty, fromDt, toDt);
	}

	@ApiOperation("카테고리 별 사용 SaaS 항목 조회")
	@GetMapping(URI.CATEGORIES)
	public ResponseEntity getUseSaasByCategory(@ApiIgnore @CurrentUser CustomUser user) {
		if (log.isInfoEnabled()) {
			log.info("([ getUseSaasByCategory ])");
		}
		return service.getUseSaasByCategory(user.idx());
	}

	@ApiOperation("구독 중/구독 만료 SaaS 항목 조회")
	@GetMapping(URI.SUBSCRIPTIONS)
	public ResponseEntity getUseSaasList(@ApiIgnore @CurrentUser CustomUser user) {
		if (log.isInfoEnabled()) {
			log.info("([ getUseSaasList ])");
		}
		return service.getUseSaasList(user.idx());
	}

	@ApiOperation(value = "이용중인 SaaS 정보 수정")
	@PutMapping(URI.INFO_DETAILS)
	public ResponseEntity updateSaasInfo(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable Long idx,
			@RequestBody @Valid SaasTrackerDto.UpdateSaasInfoReq dto) {

		if (log.isInfoEnabled()) {
			log.info("([ updateSaasInfo ]) $user='{}' $idx='{}' $dto='{}'", user, idx, dto);
		}
		return service.updateSaasInfo(user.idx(), idx, dto);
	}

	@ApiOperation("결제 예정 목록 조회 - 캘린더")
	@GetMapping(URI.SCHEDULES_CALENDAR)
	public ResponseEntity getSaasPaymentSchedulesAtCalendar(@ApiIgnore @CurrentUser CustomUser user) {
		if (log.isInfoEnabled()) {
			log.info("([ getSaasPaymentSchedulesAtCalendar ])");
		}
		return service.getSaasPaymentSchedulesAtCalendar(user.idx());
	}

	@ApiOperation("결제 예정 목록 조회")
	@GetMapping(URI.SCHEDULES)
	public ResponseEntity getSaasPaymentSchedules(@ApiIgnore @CurrentUser CustomUser user) {
		if (log.isInfoEnabled()) {
			log.info("([ getSaasPaymentSchedules ])");
		}
		return service.getSaasPaymentSchedules(user.idx());
	}

	@ApiOperation(value = "이용중인 SaaS 정보 상세조회")
	@GetMapping(URI.INFO_DETAILS)
	public ResponseEntity getSaasPaymentDetailInfo(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable Long idx) {

		if (log.isInfoEnabled()) {
			log.info("([ getSaasPaymentDetailInfo ]) $user='{}' $idx='{}' $dto='{}'", user, idx);
		}
		return service.getSaasPaymentDetailInfo(user.idx(), idx);
	}

	@ApiOperation("Insight 조회")
	@GetMapping(URI.INSIGHTS)
	public ResponseEntity getInsights(@ApiIgnore @CurrentUser CustomUser user) {
		if (log.isInfoEnabled()) {
			log.info("([ getInsights ])");
		}
		return service.getInsights(user.idx());
	}

	@ApiOperation(value = "Insight 수정(중복결제 의심 취소)")
	@PutMapping(URI.INSIGHTS_DETAIL)
	public ResponseEntity updateSaasInsightAtDuplicatePayment(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable Long idx) {

		if (log.isInfoEnabled()) {
			log.info("([ updateSaasInsightAtDuplicatePayment ]) $user='{}' $idx='{}'", user, idx);
		}
		return service.updateSaasInsightAtDuplicatePayment(user.idx(), idx);
	}
}