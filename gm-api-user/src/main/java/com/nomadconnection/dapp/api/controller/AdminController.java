package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.AdminDto;
import com.nomadconnection.dapp.api.dto.RiskDto;
import com.nomadconnection.dapp.api.service.AdminService;
import com.nomadconnection.dapp.api.service.AuthService;
import com.nomadconnection.dapp.api.service.RiskService;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.core.annotation.ApiPageable;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.domain.repository.querydsl.AdminCustomRepository;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


@Slf4j
@RestController
@RequestMapping(AdminController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "Admin", description = AdminController.URI.BASE)
@SuppressWarnings({"unused", "deprecation"})
public class AdminController {

	@SuppressWarnings("WeakerAccess")
	public static class URI {
		public static final String BASE = "/admin/v1";
		public static final String RISK = "/risk";			// 리스크
		public static final String CORP = "/corp";			// 법인정보
		public static final String BURNRATE = "/burnrate";	//Burn Rate
		public static final String ERROR = "/error";	//Error History

		public static final String RISK_LIST = "/risk/list"; // 리스트 Page
		public static final String RISK_LEVELCHANGE = "/risk/level-change"; // 등급변경
		public static final String RISK_RECHECK = "/risk/recheck"; // 한도 재계산
		public static final String RISK_STOP = "/risk/stop"; // 긴급중지

		public static final String BURNRATE_LIST = "/burnrate/list";	//Burn Rate 법인별 Page
		public static final String ERROR_LIST = "/error/list";	//Burn Rate 법인별 Page
	}

	private final Boolean boolDebug = true;
	private final AdminService service;
	private final AuthService serviceAuth;
	private final UserService serviceUser;

	@ApiOperation(value = "리스크"
			, notes = "" + "\n"
			+ "법인별 카드리스크" + "\n"
	)
	@GetMapping( URI.RISK )
	@ApiPageable
	public ResponseEntity riskList(@ModelAttribute AdminCustomRepository.SearchRiskDto riskDto, @ApiIgnore @CurrentUser CustomUser user, @PageableDefault Pageable pageable) {
		if (log.isDebugEnabled()) {
			log.debug("([ getAuthInfo ]) $user='{}'", user);		}

		return service.riskList(riskDto, user.idx(), pageable);
	}

	@ApiOperation(value = "리스크"
			, notes = "" + "\n"
			+ "법인별 카드리스크" + "\n"
	)
	@GetMapping( URI.RISK + 1 )
	@ApiPageable
	public ResponseEntity genVid(@RequestParam String idNum,
								 @RequestParam byte[] idRandumNum,
								 @RequestParam String digestName) throws Exception{
		byte[] result = null;

		DERSequence hashContent = new DERSequence (
				new ASN1Encodable[] {
						new DERPrintableString(idNum),
						new DERBitString(idRandumNum)
				});

		byte[] content = hashContent.getDEREncoded();

		result = CryptoUtil.getMessageDigest(content, digestName);
		result = CryptoUtil.getMessageDigest(result, digestName);

		return result;
	}

}
