package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.CorpDto;
import com.nomadconnection.dapp.api.dto.UserDto;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping(UserController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "[02] 사용자", description = UserController.URI.BASE)
public class UserController {

	public static class URI {
		public static final String BASE = "/user/v1";
		public static final String REGISTRATION_USER = "/registration/user"; // 유저등록
		public static final String REGISTRATION_CORP = "/registration/corp"; // 가능 한도 확인, 결제계좌 선택??
		public static final String REGISTRATION_INFO = "/registration/info"; //
		public static final String REGISTRATION_CORP_BRANCH = "/registration/corp-branch";
		public static final String REGISTRATION_CONSENT = "/registration/consent"; // 약관동의, 카드관리자등록
		public static final String LIMIT_REVIEW = "/limit-review";
		public static final String EXTERNAL_ID = "/external-id";
		public static final String DELETE_ACCOUNT = "/delete-account";
		public static final String INIT_USER_INFO = "/init/user";
		public static final String ENABLE = "/enable";
		public static final String INFO = "/info";
		public static final String EVENTS = "/events";
		public static final String MEMBERS = "/members";
	}

	private final UserService service;

	//==================================================================================================================
	//
	//	사용자 등록(회원가입): 선택약관 수신동의 여부, 이메일, 비밀번호, 이름, 연락처
	//
	//==================================================================================================================

    @ApiOperation(value = "사용자정보 초기화")
    @DeleteMapping(URI.INIT_USER_INFO)
    public ResponseEntity<?> initUserInfo(
            @ApiIgnore @CurrentUser CustomUser user
    ) {
        if (log.isInfoEnabled()) {
            log.info("([ initUserInfo ]) $user='{}'", user);
        }
        service.initUserInfo(user.idx());
        return ResponseEntity.ok().build();
    }


	//==================================================================================================================
	//
	//	정보 조회
	//
	//==================================================================================================================

	@ApiOperation(value = "정보조회(요청하는 사용자의 기본정보를 반환)")
	@GetMapping(URI.INFO)
	public UserDto getUserInfo(
			@ApiIgnore @CurrentUser CustomUser user
	) {
		if (log.isInfoEnabled()) {
			log.info("([ getUserInfo ]) $user='{}'", user);
		}
		return service.getUserInfo(user.idx());
	}

	@ApiOperation(value ="내부 API 멤버 추가")
	@PostMapping(URI.MEMBERS)
	public UserDto addMember(
			@ApiIgnore @CurrentUser CustomUser user, @RequestBody UserDto.MemberRegister memberInfo
	) {
		if (log.isInfoEnabled()) {
			log.info("([ addMember ]) $memberInfo='{}'", memberInfo);
		}

    	return service.addMember(user.idx(), memberInfo);
	}

	@ApiOperation(value ="내부 API 멤버 정보 수정")
	@PatchMapping(URI.MEMBERS + "/{email}")
	public ResponseEntity<?> modifyMember(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable String email,
			@RequestBody UserDto.MemberRegister memberInfo
			) {
		if (log.isInfoEnabled()) {
			log.info("([ ModifyManager ]) $admin = {} $email = {} memberInfo='{}'", user.idx(), email, memberInfo.toString());
		}

		service.changeMemberInfo(user.idx(), email, memberInfo);
		return ResponseEntity.ok().build();
	}

	@ApiOperation(value ="내부 API 멤버 삭제")
	@DeleteMapping(URI.MEMBERS + "/{email}")
	public ResponseEntity<?> removeMember(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable String email
	) {
		if (log.isInfoEnabled()) {
			log.info("([ RemoveAccount ]) $admin = {} $email='{}'", user.idx(), email);
		}

		service.removeMember(user.idx(), email);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping(URI.MEMBERS + "/{email}/password")
	public ResponseEntity<?> initializeMemberPassword(
			@PathVariable String email
	) {
		if (log.isInfoEnabled()) {
			log.info("([ InitPassword ]) $email='{}'", email);
		}

		service.initializePassword(email);
		return ResponseEntity.ok().build();
	}

	@ApiOperation(
			value = "Brand 회원가입 유저정보",
			notes = "### Remarks \n - <mark>액세스토큰 불필요</mark>"
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
		if (log.isInfoEnabled()) {
			log.info("([ registerBrandUser ]) $dto='{}'", dto);
		}

		return service.registerBrandUser(dto);
	}

	@Deprecated
	@ApiOperation(value = "Brand 회원가입 법인정보")
	@PostMapping(path = URI.REGISTRATION_CORP, consumes = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> registerBrandCorp(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody UserDto.RegisterBrandCorp dto
	) {
		if (log.isInfoEnabled()) {
			log.info("([ registerBrandCorp ]) $user='{}' $dto='{}'", user, dto);
		}

		return service.registerBrandCorp(user.idx(), dto);
	}

	@ApiOperation(value = "Brand 회원가입 법인정보")
	@GetMapping(path = URI.REGISTRATION_CORP)
	public ResponseEntity<?> getBrandCorp(
			@ApiIgnore @CurrentUser CustomUser user
	) {
		if (log.isInfoEnabled()) {
			log.info("([ getBrandCorp ]) $user='{}'", user);
		}

		if(user == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
 
		return service.getBrandCorp(user.idx()); 
	}

	@Secured({"ROLE_MASTER","ROLE_VIEWER", "ROLE_EXPENSE_MANAGER"})
	@ApiOperation(value = "Brand 회원가입 법인정보 추가정보")
	@GetMapping(path = URI.REGISTRATION_CORP_BRANCH)
	public ResponseEntity<CorpDto.CorpInfoDto> getBrandCorpBranch(@ApiIgnore @CurrentUser CustomUser user){

		CorpDto.CorpInfoDto data = service.getBrandCorpBranch(user.idx());

		return ResponseEntity.ok().body(data);
	}

	@ApiOperation(value = "Brand 내 정보 수정")
	@ApiResponses(value={
			@ApiResponse(code = 200, message = "정상"),
			@ApiResponse(code = 201, message = "생성"),
			@ApiResponse(code = 401, message = "권한없음(패스워드 불일치)"),
			@ApiResponse(code = 403, message = "권한없음(패스워드 불일치)"),
			@ApiResponse(code = 404, message = "등록되지 않은 이메일"),
			@ApiResponse(code = 500, message = "")
	})
	@PostMapping(URI.REGISTRATION_INFO)
	public ResponseEntity<?> registerUserUpdate(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody UserDto.registerUserUpdate dto
	) {
		if (log.isInfoEnabled()) {
			log.info("([ registerUserUpdate ]) $user='{}' $dto='{}'", user, dto);
		}

		return service.registerUserUpdate(dto, user.idx());
	}

	@ApiOperation(value = "사용자별 이용약관 등록")
	@PostMapping(URI.REGISTRATION_CONSENT)
	public ResponseEntity<?> registerUserConsent(
			@RequestParam Long idxUser,
			@RequestBody UserDto.RegisterUserConsent dto
	) {
		if (log.isInfoEnabled()) {
			log.info("([ registerUserConsent ]) $idxUser='{}' $dto='{}'", idxUser, dto);
		}

		return service.registerUserConsent(dto, idxUser);
	}

	@ApiOperation(value = "한도 재심사 요청")
	@PostMapping(URI.LIMIT_REVIEW)
	public ResponseEntity<?> limitReview(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody UserDto.LimitReview dto
	) {
		if (log.isInfoEnabled()) {
			log.info("([ limitReview ]) $user='{}' $dto='{}'", user, dto);
		}

		return service.limitReview(user.idx(), dto);
	}

	@ApiOperation(value = "외부 아이디 조회")
	@GetMapping(URI.EXTERNAL_ID)
	public com.nomadconnection.dapp.api.dto.gateway.ApiResponse<UserDto.ExternalIdRes> externalId(
			@ApiIgnore @CurrentUser CustomUser customUser) {

		return com.nomadconnection.dapp.api.dto.gateway.ApiResponse
				.SUCCESS(service.getUserExternalId(customUser));
	}

	@ApiOperation(value = "회원탈퇴 요청")
	@PostMapping(URI.DELETE_ACCOUNT)
	public ResponseEntity<?> requestDeleteAccount(
		@ApiIgnore @CurrentUser CustomUser user,
		@RequestBody UserDto.DeleteUserAccount dto
		) {
		if (log.isInfoEnabled()) {
			log.info("([ requestDeleteAccount ]) $user='{}'", user);
		}
		service.sendEmailDeleteAccount(user.email(), dto.getPassword(), dto.getReason());
		service.deleteUserByEmail(user.email());
		return ResponseEntity.ok().build();
	}

	@ApiOperation(value = "회원활성")
	@PostMapping(URI.ENABLE)
	public ResponseEntity<?> enableAccount(
			@RequestParam Long idxUser
	) {
		if (log.isInfoEnabled()) {
			log.info("([ enableAccount ]) $user='{}'", idxUser);
		}
		service.enableAccount(idxUser);
		return ResponseEntity.ok().build();
	}

	@ApiOperation(value = "Event 정보")
	@GetMapping(URI.EVENTS)
	public ResponseEntity<?> events(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam String eventName
	) {
		if (log.isInfoEnabled()) {
			log.info("([ events ]) $user='{}', $eventName='{}'", user.idx(), eventName);
		}

		UserDto.EventsInfo events = service.getEvents(user, eventName);

		if(events == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(events,HttpStatus.OK);
	}
}
