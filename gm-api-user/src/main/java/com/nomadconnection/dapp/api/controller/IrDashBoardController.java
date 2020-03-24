package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.IrDashBoardDto;
import com.nomadconnection.dapp.api.service.IrDashBoardService;
import com.nomadconnection.dapp.core.annotation.ApiPageable;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


@Slf4j
@RestController
@RequestMapping(IrDashBoardController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "Ir 게시판", description = IrDashBoardController.URI.BASE)
@SuppressWarnings({"unused", "deprecation"})
public class IrDashBoardController {

	@SuppressWarnings("WeakerAccess")
	public static class URI {
		public static final String BASE = "/IrDashBoard/v1";
		public static final String IRDASHBOARD = "/IrDashBoard";			// 리스크
	}

	private final Boolean boolDebug = true;
	private final IrDashBoardService service;

	@ApiOperation(value = "리스트", notes = "" + "\n")
	@GetMapping( URI.IRDASHBOARD )
	public Page<IrDashBoardDto> getList(@ApiIgnore @CurrentUser CustomUser user, @ModelAttribute IrDashBoardDto irDashBoard,
						@PageableDefault Pageable page) {
		return service.getList(page, irDashBoard, user.idx());
	}

	@ApiOperation(value = "리스트", notes = "" + "\n")
	@PostMapping( URI.IRDASHBOARD )
	public ResponseEntity saveList(@RequestParam Long idxUser ,
								   @ModelAttribute IrDashBoardDto irDashBoard) {
		return service.saveList(irDashBoard, idxUser);
	}
}
