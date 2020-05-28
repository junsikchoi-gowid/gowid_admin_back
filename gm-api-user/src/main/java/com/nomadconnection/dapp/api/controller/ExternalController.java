package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.ExternalDto;
import com.nomadconnection.dapp.api.dto.RiskDto;
import com.nomadconnection.dapp.api.service.AuthService;
import com.nomadconnection.dapp.api.service.ExternalService;
import com.nomadconnection.dapp.api.service.RiskService;
import com.nomadconnection.dapp.api.service.UserService;
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
@RestController
@RequestMapping(ExternalController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "외부연결", description = ExternalController.URI.BASE)
@SuppressWarnings({"unused", "deprecation"})
public class ExternalController {

	@SuppressWarnings("WeakerAccess")
	public static class URI {
		public static final String BASE = "/external/v1";
		public static final String DATA = "/data";			// 리스크
	}

	private final Boolean boolDebug = true;
	private final ExternalService service;

	@ApiOperation(value = "리스크 설정 저장", notes = "" + "\n")
	@GetMapping( URI.DATA )
	public Page getData(@PageableDefault Pageable page, @ModelAttribute ExternalDto externalDto) {
		return service.getData(page, externalDto);
	}
}