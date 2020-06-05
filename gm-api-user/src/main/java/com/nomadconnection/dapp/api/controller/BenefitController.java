package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.BenefitDto;
import com.nomadconnection.dapp.api.service.BenefitService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(UserCorporationController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "멤버십 혜택", description = BenefitController.URI.BASE)
public class BenefitController {

	public static class URI {
		public static final String BASE = "/benefit/v1";
		public static final String BENEFITS = "/benefits";
		public static final String BENEFIT = "/benefits/{idxBenefit}";
		public static final String APPLICATION = "/application";
	}

	private final BenefitService service;

	@ApiOperation("베네핏 목록 조회")
	@GetMapping(URI.BENEFITS)
	public ResponseEntity<List<BenefitDto.BenefitRes>> getBenefits(
			@ApiIgnore @CurrentUser CustomUser user) {
		if (log.isInfoEnabled()) {
			log.info("([ getBenefits ]) $user='{}'", user);
		}

		return ResponseEntity.ok().body(service.getBenefits());
	}

	@ApiOperation("베네핏 상세 조회")
	@GetMapping(URI.BENEFIT)
	public ResponseEntity<BenefitDto.BenefitRes> getBenefit(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable Long idxBenefit) {
		if (log.isInfoEnabled()) {
			log.info("([ getBenefit ]) $user='{}', $idx_benefit='{}'", user, idxBenefit);
		}

		return ResponseEntity.ok().body(service.getBenefit(idxBenefit));
	}
}
