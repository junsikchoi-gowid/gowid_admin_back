package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.service.CorpService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping(CorpController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "법인정보", description = CorpController.URI.BASE)
public class CorpController {

	public static class URI {
		public static final String BASE = "/corp/v1";
		public static final String REGISTER_CORP = "/register/corp";
		public static final String REGISTER_CORP_ID = "/register/corp/id";
	}

	private final CorpService service;

	//==================================================================================================================
	//
	//	홈택스 인증서 등록
	//
	//==================================================================================================================
//
//	@ApiOperation(value = "홈택스 인증서 등록  ", notes = "" )
//	@ApiImplicitParams({
//			@ApiImplicitParam(name = "pfx", value = "pfx파일"),
//			@ApiImplicitParam(name = "pw", value = "패스워드")
//	})
//	@PostMapping(URI.REGISTER_CORP)
//	public ResponseEntity<?> getCorpRegistrable(@ApiIgnore @CurrentUser CustomUser user
//												,@ModelAttribute
//	) {
//		return ResponseEntity.ok().build();
//	}

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
