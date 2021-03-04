package com.nomadconnection.dapp.api.v2.controller.admin;

import com.nomadconnection.dapp.api.v2.dto.AdminDto;
import com.nomadconnection.dapp.api.v2.service.admin.AdminUserService;
import com.nomadconnection.dapp.core.annotation.ApiPageable;
import com.nomadconnection.dapp.core.domain.repository.querydsl.UserCustomRepository;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AdminUserController extends AdminBaseController {
    public static class URI {
        public static final String USERS = "/users";
    }

    private final AdminUserService adminUserService;

    @PreAuthorize("hasAnyAuthority('GOWID_ADMIN')")
    @ApiOperation( value = "유저목록 조회")
    @ApiPageable
    @GetMapping(value = URI.USERS)
    public ResponseEntity<Page<UserCustomRepository.UserListDto>> getUserList(
        @ModelAttribute UserCustomRepository.UserListDto dto,
        @PageableDefault Pageable pageable){
        return ResponseEntity.ok().body(adminUserService.getUserList(dto, pageable));
    }

    @PreAuthorize("hasAnyAuthority('GOWID_ADMIN')")
    @ApiOperation( value = "유저정보 조회")
    @GetMapping(value = URI.USERS + "/{idxUser}")
    public ResponseEntity<UserCustomRepository.UserInfoDto> getUserInfo(
        @PathVariable Long idxUser){
        return ResponseEntity.ok().body(adminUserService.getUserInfo(idxUser));
    }

    @PreAuthorize("hasAnyAuthority('GOWID_ADMIN')")
    @ApiOperation( value = "유저정보 업데이트")
    @PatchMapping(value = URI.USERS + "/{idxUser}")
    public ResponseEntity<?> updateUserInfo(
        @RequestBody AdminDto.UpdateUserDto dto,
        @PathVariable Long idxUser){
        return adminUserService.updateUserInfo(idxUser, dto);
    }

    @PreAuthorize("hasAnyAuthority('GOWID_ADMIN')")
    @ApiOperation( value = "유저정보 초기화")
    @DeleteMapping(value = URI.USERS + "/{idxUser}")
    public ResponseEntity<?> initUserInfo(
        @PathVariable Long idxUser){
        return adminUserService.initUserInfo(idxUser);
    }
}
