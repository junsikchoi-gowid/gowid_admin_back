package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.UserCorporationDto;
import com.nomadconnection.dapp.api.service.UserCorporationService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
        public static final String CORPORATION = "/corporation/{corpIdx}";
        public static final String VENTURE = "/venture/{corpIdx}";
    }

    private final UserCorporationService service;

    @ApiOperation("법인정보 등록")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "corpIdx", value = "법인회사 식별자", dataType = "Long")
    })
    @PostMapping(URI.CORPORATION)
    public ResponseEntity registerCorporation(
            @ApiIgnore @CurrentUser CustomUser user,
            @PathVariable Long corpIdx,
            @RequestBody UserCorporationDto.registerCorporation dto) {
        if (log.isInfoEnabled()) {
            log.debug("([ registerCorporation ]) $user='{}', $code='{}', $dto='{}'", user, corpIdx, dto);
        }

        return ResponseEntity.ok().body(service.registerCorporation(user.idx(), corpIdx, dto));
    }

    @ApiOperation("벤처기업정보 등록")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "corpIdx", value = "법인회사 식별자", dataType = "Long")
    })
    @PostMapping(URI.VENTURE)
    public ResponseEntity registerVenture(
            @ApiIgnore @CurrentUser CustomUser user,
            @PathVariable Long corpIdx,
            @RequestBody UserCorporationDto.registerVenture dto) {
        if (log.isInfoEnabled()) {
            log.debug("([ registerVenture ]) $user='{}', $code='{}', $dto='{}'", user, corpIdx, dto);
        }

        return ResponseEntity.ok().body(service.registerVenture(user.idx(), corpIdx, dto));
    }
}
