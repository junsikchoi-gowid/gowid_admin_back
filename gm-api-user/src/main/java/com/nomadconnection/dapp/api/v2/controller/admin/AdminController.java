package com.nomadconnection.dapp.api.v2.controller.admin;

import com.nomadconnection.dapp.api.v2.dto.AdminDto;
import com.nomadconnection.dapp.api.v2.service.admin.AdminCardIssuanceService;
import com.nomadconnection.dapp.api.v2.service.admin.AdminCorpService;
import com.nomadconnection.dapp.api.v2.service.admin.AdminUserService;
import com.nomadconnection.dapp.api.v2.service.admin.CommonAdminService;
import com.nomadconnection.dapp.core.annotation.ApiPageable;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController("AdminV2Controller")
@RequiredArgsConstructor
@RequestMapping(AdminController.URI.BASE)
@CrossOrigin(allowCredentials = "true")
@Api(tags = "어드민 V2")
public class AdminController {

	public static class URI {
		public static final String BASE = "/admin/v2";

		public static final String USERS = "/users";
		public static final String CORPS = "/corps";
		public static final String ISSUANCES = "/issuances";
		public static final String CERTS = "/certs";
	}

	private final CommonAdminService commonAdminService;
	private final AdminUserService adminUserService;
	private final AdminCorpService adminCorpService;
	private final AdminCardIssuanceService adminCardIssuanceService;

	@ApiPageable
	@GetMapping(value = URI.USERS)
	public ResponseEntity<?> getUserList(
		@ApiIgnore @CurrentUser CustomUser user,
		@RequestParam(required = false) String keyWord,
		@PageableDefault Pageable pageable){
		if(commonAdminService.isGowidAdmin(user.idx())) {
			return adminUserService.getUserList(keyWord, pageable);
		}
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = URI.USERS + "/{idxUser}")
	public ResponseEntity<?> getUserInfo(
		@ApiIgnore @CurrentUser CustomUser user,
		@PathVariable Long idxUser){
		if(commonAdminService.isGowidAdmin(user.idx())) {
			return adminUserService.getUserInfo(idxUser);
		}
		return ResponseEntity.ok().build();
	}

	@PatchMapping(value = URI.USERS + "/{idxUser}")
	public ResponseEntity<?> updateUserInfo(
		@ApiIgnore @CurrentUser CustomUser user,
		@RequestBody AdminDto.UpdateUserDto dto,
		@PathVariable Long idxUser){
		if(commonAdminService.isGowidAdmin(user.idx())) {
			return adminUserService.updateUserInfo(idxUser, dto);
		}
		return ResponseEntity.ok().build();
	}

	@DeleteMapping(value = URI.USERS + "/{idxUser}")
	public ResponseEntity<?> initUserInfo(
		@ApiIgnore @CurrentUser CustomUser user,
		@PathVariable Long idxUser){
		if(commonAdminService.isGowidAdmin(user.idx())) {
			return adminUserService.initUserInfo(idxUser);
		}
		return ResponseEntity.ok().build();
	}

	@ApiPageable
	@GetMapping(value = URI.CORPS)
	public ResponseEntity<?> getCorpList(
		@ApiIgnore @CurrentUser CustomUser user,
		@RequestParam(required = false) String keyWord,
		@PageableDefault Pageable pageable){
		if(commonAdminService.isGowidAdmin(user.idx())) {
			return adminCorpService.getCorpList(keyWord, pageable);
		}
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = URI.CORPS + "/{idxCorp}")
	public ResponseEntity<?> getCorpInfo(
		@ApiIgnore @CurrentUser CustomUser user,
		@PathVariable Long idxCorp){
		if(commonAdminService.isGowidAdmin(user.idx())) {
			return adminCorpService.getCorpInfo(idxCorp);
		}
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = URI.ISSUANCES + "/{idxCardIssuanceInfo}")
	public ResponseEntity<?> getIssuanceInfo(
		@ApiIgnore @CurrentUser CustomUser user,
		@PathVariable Long idxCardIssuanceInfo){
		if(commonAdminService.isGowidAdmin(user.idx())) {
			return adminCardIssuanceService.getIssuanceInfo(idxCardIssuanceInfo);
		}
		return ResponseEntity.ok().build();
	}

	@PatchMapping(value = URI.ISSUANCES + "/{idxCardIssuanceInfo}")
	public ResponseEntity<?> updateIssuanceStatus(
		@ApiIgnore @CurrentUser CustomUser user,
		@RequestBody AdminDto.UpdateIssuanceStatusDto dto,
		@PathVariable Long idxCardIssuanceInfo){
		if(commonAdminService.isGowidAdmin(user.idx())) {
			return adminCardIssuanceService.updateIssuanceStatus(idxCardIssuanceInfo, dto);
		}
		return ResponseEntity.ok().build();
	}

	@ApiPageable
	@GetMapping(value = URI.CERTS + "/{idxUser}")
	public ResponseEntity<?> getCertList(
		@ApiIgnore @CurrentUser CustomUser user,
		@PathVariable Long idxUser){
		if(commonAdminService.isGowidAdmin(user.idx())) {
			return adminCorpService.getCertList(idxUser);
		}
		return ResponseEntity.ok().build();
	}
}
