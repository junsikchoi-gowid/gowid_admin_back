package com.nomadconnection.dapp.api.v2.controller.saas;

import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.v2.dto.saas.SaasTrackerCheckListDto;
import com.nomadconnection.dapp.api.v2.service.saas.SaasTrackerCheckListService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SaasCheckListController extends SaasTrackerBaseController {

	public static class URI {
		public static final String CHECK_LIST_BASE = "/check-lists";
		public static final String CHECK_LIST_DETAIL = "/check-lists/{idxSaasCheckInfo}";
		public static final String COUNT = "/counts";
	}

	private final SaasTrackerCheckListService checkListService;

	@GetMapping(value = URI.CHECK_LIST_BASE + URI.COUNT)
	@ApiOperation(value = "항목별 건수 조회")
	public ApiResponse<?> getCheckListCount(@ApiIgnore @CurrentUser CustomUser user){
		if (log.isInfoEnabled()) {
			log.info("([ getCheckListCount ]) $user='{}'", user);
		}
		return ApiResponse.SUCCESS(checkListService.getCheckListCount(user));
	}

	@GetMapping(value = URI.CHECK_LIST_BASE)
	@ApiOperation(value = "항목별 데이터 조회")
	public ApiResponse<?> getCheckListData(@ApiIgnore @CurrentUser CustomUser user){
		if (log.isInfoEnabled()) {
			log.info("([ getCheckListData ]) $user='{}'", user);
		}
		return ApiResponse.SUCCESS(checkListService.getCheckListData(user));
	}

	@PutMapping(value = URI.CHECK_LIST_DETAIL)
	@ApiOperation(value = "체크리스트 확인")
	public ApiResponse<?> updateCheckListData(@ApiIgnore @CurrentUser CustomUser user,
											  @PathVariable Long idxSaasCheckInfo,
											  @RequestBody SaasTrackerCheckListDto.CheckInfoReq req) {
		if (log.isInfoEnabled()) {
			log.info("([ updateCheckListData ]) $user='{}' idxSaasCheckInfo='{}' req='{}'", user, idxSaasCheckInfo, req);
		}
		checkListService.updateSaasCheckInfo(user, idxSaasCheckInfo, req);
		return ApiResponse.OK();
	}
}
