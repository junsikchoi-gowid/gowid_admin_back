package com.nomadconnection.dapp.api.v2.controller.admin;

import com.nomadconnection.dapp.api.v2.dto.DashBoardDto;
import com.nomadconnection.dapp.api.v2.service.admin.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController("AdminV2Controller")
@RequiredArgsConstructor
@RequestMapping(AdminStatisticsController.URI.BASE)
@CrossOrigin(allowCredentials = "true")
@Api(tags = "어드민 V2")
public class AdminStatisticsController {

	public static class URI {
		public static final String BASE = "/admin/v2";
		public static final String DASHBOARD_CARD = "/dashboard-card";
		public static final String DASHBOARD_MONTH = "/dashboard-month";
		public static final String DASHBOARD_WEEK = "/dashboard-week";
	}

	private final DashBoardService dashBoardService;

	@PreAuthorize("hasAnyAuthority('GOWID_ADMIN')")
	@ApiOperation(value = "대시보드 - 카드사별 6개월 정보")
	@GetMapping(URI.DASHBOARD_CARD)
	public ResponseEntity<?> findDashBoardCard() {

		List<DashBoardDto.Card> data = dashBoardService.getDashBoardCard();

		return new ResponseEntity<>(data, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('GOWID_ADMIN')")
	@ApiOperation(value = "대시보드 - 월별 6개월 정보")
	@GetMapping(URI.DASHBOARD_MONTH)
	public ResponseEntity<?> findDashBoardMonth() {

		List<DashBoardDto.Month> data = dashBoardService.getDashBoardMonth();

		return new ResponseEntity<>(data, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('GOWID_ADMIN')")
	@ApiOperation(value = "대시보드 - 주차별 6개월 정보")
	@GetMapping(URI.DASHBOARD_WEEK)
	public ResponseEntity<?> findDashBoardWeek() {

		List<DashBoardDto.Week> data = dashBoardService.getDashBoardWeek();

		return new ResponseEntity<>(data, HttpStatus.OK);
	}
}
