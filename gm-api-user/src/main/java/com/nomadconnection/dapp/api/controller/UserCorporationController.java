package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.UserCorporationDto;
import com.nomadconnection.dapp.api.service.UserCorporationService;
import com.nomadconnection.dapp.api.service.shinhan.IssuanceService;
import com.nomadconnection.dapp.api.service.shinhan.ResumeService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.domain.shinhan.SignatureHistory;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin(allowCredentials = "true")
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
        public static final String CARD = "/card";
        public static final String RESUME = "/resume";
        public static final String CEO = "/ceo";
        public static final String CEO_ID = "/ceo/identification";
    }

    private final UserCorporationService service;
    private final IssuanceService issuanceService;
    private final ResumeService resumeService;

    @ApiOperation("법인정보 업종종류 조회")
    @GetMapping(URI.CORPORATION_TYPE)
    public ResponseEntity<List<UserCorporationDto.BusinessType>> getBusinessType(
            @ApiIgnore @CurrentUser CustomUser user) {
        if (log.isInfoEnabled()) {
            log.info("([ getBusinessType ]) $user='{}'", user);
        }

        return ResponseEntity.ok().body(service.getBusinessType());
    }

    @ApiOperation("법인정보 수정")
    @PutMapping(URI.CORPORATION)
    public ResponseEntity<UserCorporationDto.CorporationRes> updateCorporation(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestParam(required = false) Long idxCardInfo,
            @RequestBody @Valid UserCorporationDto.RegisterCorporation dto) {
        if (log.isInfoEnabled()) {
            log.info("([ registerCorporation ]) $user='{}', $dto='{}', $idx_cardInfo='{}'", user, dto, idxCardInfo);
        }

        return ResponseEntity.ok().body(service.updateCorporation(user.idx(), dto, idxCardInfo));
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
    public ResponseEntity<List<UserCorporationDto.StockholderFileRes>> uploadStockholderFile(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestParam Long idxCardInfo,
            @RequestParam String fileType,
            @RequestPart MultipartFile[] file_1,
            @RequestPart MultipartFile[] file_2) throws IOException {
        if (log.isInfoEnabled()) {
            log.info("([ uploadStockholderFile ]) $user='{}', $file_1='{}', $file_2='{}', $idx_cardInfo='{}'", user, file_1, file_2, idxCardInfo);
        }

        return ResponseEntity.ok().body(service.registerStockholderFile(user.idx(), file_1, file_2, fileType, idxCardInfo));
    }

    @ApiOperation("주주명부 파일 삭제")
    @DeleteMapping(URI.STOCKHOLDER_FILES_IDX)
    public ResponseEntity<ResponseEntity.BodyBuilder> deleteStockholderFile(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestParam Long idxCardInfo,
            @PathVariable Long idxFile) throws IOException {
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

    @ApiOperation(value = "신분증 본인 확인")
    @PostMapping(URI.CEO_ID)
    public ResponseEntity<?> verifyIdentification(
            HttpServletRequest request,
            @ApiIgnore @CurrentUser CustomUser user,
            @ModelAttribute @Valid UserCorporationDto.IdentificationReq dto) {
        if (log.isInfoEnabled()) {
            log.info("([ verifyIdentification ]) $user='{}', $dto='{}'", user, dto);
        }

        issuanceService.verifyCeoIdentification(request, dto);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "법인카드 발급 신청")
    @PostMapping(URI.CARD)
    public ResponseEntity<UserCorporationDto.IssuanceRes> application(
            @ApiIgnore @CurrentUser CustomUser user,
            @ModelAttribute @Valid UserCorporationDto.IssuanceReq request,
            HttpServletRequest httpServletRequest) {

        SignatureHistory signatureHistory = issuanceService.verifySignedBinaryAndSave(user.idx(), request.getSignedBinaryString());
        issuanceService.issuance(user.idx(), httpServletRequest, request, signatureHistory.getIdx());

        return ResponseEntity.ok().build();
    }

    // todo : 에러처리
    @ApiOperation(value = "법인카드 발급 재개")
    @PostMapping(URI.RESUME)
    public ResponseEntity<UserCorporationDto.ResumeRes> resumeApplication(
            @RequestBody UserCorporationDto.ResumeReq request) {

        log.debug("## Received 1600");
        if (!ObjectUtils.isEmpty(request)) {
            log.debug("## request 1600 => " + request.toString());
        } else {
            log.warn("## request data of 1600 is empty!");
        }

        return ResponseEntity.ok().body(
                resumeService.resumeApplication(request)
        );
    }

}
