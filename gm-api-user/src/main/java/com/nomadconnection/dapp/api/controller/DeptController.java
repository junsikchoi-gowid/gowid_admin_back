package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.DeptDto;
import com.nomadconnection.dapp.api.exception.NotAllowedException;
import com.nomadconnection.dapp.api.service.DeptService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.domain.repository.querydsl.DeptCustomRepository;
import com.nomadconnection.dapp.core.domain.user.Role;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping(DeptController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "부서", description = DeptController.URI.BASE)
public class DeptController {

	public static class URI {
		public static final String BASE = "/dept/v1";
		public static final String DEPTS = "/depts";
		public static final String DEPTS_DEPT = "/depts/{dept}";
		public static final String MEMBERS = "/members";
	}

	private final DeptService service;

	//==================================================================================================================
	//
	//	등록된 부서 목록 조회
	//
	//==================================================================================================================

	@ApiOperation(value = "등록된 부서 목록 조회", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - 없음" +
			"\n")
	@GetMapping(URI.DEPTS)
//	public List<DeptDto> getDepts(
	public List<DeptCustomRepository.DeptWithMemberCountDto> getDepts(
			@ApiIgnore @CurrentUser CustomUser user
	) {
		if (log.isDebugEnabled()) {
			log.debug("([ getDepts ]) $user='{}'", user);
		}
//		return service.getDepts(user.idx());
		return service.depts(user.idx());
	}

	//==================================================================================================================
	//
	//	새로운 부서 등록: 부서명
	//
	//==================================================================================================================

	@ApiOperation(value = "부서 등록", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - 없음" +
			"\n")
	@PostMapping(URI.DEPTS)
	public DeptDto postDepts(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam String dept
	) {
		if (log.isDebugEnabled()) {
			log.debug("([ postDepts ]) $user='{}', $dept='{}'", user, dept);
		}
		return service.addDept(user.idx(), dept);
	}

	//==================================================================================================================
	//
	//	부서명 변경
	//
	//==================================================================================================================

	@ApiOperation(value = "부서명 변경", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n -" +
			"\n")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "dept", value = "식별자(부서)", dataType = "Long")
	})
	@PutMapping(URI.DEPTS_DEPT)
	public ResponseEntity<?> putDept(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable Long dept,
			@RequestParam String name
	) {
		if (log.isDebugEnabled()) {
			log.debug("([ putDept ]) $user='{}', $dept.idx='{}', $dept.name='{}'", user, dept, name);
		}
		if (user.getAuthorities().stream().map(GrantedAuthority::getAuthority).map(Role::valueOf).noneMatch(Role::isUpdatableDeptName)) {
			throw NotAllowedException.builder().build();
		}
		service.updateDeptName(user.idx(), dept, name);
		return ResponseEntity.ok().build();
	}

	//==================================================================================================================
	//
	//	부서 삭제(부서 내 멤버가 존재하는 경우, 모두 부서 미지정 상태로 설정됨)
	//
	//==================================================================================================================

	@ApiOperation(value = "부서 삭제", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - 부서 내 멤버가 존재하는 경우, 모두 부서 미지정 상태로 설정됨" +
			"\n")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "dept", value = "식별자(부서)", dataType = "Long")
	})
	@DeleteMapping(URI.DEPTS_DEPT)
	public ResponseEntity<?> deleteDept(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable Long dept
	) {
		if (log.isDebugEnabled()) {
			log.debug("([ deleteDept ]) $user='{}', $dept.idx='{}'", user, dept);
		}
		service.deleteDept(user.idx(), dept);
		return ResponseEntity.ok().build();
	}

	//==================================================================================================================
	//
	//	멤버 조회
	//
	//==================================================================================================================

	@ApiOperation(value = "멤버 조회", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n -" +
			"\n")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "dept", value = "식별자(부서): 설정되지 않을 경우, 부서가 할당되지 않은 사용자 목록을 반환", dataType = "Long")
	})
	@GetMapping(URI.MEMBERS)
	public List<DeptDto.DeptMember> getMembers(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam(required = false) Long dept
	) {
		if (log.isDebugEnabled()) {
			log.debug("([ getMembers ]) $user='{}', $dept.idx='{}'", user, dept);
		}
		return service.getDeptMembers(user.idx(), dept);
	}
}
