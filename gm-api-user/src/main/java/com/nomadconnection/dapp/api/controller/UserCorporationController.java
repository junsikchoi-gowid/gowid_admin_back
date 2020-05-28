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
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(UserController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "법인카드 발급", description = UserController.URI.BASE)
public class UserCorporationController {

    @SuppressWarnings("WeakerAccess")
    public static class URI {
        public static final String BASE = "/corp/v1";
        public static final String CORPORATION = "/corporation";
        public static final String VENTURE = "/venture";
        public static final String STOCKHOLDER = "/stockholder";
        public static final String ACCOUNT = "/account";
        public static final String CARD = "/card";
    }

    private final UserCorporationService service;

    @ApiOperation("법인정보 등록")
    @PostMapping(URI.CORPORATION)
    public ResponseEntity registerCorporation(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestParam (required = false) Long idxCardInfo,
            @RequestBody @Valid UserCorporationDto.RegisterCorporation dto) {
        if (log.isInfoEnabled()) {
            log.debug("([ registerCorporation ]) $user='{}', $dto='{}', $idx_cardInfo='{}'", user, dto, idxCardInfo);
        }

        return ResponseEntity.ok().body(service.registerCorporation(user.idx(), dto, idxCardInfo));
    }

    @ApiOperation("벤처기업정보 등록")
    @PostMapping(URI.VENTURE)
    public ResponseEntity registerVenture(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestParam Long idxCardInfo,
            @RequestBody @Valid UserCorporationDto.RegisterVenture dto) {
        if (log.isInfoEnabled()) {
            log.debug("([ registerVenture ]) $user='{}', $dto='{}', $idx_cardInfo='{}'", user, dto, idxCardInfo);
        }

        return ResponseEntity.ok().body(service.registerVenture(user.idx(), dto, idxCardInfo));
    }

    @ApiOperation("주주명부 등록")
    @PostMapping(URI.STOCKHOLDER)
    public ResponseEntity registerStockholder(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestParam Long idxCardInfo,
            @RequestBody @Valid UserCorporationDto.RegisterStockholder dto) {
        if (log.isInfoEnabled()) {
            log.debug("([ registerStockholder ]) $user='{}', $dto='{}', $idx_cardInfo='{}'", user, dto, idxCardInfo);
        }

        return ResponseEntity.ok().body(service.registerStockholder(user.idx(), dto, idxCardInfo));
    }

    @ApiOperation("주주명부 등록")
    @PostMapping(URI.CARD)
    public ResponseEntity registerCard(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestParam Long idxCardInfo,
            @RequestBody @Valid UserCorporationDto.RegisterCard dto) {
        if (log.isInfoEnabled()) {
            log.debug("([ registerStockholder ]) $user='{}', $dto='{}', $idx_cardInfo='{}'", user, dto, idxCardInfo);
        }

        return ResponseEntity.ok().body(service.registerCard(user.idx(), dto, idxCardInfo));
    }

    @ApiOperation("결제계좌 등록")
    @PostMapping(URI.ACCOUNT)
    public ResponseEntity registerAccount(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestParam Long idxCardInfo,
            @RequestBody @Valid UserCorporationDto.RegisterAccount dto) {
        if (log.isInfoEnabled()) {
            log.debug("([ registerAccount ]) $user='{}', $dto='{}', $idx_cardInfo='{}'", user, dto, idxCardInfo);
        }

        return ResponseEntity.ok().body(service.registerAccount(user.idx(), dto, idxCardInfo));
    }

}
