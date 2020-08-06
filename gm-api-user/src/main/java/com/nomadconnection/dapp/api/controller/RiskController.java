package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.RiskDto;
import com.nomadconnection.dapp.api.service.AuthService;
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


@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping(RiskController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "리스크", description = RiskController.URI.BASE)
public class RiskController {

	public static class URI {
		public static final String BASE = "/risk/v1";

		public static final String RISK = "/risk";			// 리스크
		public static final String RISKCORP = "/riskCorp";			// 리스크
		public static final String SAVE45 = "/save45";			// 리스크
		public static final String RISKCONFIG = "/riskconfig";			// 리스크
		public static final String CARD_LIMIT = "/cardLimit";
		public static final String GRANT_LIMIT = "/grantLimit";
	}

	private final RiskService service;

	@ApiOperation(value = "리스크 저장", notes = "" + "\n")
	@GetMapping( URI.RISK )
	public ResponseEntity saveRisk(@ApiIgnore @CurrentUser CustomUser user
			, @RequestParam(required = false) String calcDate) {
		return service.saveRisk(user.idx(), null ,calcDate);
	}

	@ApiOperation(value = "리스크 저장 (idxCorp) ", notes = "" + "\n")
	@GetMapping( URI.RISKCORP )
	public ResponseEntity saveRiskCorp(@ApiIgnore @CurrentUser CustomUser user
			, @RequestParam(required = false) Long idxCorp, @RequestParam(required = false) String calcDate) {
		return service.saveRisk(user.idx(), idxCorp ,calcDate);
	}

	@ApiOperation(value = "리스크 저장 45일 기준 전문테이블 저장", notes = "" + "\n")
	@GetMapping( URI.SAVE45)
	public ResponseEntity saveRisk45(@ApiIgnore @CurrentUser CustomUser user
			, @RequestParam(required = false) String calcDate) {
		return service.saveRisk45(user.idx(), null ,calcDate);
	}

	@ApiOperation(value = "리스크 설정 저장", notes = "" + "\n")
	@GetMapping( URI.RISKCONFIG )
	public ResponseEntity saveRiskConfig(@RequestParam Long idxUser ,
									  @ModelAttribute RiskDto.RiskConfigDto riskConfigDto) {
		return service.saveRiskConfig(riskConfigDto);
	}

	@ApiOperation(value = "리스크 한도 금액 조회")
	@GetMapping(URI.CARD_LIMIT)
	public ResponseEntity<String> getCardLimit(
			@ApiIgnore @CurrentUser CustomUser user) {
		if (log.isInfoEnabled()) {
			log.info("([ getCardLimit ]) $user='{}''", user);
		}

		return ResponseEntity.ok().body(service.getCardLimit(user.idx()));
	}

	@ApiOperation(value = "부여 한도 금액 조회")
	@GetMapping(URI.GRANT_LIMIT)
	public ResponseEntity<String> getGrantLimit(
			@ApiIgnore @CurrentUser CustomUser user) {
		if (log.isInfoEnabled()) {
			log.info("([ getGrantLimit ]) $user='{}''", user);
		}

		return service.getGrantLimit(user.idx());
	}
}
