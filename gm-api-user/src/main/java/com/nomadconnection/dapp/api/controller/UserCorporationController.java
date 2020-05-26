package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.UserCorporationDto;
import com.nomadconnection.dapp.api.service.UserCorporationService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(UserController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "법인카드 발급", description = UserController.URI.BASE)
@SuppressWarnings({"unused", "deprecation"})
public class UserCorporationController {

    @SuppressWarnings("WeakerAccess")
    public static class URI {
        public static final String BASE = "/corp/v1";
        public static final String CORPORATION = "/corporation";
        public static final String VENTURE = "/venture";
    }

    private final UserCorporationService service;

    @ApiOperation("법인정보 등록")
    @PostMapping(URI.CORPORATION)
    public ResponseEntity registerCorporation(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestBody @Valid UserCorporationDto.RegisterCorporation dto) {
        if (log.isInfoEnabled()) {
            log.debug("([ registerCorporation ]) $user='{}',  $dto='{}'", user, dto);
        }

        return ResponseEntity.ok().body(service.registerCorporation(user.idx(), dto));
    }

    @ApiOperation("벤처기업정보 등록")
    @PostMapping(URI.VENTURE)
    public ResponseEntity registerVenture(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestBody @Valid UserCorporationDto.RegisterVenture dto) {
        if (log.isInfoEnabled()) {
            log.debug("([ registerVenture ]) $user='{}', $dto='{}'", user, dto);
        }

        return ResponseEntity.ok().body(service.registerVenture(user.idx(), dto));
    }
}
