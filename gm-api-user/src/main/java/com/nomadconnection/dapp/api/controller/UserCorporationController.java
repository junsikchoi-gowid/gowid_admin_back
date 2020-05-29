package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.UserCorporationDto;
import com.nomadconnection.dapp.api.dto.shinhan.ui.IssuanceDto;
import com.nomadconnection.dapp.api.dto.shinhan.ui.UiResponse;
import com.nomadconnection.dapp.api.service.UserCorporationService;
import com.nomadconnection.dapp.api.service.shinhan.IssuanceService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.objects.annotations.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(UserCorporationController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "법인카드 발급", description = UserCorporationController.URI.BASE)
public class UserCorporationController {

    @SuppressWarnings("WeakerAccess")
    public static class URI {
        public static final String BASE = "/corp/v1";
        public static final String CORPORATION = "/corporation";
        public static final String VENTURE = "/venture";
        public static final String STOCKHOLDER = "/stockholder";
        public static final String ACCOUNT = "/account";
        public static final String ISSUANCE = "/issuance";
        public static final String ISSUANCE_IDX = "/issuance/{idxCardInfo}";
        public static final String CARD = "/card";
        public static final String CEO = "/ceo";
    }

    private final UserCorporationService service;
    private final IssuanceService issuanceService;

    @ApiOperation("법인정보 등록")
    @PostMapping(URI.CORPORATION)
    public ResponseEntity registerCorporation(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestParam(required = false) Long idxCardInfo,
            @RequestBody @Valid UserCorporationDto.RegisterCorporation dto) {
        if (log.isInfoEnabled()) {
            log.info("([ registerCorporation ]) $user='{}', $dto='{}', $idx_cardInfo='{}'", user, dto, idxCardInfo);
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
            log.info("([ registerVenture ]) $user='{}', $dto='{}', $idx_cardInfo='{}'", user, dto, idxCardInfo);
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
            log.info("([ registerStockholder ]) $user='{}', $dto='{}', $idx_cardInfo='{}'", user, dto, idxCardInfo);
        }

        return ResponseEntity.ok().body(service.registerStockholder(user.idx(), dto, idxCardInfo));
    }

    @ApiOperation("카드발급정보 등록")
    @PostMapping(URI.ISSUANCE)
    public ResponseEntity registerCard(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestParam Long idxCardInfo,
            @RequestBody @Valid UserCorporationDto.RegisterCard dto) {
        if (log.isInfoEnabled()) {
            log.info("([ registerStockholder ]) $user='{}', $dto='{}', $idx_cardInfo='{}'", user, dto, idxCardInfo);
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
            log.info("([ registerAccount ]) $user='{}', $dto='{}', $idx_cardInfo='{}'", user, dto, idxCardInfo);
        }

        return ResponseEntity.ok().body(service.registerAccount(user.idx(), dto, idxCardInfo));
    }

    @ApiOperation("대표자 종류")
    @GetMapping(URI.CEO)
    public ResponseEntity getCeo(
            @ApiIgnore @CurrentUser CustomUser user) {
        if (log.isInfoEnabled()) {
            log.info("([ getCeo ]) $user='{}'", user);
        }

        return ResponseEntity.ok().body(service.getCeoType(user.idx()));
    }

    @ApiOperation("대표자 등록(인증)")
    @PostMapping(URI.CEO)
    public ResponseEntity registerCEO(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestParam Long idxCardInfo,
            @RequestBody @Valid UserCorporationDto.RegisterCeo dto) throws IOException {
        if (log.isInfoEnabled()) {
            log.info("([ registerCEO ]) $user='{}', $dto='{}', $idx_cardInfo='{}'", user, dto, idxCardInfo);
        }

        return ResponseEntity.ok().body(service.registerCeo(user.idx(), dto, idxCardInfo));
    }

    @ApiOperation("카드발급정보 전체조회")
    @GetMapping(URI.ISSUANCE)
    public ResponseEntity getCardIssuanceByUser(
            @ApiIgnore @CurrentUser CustomUser user) {
        if (log.isInfoEnabled()) {
            log.info("([ getCardIssuanceByUser ]) $user='{}'", user);
        }

        return ResponseEntity.ok().body(service.getCardIssuanceInfoByUser(user.idx()));
    }

    @ApiOperation("카드발급정보 전체조회")
    @GetMapping(URI.ISSUANCE_IDX)
    public ResponseEntity getCardIssuanceByUser(
            @ApiIgnore @CurrentUser CustomUser user,
            @PathVariable Long idxCardInfo) {
        if (log.isInfoEnabled()) {
            log.info("([ getCardIssuanceByUser ]) $user='{}', $idx_cardInfo='{}'", user, idxCardInfo);
        }

        return ResponseEntity.ok().body(service.getCardIssuanceInfo(idxCardInfo));
    }

    /**
     * todo :
     * 1) request, response 정의
     * 2) 예외 처리
     * 3) 제네릭 타입 적용
     */
    @ApiOperation(value = "법인카드 발급 신청")
    @PostMapping(URI.CARD)
    public UiResponse application(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestBody @Valid IssuanceDto request) {

        return issuanceService.application(request.getBusinessLicenseNo());
    }

}
