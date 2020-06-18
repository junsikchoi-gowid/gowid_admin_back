package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.IrDashBoardDto;
import com.nomadconnection.dapp.api.service.IrDashBoardService;
import com.nomadconnection.dapp.core.annotation.ApiPageable;
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


@Slf4j
@CrossOrigin
@RestController
@RequestMapping(IrDashBoardController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "Ir 게시판", description = IrDashBoardController.URI.BASE)
public class IrDashBoardController {

	public static class URI {
		public static final String BASE = "/IrDashBoard/v1";
		public static final String IRDASHBOARD = "/IrDashBoard";			// 리스크
		public static final String IRDASHBOARD_SAVE = "/IrDashBoard/save";			// 리스크
	}

	private final Boolean boolDebug = true;
	private final IrDashBoardService service;

	@ApiOperation(value = "리스트", notes = " sortBy = asc, desc " + "\n")
	@GetMapping( URI.IRDASHBOARD )
	@ApiPageable
	public Page<IrDashBoardDto> getList(@PageableDefault Pageable pageable) {
		return service.getList(pageable);
	}

	@ApiOperation(value = "리스트", notes = "" + "\n")
	@PostMapping( URI.IRDASHBOARD_SAVE )
	public ResponseEntity saveList(@RequestParam Long idxUser ,
								   @ModelAttribute IrDashBoardDto irDashBoard) {
		return service.saveList(irDashBoard, idxUser);
	}
}
