package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.AccountDto;
import com.nomadconnection.dapp.api.dto.UserDto;
import com.nomadconnection.dapp.api.security.CustomUser;
import com.nomadconnection.dapp.api.service.AuthService;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.jwt.dto.TokenDto;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController
@RequestMapping(UserController.URI.BASE)
@RequiredArgsConstructor
@Api(tags = "사용자", description = UserController.URI.BASE)
@SuppressWarnings({"unused", "deprecation"})
public class UserController {

	@SuppressWarnings("WeakerAccess")
	public static class URI {
		public static final String BASE = "/user/v1";
		public static final String REGISTER = "/register";
		public static final String REGISTRATION_USER = "/registration/user";
		public static final String REGISTRATION_CORP = "/registration/corp";
		public static final String REGISTRATION_REGISTRATION  = "/registration/{registration}";;
		public static final String REGISTRATION_REGISTRATION_PW = "/registrationpw/{registration}";
		public static final String MEMBERS = "/members";
		public static final String MEMBERS_MEMBER_DEPT = "/members/{member}/dept";
		public static final String INFO = "/info";
	}

	private final UserService service;
	private final AuthService serviceAuth;

	//==================================================================================================================
	//
	//	사용자 등록(회원가입): 선택약관 수신동의 여부, 이메일, 비밀번호, 이름, 연락처
	//
	//==================================================================================================================

	//
	//	사용자 등록(회원가입)
	//	- 가입요청
	//	- 인증메일발송
	//	- 이메일인증
	//

	@ApiOperation(value = "회원가입 - 법인관리자 등록", position = 3, notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - <mark>액세스토큰 불필요</mark>" +
//			"\n - <mark>확인필요</mark>" +
//			"\n   - 회원가입 -> 법인관리자 등록" +
//			"\n   - 멤버초대 -> 카드사용자 등록" +
//			"\n   - 회원가입진행 후 법인정보 등록에서 멈춘 사용자 정보 vs 법인관리자에 의해 다시 멤버초대된 사용자 정보" +
//			"\n - <mark>개발서버에서는 인증메일 발송없이 바로 가입완료 처리됨</mark>" +
			"\n" +
			"\n ### Errors" +
			"\n" +
			"\n - <s>401 UNAUTHORIZED: 권한없음(패스워드 불일치)</s>" +
			"\n 	- <pre>{ \"error\": \"UNAUTHORIZED\", ... }</pre>" +
			"\n" +
			"\n - 404 NOT FOUND: 등록되지 않은 이메일" +
			"\n 	- <pre>{ \"error\": \"USER_NOT_FOUND\", ... }</pre>" +
			"\n" +
			"\n")
	@PostMapping(URI.REGISTER)
	public TokenDto.TokenSet registerUser(
			@RequestBody UserDto.UserRegister dto
	) {
		service.registerUser(dto);
		return serviceAuth.issueTokenSet(AccountDto.builder()
				.email(dto.getEmail())
				.password(dto.getPassword())
				.build());
	}

	//==================================================================================================================
	//
	//	멤버 초대
	//
	//==================================================================================================================

	@ApiOperation(value = "멤버초대 - 카드사용자 등록", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - <mark>사용자 테이블에 넣어야 할지, 별도 테이블에 넣어야할지 모호해서 일단 호출만 가능(무조건 200OK)한 상태로 되어 있음</mark>" +
			"\n - 카드멤버는 패스워드 설정이 없는건지.. " +
			"\n - 인증관련해서는 PIN 인증만 보이는데, 만료되지않는 토큰을 발급하고 PIN 인증시 해당 토큰을 사용하는 방식인가.." +
			"\n")
	@PostMapping(URI.MEMBERS)
	public ResponseEntity<?> postMembers(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody UserDto.MemberRegister dto
	) {
		if (log.isDebugEnabled()) {
			log.debug("([ postMembers ]) $user='{}', $dto='{}'", user, dto);
		}
		service.inviteMember(user.idx(), dto);
		return ResponseEntity.ok().build();
	}

	//==================================================================================================================
	//
	//	정보 조회
	//
	//==================================================================================================================

	@ApiOperation(value = "정보조회(요청하는 사용자의 기본정보를 반환)", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - " +
			"\n" +
			"\n")
	@GetMapping(URI.INFO)
	public UserDto getUserInfo(
			@ApiIgnore @CurrentUser CustomUser user
	) {
		if (log.isDebugEnabled()) {
			log.debug("([ getUserInfo ]) $user='{}'", user);
		}
		return service.getUserInfo(user.idx());
	}

	//==================================================================================================================
	//
	//	부서정보 설정(변경)
	//
	//==================================================================================================================

	@ApiOperation(value = "부서정보 설정(변경)", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - " +
			"\n")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "member", value = "식별자(멤버)", dataType = "Long"),
			@ApiImplicitParam(name = "dept", value = "식별자(부서)", dataType = "Long")
	})
	@PutMapping(URI.MEMBERS_MEMBER_DEPT)
	public ResponseEntity<?> putUserDept(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable Long member,
			@RequestParam Long dept
	) {
		if (log.isDebugEnabled()) {
			log.debug("([ putUserDept ]) $user='{}', $member.idx='{}', $dept.idx='{}'", user, member, dept);
		}
		service.updateDept(user.idx(), member, dept);
		return ResponseEntity.ok().build();
	}

	//==================================================================================================================
	//
	//	부서정보 제거
	//
	//==================================================================================================================

	@ApiOperation(value = "부서정보 제거", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - " +
			"\n")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "member", value = "식별자(멤버)", dataType = "Long"),
	})
	@DeleteMapping(URI.MEMBERS_MEMBER_DEPT)
	public ResponseEntity<?> deleteUserDept(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable Long member
	) {
		if (log.isDebugEnabled()) {
			log.debug("([ deleteUserDept ]) $user='{}', $member.idx='{}'", user, member);
		}
		service.removeDept(user.idx(), member);
		return ResponseEntity.ok().build();
	}


	@ApiOperation(
			value = "Brand 회원가입 유저정보",
			notes = "### Remarks \n - <mark>액세스토큰 불필요</mark>",
			tags = "1. 브랜드"
	)
	@ApiResponses(value={
			@ApiResponse(code = 200, message = "정상"),
			@ApiResponse(code = 201, message = "생성"),
			@ApiResponse(code = 401, message = "권한없음(패스워드 불일치)"),
			@ApiResponse(code = 403, message = "권한없음(패스워드 불일치)"),
			@ApiResponse(code = 404, message = "등록되지 않은 이메일"),
			@ApiResponse(code = 500, message = "")
	})
	@PostMapping(URI.REGISTRATION_USER)
	public ResponseEntity<?> registerBrandUser(
			@RequestBody UserDto.RegisterBrandUser dto) {
		log.debug("registerBrandUser controller ");

		return service.registerBrandUser(dto);
	}


	@ApiOperation(
			value = "Brand 회원가입 법인정보",
			notes = "### Remarks \n",
			tags = "1. 브랜드"
	)
	@PostMapping(path = URI.REGISTRATION_CORP, consumes = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity registerBrandCorp(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody UserDto.RegisterBrandCorp dto
	) {
		log.debug("registerUserCorp controller ");

		return service.registerBrandCorp(user.idx(), dto);
	}

	@ApiOperation(
			value = "Brand 내 정보 수정",
			notes = "### Remarks ",
			tags = "1. 브랜드"
	)
	@ApiResponses(value={
			@ApiResponse(code = 200, message = "정상"),
			@ApiResponse(code = 201, message = "생성"),
			@ApiResponse(code = 401, message = "권한없음(패스워드 불일치)"),
			@ApiResponse(code = 403, message = "권한없음(패스워드 불일치)"),
			@ApiResponse(code = 404, message = "등록되지 않은 이메일"),
			@ApiResponse(code = 500, message = "")
	})
	@PostMapping(URI.REGISTRATION_REGISTRATION)
	public ResponseEntity registerUserUpdate(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody UserDto.registerUserUpdate dto,
			@PathVariable Long member
	) {
		if (log.isDebugEnabled()) {
			log.debug("([ registerUserUpdate ]) $member.idx='{}'", member , user);
		}

		return service.registerUserUpdate(dto, member, user.idx());
	}

	@ApiOperation(
			value = "Brand 비밀번호 수정",
			notes = "### Remarks ",
			tags = "1. 브랜드"
	)
	@ApiResponses(value={
			@ApiResponse(code = 200, message = "정상"),
			@ApiResponse(code = 201, message = "생성"),
			@ApiResponse(code = 401, message = "권한없음(패스워드 불일치)"),
			@ApiResponse(code = 403, message = "권한없음(패스워드 불일치)"),
			@ApiResponse(code = 404, message = "등록되지 않은 이메일"),
			@ApiResponse(code = 500, message = "")
	})
	@PostMapping(URI.REGISTRATION_REGISTRATION_PW)
	public ResponseEntity registerUserPasswordUpdate(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody UserDto.registerUserPasswordUpdate dto,
			@PathVariable Long member
	) {
		if (log.isDebugEnabled()) {
			log.debug("([ registerUserUpdate ]) $member.idx='{}'", member , user);
		}

		return service.registerUserPasswordUpdate(dto, member, user.idx());
	}




}
