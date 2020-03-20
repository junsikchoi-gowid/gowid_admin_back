package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.CorpDto;
import com.nomadconnection.dapp.api.exception.AlreadyExistException;
import com.nomadconnection.dapp.core.security.CustomUser;
import com.nomadconnection.dapp.api.service.CorpService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(CorpController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "법인정보", description = CorpController.URI.BASE)
@SuppressWarnings({"unused", "deprecation"})
public class CorpController {

	@SuppressWarnings("WeakerAccess")
	public static class URI {
		public static final String BASE = "/corp/v1";
		public static final String REGISTRABLE = "/registrable";
		public static final String REGISTER = "/register";
		public static final String MEMBERS = "/members";
	}

	private final CorpService service;

	//==================================================================================================================
	//
	//	사업자등록번호 등록가능여부 체크
	//
	//==================================================================================================================
/*

	@ApiOperation(value = "등록가능여부 확인 - 사업자등록번호", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - <mark>인증토큰(액세스) 불필요</mark>" +
			"\n")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "no", value = "사업자등록번호(10 Digits)")
	})
	@GetMapping(URI.REGISTRABLE)
	public ResponseEntity<?> getCorpRegistrable(@RequestParam("no") String bizRegNo) {
		if (service.isPresent(bizRegNo)) {
			throw AlreadyExistException.builder()
					.resource(bizRegNo)
					.build();
		}
		return ResponseEntity.ok().build();
	}
*/

	//==================================================================================================================
	//
	//	법인정보등록: 법인명, 사업자등록번호, 결제계좌, 주주명부, 희망법인총한도
	//
	//==================================================================================================================

//	@ApiOperation(value = "법인정보 등록", notes = "" +
//			"\n ### Remarks" +
//			"\n" +
//			"\n - <mark>multipart/form-data</mark>" +
//			"\n - 결제계좌의 은행코드는 아직 반영되어 있지 않음" +
//			"\n")
//	@PostMapping(path = URI.REGISTER, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//	public CorpDto postCorps(@ApiIgnore @CurrentUser CustomUser user, CorpDto.CorpRegister dto) {
//		if (log.isDebugEnabled()) {
//			log.debug("([ postCorps ]) $user='{}', $dto='{}'", user, dto);
//		}
//		return service.registerCorp(user.idx(), dto);
//	}

	//==================================================================================================================
	//
	//	동일법인에 소속된 모든 멤버 조회
	//
	//==================================================================================================================
/*

	@ApiOperation(value = "동일법인에 소속된 모든 멤버 조회: E-01", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - " +
			"\n")
	@GetMapping(URI.MEMBERS)
	public List<CorpDto.CorpMember> getCorpMembers(@ApiIgnore @CurrentUser CustomUser user) {
		if (log.isDebugEnabled()) {
			log.debug("([ getCorpMembers ]) $user='{}'", user);
		}
		List<CorpDto.CorpMember> members = service.members(user.idx());
		{
			if (log.isDebugEnabled()) {
				log.debug("([ getCorpMembers ]) $user='{}' => {}", user, members);
			}
		}
		return members;
	}
*/

}
