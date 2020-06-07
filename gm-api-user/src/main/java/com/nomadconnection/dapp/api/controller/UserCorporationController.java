package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.UserCorporationDto;
import com.nomadconnection.dapp.api.service.UserCorporationService;
import com.nomadconnection.dapp.api.service.shinhan.IssuanceService;
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
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(UserCorporationController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "법인카드 발급", description = UserCorporationController.URI.BASE)
public class UserCorporationController {

    public static class URI {
        public static final String BASE = "/corp/v1";
        public static final String CORPORATION = "/corporation";
        public static final String CORPORATION_TYPE = "/corporation/type";
        public static final String VENTURE = "/venture";
        public static final String STOCKHOLDER = "/stockholder";
        public static final String STOCKHOLDER_FILES = "/stockholder/files";
        public static final String STOCKHOLDER_FILES_IDX = "/stockholder/files/{idxFile}";
        public static final String ACCOUNT = "/account";
        public static final String ISSUANCE = "/issuance";
        public static final String ISSUANCE_IDX = "/issuance/{idxCardInfo}";
        public static final String CARD = "/card";
        public static final String CEO = "/ceo";
    }

    private final UserCorporationService service;
    private final IssuanceService issuanceService;

    @ApiOperation("법인정보 업종종류 조회")
    @GetMapping(URI.CORPORATION_TYPE)
    public ResponseEntity<List<UserCorporationDto.BusinessType>> getBusinessType(
            @ApiIgnore @CurrentUser CustomUser user) {
        if (log.isInfoEnabled()) {
            log.info("([ getBusinessType ]) $user='{}'", user);
        }

        return ResponseEntity.ok().body(service.getBusinessType());
    }

    @ApiOperation("법인정보 등록")
    @PostMapping(URI.CORPORATION)
    public ResponseEntity<UserCorporationDto.CorporationRes> registerCorporation(
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
    public ResponseEntity<UserCorporationDto.VentureRes> registerVenture(
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
    public ResponseEntity<UserCorporationDto.VentureRes> registerStockholder(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestParam Long idxCardInfo,
            @RequestBody @Valid UserCorporationDto.RegisterStockholder dto) {
        if (log.isInfoEnabled()) {
            log.info("([ registerStockholder ]) $user='{}', $dto='{}', $idx_cardInfo='{}'", user, dto, idxCardInfo);
        }

        return ResponseEntity.ok().body(service.registerStockholder(user.idx(), dto, idxCardInfo));
    }

    @ApiOperation("주주명부 파일 등록")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileType", value = "BASIC:주주명부, MAJOR:1대주주명부", dataType = "String")
    })
    @PostMapping(URI.STOCKHOLDER_FILES)
    public ResponseEntity<UserCorporationDto.StockholderFileRes> uploadStockholderFile(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestParam Long idxCardInfo,
            @RequestParam String fileType,
            @RequestPart MultipartFile file) {
        if (log.isInfoEnabled()) {
            log.info("([ uploadStockholderFile ]) $user='{}', $file='{}', $idx_cardInfo='{}'", user, file, idxCardInfo);
        }

        return ResponseEntity.ok().body(service.uploadStockholderFile(user.idx(), file, fileType, idxCardInfo));
    }

    @ApiOperation("주주명부 파일 삭제")
    @DeleteMapping(URI.STOCKHOLDER_FILES_IDX)
    public ResponseEntity<ResponseEntity.BodyBuilder> deleteStockholderFile(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestParam Long idxCardInfo,
            @PathVariable Long idxFile) {
        if (log.isInfoEnabled()) {
            log.info("([ deleteStockholderFile ]) $user='{}', $idx_file='{}', $idx_cardInfo='{}'", user, idxFile, idxCardInfo);
        }

        service.deleteStockholderFile(user.idx(), idxFile, idxCardInfo);
        return ResponseEntity.ok().build();
    }

    @ApiOperation("카드발급정보 등록")
    @PostMapping(URI.ISSUANCE)
    public ResponseEntity<UserCorporationDto.CardRes> registerCard(
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
    public ResponseEntity<UserCorporationDto.AccountRes> registerAccount(
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
    public ResponseEntity<UserCorporationDto.CeoTypeRes> getCeo(
            @ApiIgnore @CurrentUser CustomUser user) {
        if (log.isInfoEnabled()) {
            log.info("([ getCeo ]) $user='{}'", user);
        }

        return ResponseEntity.ok().body(service.getCeoType(user.idx()));
    }

    @ApiOperation("대표자 등록")
    @PostMapping(URI.CEO)
    public ResponseEntity<UserCorporationDto.CeoRes> registerCEO(
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
    public ResponseEntity<UserCorporationDto.CardIssuanceInfoRes> getCardIssuanceByUser(
            @ApiIgnore @CurrentUser CustomUser user) {
        if (log.isInfoEnabled()) {
            log.info("([ getCardIssuanceByUser ]) $user='{}'", user);
        }

        return ResponseEntity.ok().body(service.getCardIssuanceInfoByUser(user.idx()));
    }

    @ApiOperation("벤처기업사 조회")
    @GetMapping(URI.VENTURE)
    public ResponseEntity<List<String>> getVenture(
            @ApiIgnore @CurrentUser CustomUser user) {
        if (log.isInfoEnabled()) {
            log.info("([ getCardIssuanceByUser ]) $user='{}'", user);
        }

        return ResponseEntity.ok().body(service.getVentureBusiness());
    }

    @ApiOperation("카드발급정보 전체조회")
    @GetMapping(URI.ISSUANCE_IDX)
    public ResponseEntity<UserCorporationDto.CardIssuanceInfoRes> getCardIssuanceByUser(
            @ApiIgnore @CurrentUser CustomUser user,
            @PathVariable Long idxCardInfo) {
        if (log.isInfoEnabled()) {
            log.info("([ getCardIssuanceByUser ]) $user='{}', $idx_cardInfo='{}'", user, idxCardInfo);
        }

        return ResponseEntity.ok().body(service.getCardIssuanceInfo(idxCardInfo));
    }

    @ApiOperation(value = "법인카드 발급 신청")
    @PostMapping(URI.CARD)
    public ResponseEntity<UserCorporationDto.IssuanceRes> application(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestBody @Valid UserCorporationDto.IssuanceReq request) {

        return ResponseEntity.ok().body(
                issuanceService.issuance(user.idx(), request)
        );
    }

    // todo : 게이트웨이에서 수신 되므로, 인증 우회방안 처리 필요.
    @ApiOperation(value = "법인카드 발급 재개")
    @PostMapping(URI.CARD)
    public ResponseEntity<UserCorporationDto.IssuanceRes> resumeApplication(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestBody @Valid UserCorporationDto.IssuanceReq request) {

        return ResponseEntity.ok().body(
                issuanceService.issuance(user.idx(), request)
        );
    }

}
