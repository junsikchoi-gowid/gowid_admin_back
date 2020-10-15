package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.api.service.AuthService;
import com.nomadconnection.dapp.api.service.CodefService;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.codef.io.dto.Common;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
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


@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping(CodefController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "CodeF 스크래핑", description = CodefController.URI.BASE)
public class CodefController {

	public static class URI {
		public static final String BASE = "/codef/v1";

		public static final String ACCOUNT_CONNECTED_LIST = "/account/connectedId-list";       				// 커넥티드아이디 목록 조회
		public static final String ACCOUNT_LIST= "/account/list";            								// 인증서 목록 조회
		public static final String ACCOUNT_LIST_CORP= "/account/listcorp";            						// 인증서 목록 조회 (법인)
		public static final String ACCOUNT_CREATE = "/account/create";            							//
		// 인증서 등록(커넥티드아이디 발급)
		public static final String ACCOUNT_ADD = "/account/add";            								// 인증서 추가
		public static final String REFERENCE_ADD_ACCOUNT = "/account/reference-add";  						// 인증서 추가 레퍼런스 추가

		public static final String ACCOUNT_CREATE_NT = "/account/create/nt";         						// 인증서 추가 국세청 추가로 인한 수정
		public static final String ACCOUNT_UPDATE = "/account/update";            							// 인증서 수정
		public static final String ACCOUNT_DELETE = "/account/delete";            							// 인증서 삭제
		public static final String ACCOUNT_REGISTER_CORP = "/account/register/corp";         				// 법인 정보 등록 및 등기부등본 스크래핑
		public static final String ACCOUNT_REGISTER_CORP_MANUAL = "/account/register/corp/manual";         				// 등기부등본 스크래핑


		public static final String SCRAPING ="/scraping"; 													// 스크래핑

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

	@ApiOperation(value = "인증서 목록 조회 (법인) ", notes = "" +
			"\n ### Remarks" +
			"\n")
	@GetMapping( URI.ACCOUNT_LIST_CORP )
	public ResponseEntity ConnectedIdList(
			@ApiIgnore @CurrentUser CustomUser user ,@RequestParam(required = false) Long idxCorp) {
		return service.findConnectedIdListCorp(user.idx(), idxCorp );
	}




	@ApiOperation(value = "인증서 등록(커넥티드아이디 발급)", notes = "" +
			"\n ### Remarks" +
			"\n")
	@PostMapping(URI.ACCOUNT_CREATE)
	public ResponseEntity RegisterAccount(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody ConnectedMngDto.Account dto) {
		if (log.isInfoEnabled()) {
			log.info("([ Codef RegisterAccount ]) $user='{}' $dto='{}'", user, dto);
		}
		return service.registerAccount(dto, user.idx());
	}

	@ApiOperation(value = "인증서 등록(커넥티드아이디 추가등록)", notes = "" +
			"\n ### Remarks" +
			"\n")
	@PostMapping(URI.ACCOUNT_ADD)
	public ResponseEntity registerAccountAdd(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody Common.Account dto) {
		if (log.isInfoEnabled()) {
			log.info("([ Codef registerAccountAdd ]) $user='{}' $dto='{}'", user, dto);
		}
		return service.registerAccountAddCreate(dto, user.idx());
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
			@RequestParam String targetOrganization
	) {
		if (log.isInfoEnabled()) {
			log.info("([ Codef registerAccountReferenceAdd ]) $user='{}' $dto='{}'", user, dto);
		}
		return service.registerAccountReferenceAdd(dto, user.idx(), connectedId, sourceOrganization, targetOrganization);
	}



	@ApiOperation(value = "인증서 등록(커넥티드아이디 발급) 국세청 관련 등록 ", notes = " type 을 강제로 nt 로 저장함 " +
			"\n ### Remarks" +
			"\n")
	@PostMapping(URI.ACCOUNT_CREATE_NT)
	public ResponseEntity RegisterAccountNt(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody ConnectedMngDto.AccountNt dto) {
		if (log.isInfoEnabled()) {
			log.info("([ Codef RegisterAccountNt ]) $user='{}' $dto='{}'", user, dto);
		}
		return service.RegisterAccountNt(dto, user.idx());
	}

	@ApiOperation(value = "재무제표 스크래핑 및 이미지 저장 ", notes = "  " +
			"\n ### Remarks" +
			"\n")
	@PostMapping(URI.ACCOUNT_REGISTER_CORP)
	public ResponseEntity RegisterCorpInfo(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody ConnectedMngDto.CorpInfo dto,
			@RequestParam(required = false) Long idxCardInfo){
		if (log.isInfoEnabled()) {
			log.info("([ Codef RegisterCorpInfo ]) $user='{}' $dto='{}'", user, dto);
		}
		return service.RegisterCorpInfo(dto, user.idx(), idxCardInfo);
	}

	@ApiOperation(value = "재무제표 - 수동 저장 및 이미지 전송", notes = "  " +
			"\n ### Remarks" +
			"\n")
	@PostMapping(URI.ACCOUNT_REGISTER_CORP_MANUAL)
	public ResponseEntity RegisterCorpInfo(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody ConnectedMngDto.CorpInfoManual dto ){
		if (log.isInfoEnabled()) {
			log.info("([ Codef RegisterCorpInfo ]) $user='{}' $dto='{}'", user, dto);
		}
		return service.RegisterCorpInfoManual(dto, user.idx() );
	}



	@ApiOperation(value = "인증서 등록(커넥티드아이디 발급) test", notes = "" +
			"\n ### Remarks" +
			"\n")
	@PostMapping(URI.ACCOUNT_CREATE + "2")
	public ResponseEntity RegisterAccount2(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody ConnectedMngDto.Account2 dto) {
		if (log.isInfoEnabled()) {
			log.info("([ Codef RegisterAccount ]) $user='{}' $dto='{}'", user, dto);
		}
		return service.registerAccount2(dto, user.idx());
	}

	/*
	@ApiOperation(value = "인증서 수정", notes = "" +
			"\n ### Remarks" +
			"\n")
	@PostMapping(URI.ACCOUNT_UPDATE)
	public ResponseEntity UpdateAccount(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody ConnectedMngDto.Account dto) {
		if (log.isInfoEnabled()) {
			log.info("([Codef UpdateAccount ]) $dto='{}'", dto);
		}
		return service.updateAccount(dto, user.idx());
	}
*/
	@ApiOperation(value = "인증서 삭제", notes = "" +
			"\n ### Remarks" +
			"\n")
	@PostMapping(URI.ACCOUNT_DELETE)
	public ResponseEntity DeleteAccount(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody ConnectedMngDto.DeleteAccount dto) {
		if (log.isInfoEnabled()) {
			log.info("([ Codef DeleteAccount ]) $user='{}' $dto='{}'", user, dto);
		}
		return service.deleteAccount(dto, user.idx());
	}

	@ApiOperation(value = "인증서 삭제2", notes = "" +
			"\n ### Remarks" +
			"\n")
	@PostMapping(URI.ACCOUNT_DELETE +2 )
	public ResponseEntity DeleteAccount2(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam String connectedId) {
		return service.deleteAccount2(connectedId);
	}

	@ApiOperation(value = "인증서 기관 삭제 ", notes = "" +
			"\n ### Remarks" +
			"\n organization // 기관코드" +
			"\n loginType // 로그인타입 (0: 인증서, 1: ID/PW )" )
	@PostMapping(URI.ACCOUNT_DELETE +3 )
	public ResponseEntity DeleteAccount2(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam String connectedId,
			@RequestParam String organization,
			@RequestParam String businessType,
			@RequestParam String loginType) {
		return service.ProcDeleteConnectedId(connectedId, organization, loginType, businessType);
	}
}
