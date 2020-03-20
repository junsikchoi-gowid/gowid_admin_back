package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.dto.RiskDto;
import com.nomadconnection.dapp.api.service.AuthService;
import com.nomadconnection.dapp.api.service.BankService;
import com.nomadconnection.dapp.api.service.RiskService;
import com.nomadconnection.dapp.api.service.UserService;
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

import java.io.IOException;


@Slf4j
@RestController
@RequestMapping(RiskController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "리스크", description = RiskController.URI.BASE)
@SuppressWarnings({"unused", "deprecation"})
public class RiskController {

	@SuppressWarnings("WeakerAccess")
	public static class URI {
		public static final String BASE = "/risk/v1";

		public static final String RISK = "/risk";			// 리스크
	}

	private final Boolean boolDebug = true;
	private final RiskService service;
	private final AuthService serviceAuth;
	private final UserService serviceUser;

	@ApiOperation(value = "리스크", notes = "" + "\n")
	@GetMapping( URI.RISK )
	public ResponseEntity AccountList(@RequestParam Long idxUser ,
									  @ModelAttribute RiskDto riskDto) {
		return service.saveRisk(riskDto, idxUser);
	}

}
