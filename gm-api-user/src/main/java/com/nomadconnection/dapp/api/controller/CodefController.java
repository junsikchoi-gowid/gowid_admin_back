package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.service.CodefService;
import com.nomadconnection.dapp.codef.io.dto.Common;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.exception.response.GowidResponse;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.List;


@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping(CodefController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "[03] CodeF 스크래핑", description = CodefController.URI.BASE)
public class CodefController {

	public static class URI {
		public static final String BASE = "/codef/v1";

		// 인증서 등록(커넥티드아이디 발급)
		public static final String ACCOUNT_ADD = "/account/add";            								// 인증서 추가
		public static final String ACCOUNT_LIST= "/account/list";            								// 인증서 목록 조회
		public static final String REFERENCE_ADD_ACCOUNT = "/account/reference-add";  						// 인증서 추가 레퍼런스 추가
	}

	private final CodefService service;

	@ApiOperation(value = "인증서 목록 조회", notes = "" +
			"\n ### Remarks" +
			"\n")
	@GetMapping( URI.ACCOUNT_LIST + "test1" )
	public String ConnectedIdList1(
			@ApiIgnore @CurrentUser CustomUser user ) throws InterruptedException, ParseException, IOException {
		return service.connectedIdList();
	}

	@ApiOperation(value = "인증서 목록 조회", notes = "" +
			"\n ### Remarks" +
			"\n")
	@GetMapping( URI.ACCOUNT_LIST + "test2" )
	public String ConnectedIdList2(
			@ApiIgnore @CurrentUser CustomUser user, @RequestParam String connectedId) throws InterruptedException, ParseException, IOException {
		return service.list(connectedId);
	}


	@ApiOperation(value = "인증서 목록 조회", notes = "" +
			"\n ### Remarks" +
			"\n")
	@GetMapping( URI.ACCOUNT_LIST )
	public ResponseEntity ConnectedIdList(
			@ApiIgnore @CurrentUser CustomUser user ) {
		return service.findConnectedIdList(user.idx());
	}

	@ApiOperation(value = "인증서 등록(커넥티드아이디 추가등록)", notes = "" +
			"\n ### Remarks" +
			"\n")
	@PostMapping(URI.ACCOUNT_ADD)
	public GowidResponse<List<BankDto.ResAccountDto>> registerAccountAdd(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody Common.Account dto) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("([ Codef registerAccountAdd ]) $user='{}' $dto='{}'", user, dto);
		}

		return GowidResponse.ok(service.registerAccountAddCreate(dto, user.idx()));
	}

	@ApiOperation(value = "인증서 등록(레퍼런스 추가)", notes = "" +
			"\n ### Remarks" +
			"\n")
	@PostMapping(URI.REFERENCE_ADD_ACCOUNT)
	public ResponseEntity registerAccountReferenceAdd(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody Common.Account dto,
			@RequestParam String connectedId,
			@RequestParam String sourceOrganization,
			@RequestParam String sourceBusiness,
			@RequestParam String targetOrganization,
			@RequestParam String targetBusiness
	) {
		if (log.isInfoEnabled()) {
			log.info("([ Codef registerAccountReferenceAdd ]) $user='{}' $dto='{}'", user, dto);
		}
		return service.registerAccountReferenceAdd(dto, user.idx(), connectedId, sourceOrganization, sourceBusiness, targetOrganization, targetBusiness);
	}
}
