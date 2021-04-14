package com.nomadconnection.dapp.api.v2.controller.card;

import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.dto.FinancialConsumersRequestDto;
import com.nomadconnection.dapp.api.dto.FinancialConsumersResponseDto;
import com.nomadconnection.dapp.api.v2.dto.cardissuanceinfo.IssuanceDepthResponseDto;
import com.nomadconnection.dapp.api.v2.service.card.CommonCardServiceV2;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceDepth;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;


@Slf4j
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping(CommonCardControllerV2.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "카드사공통 법인카드 발급")
public class CommonCardControllerV2 {
    public static class URI {
        public static final String BASE = "/card/v2";
        public static final String CORPORATION = "/corporation";
        public static final String CORPORATION_TYPE = "/corporation/type";
        public static final String CORPORATION_EXTEND = "/corporation/extend";
        public static final String VENTURE = "/venture";
        public static final String STOCKHOLDER = "/stockholder";
        public static final String STOCKHOLDER_FILES = "/stockholder/files";
        public static final String CARD = "/card";
        public static final String CARD_HOPE_LIMIT = "/card/hopeLimit";
        public static final String ISSUANCE_DEPTH = "/issuance/depth";
        public static final String ACCOUNT = "/account";
        public static final String CEO = "/ceo";
        public static final String CEO_ID = "/ceo/identification";
        public static final String MANAGER = "/manager";
        public static final String CARDS = "/cards";
        public static final String FINANCIAL_CONSUMERS = "/financial-consumers";
    }

    private final CommonCardServiceV2 commonCardService;

    @ApiOperation("법인정보 수정")
    @PutMapping(URI.CORPORATION)
    public ResponseEntity<CardIssuanceDto.CorporationRes> updateCorporation(
        @ApiIgnore @CurrentUser CustomUser user,
        @RequestParam Long idxCardInfo,
        @RequestParam(defaultValue = "GOWID") CardType cardType,
        @RequestBody @Valid CardIssuanceDto.RegisterCorporation dto) {
        if (log.isInfoEnabled()) {
            log.info("([ updateCorporation ]) $user='{}' $dto='{}' $idx_cardInfo='{}'", user, dto, idxCardInfo);
        }

        return ResponseEntity.ok().body(commonCardService.updateCorporation(user.idx(), dto, idxCardInfo, cardType));
    }

    @ApiOperation("법인정보 업종종류 조회")
    @GetMapping(URI.CORPORATION_TYPE)
    public ResponseEntity<List<CardIssuanceDto.BusinessType>> getBusinessType(
        @ApiIgnore @CurrentUser CustomUser user) {
        if (log.isInfoEnabled()) {
            log.info("([ getBusinessType ]) $user='{}'", user);
        }

        return ResponseEntity.ok().body(commonCardService.getBusinessType());
    }

    // 현재는 롯데카드만 사용
    // 추가될 카드에 맞춰 수정 예정
    @ApiOperation("법인추가정보 등록")
    @PostMapping(URI.CORPORATION_EXTEND)
    public ResponseEntity<CardIssuanceDto.CorporationExtendRes> updateCorporationExtend(
        @ApiIgnore @CurrentUser CustomUser user,
        @RequestParam Long idxCardInfo,
        @RequestParam(defaultValue = "GOWID") CardType cardType,
        @RequestBody @Valid CardIssuanceDto.RegisterCorporationExtend dto) {
        if (log.isInfoEnabled()) {
            log.info("([ updateCorporationExtend ]) $user='{}' $dto='{}' $idx_cardInfo='{}'", user, dto, idxCardInfo);
        }

        return ResponseEntity.ok().body(commonCardService.updateCorporationExtend(user.idx(), dto, idxCardInfo, cardType));
    }

    @ApiOperation("주주정보 등록")
    @PostMapping(URI.STOCKHOLDER)
    public ResponseEntity<CardIssuanceDto.VentureRes> registerStockholder(
        @ApiIgnore @CurrentUser CustomUser user,
        @RequestParam Long idxCardInfo,
        @RequestParam(defaultValue = "GOWID") CardType cardType,
        @RequestBody @Valid CardIssuanceDto.RegisterStockholder dto) {
        if (log.isInfoEnabled()) {
            log.info("([ registerStockholder ]) $user='{}' $dto='{}' $idx_cardInfo='{}'", user, dto, idxCardInfo);
        }

        return ResponseEntity.ok().body(commonCardService.registerStockholder(user.idx(), dto, idxCardInfo, cardType));
    }

    @ApiOperation("주주명부 파일 등록")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "fileType", value = "BASIC:주주명부, MAJOR:1대주주명부", dataType = "String")
    })
    @PostMapping(URI.STOCKHOLDER_FILES)
    public ResponseEntity<List<CardIssuanceDto.StockholderFileRes>> uploadStockholderFile(
        @ApiIgnore @CurrentUser CustomUser user,
        @RequestParam(defaultValue = "GOWID") CardType cardType,
        @RequestParam Long idxCardInfo,
        @RequestParam String fileType,
        @RequestPart MultipartFile[] file_1,
        @RequestPart MultipartFile[] file_2) throws IOException {
        if (log.isInfoEnabled()) {
            log.info("([ uploadStockholderFile ]) $user='{}' $file_1='{}' $file_2='{}' $idx_cardInfo='{}'", user, file_1, file_2, idxCardInfo);
        }

        return ResponseEntity.ok().body(commonCardService.registerStockholderFile(user.idx(), file_1, file_2, fileType, idxCardInfo, cardType));
    }

    @ApiOperation("카드발급정보 전체조회")
    @GetMapping(URI.CARDS)
    public ResponseEntity<List<CardIssuanceDto.CardIssuanceInfoRes>> getCards(
        @ApiIgnore @CurrentUser CustomUser user) {
        if (log.isInfoEnabled()) {
            log.info("([ getCards ]) $user='{}'", user);
        }

        return ResponseEntity.ok().body(commonCardService.getCards(user.idx()));
    }

    @ApiOperation("카드발급정보 등록")
    @PostMapping(URI.CARD)
    public ResponseEntity<CardIssuanceDto.CardRes> registerCard(
        @ApiIgnore @CurrentUser CustomUser user,
        @RequestParam Long idxCardInfo,
        @RequestParam(defaultValue = "GOWID") CardType cardType,
        @RequestBody @Valid CardIssuanceDto.RegisterCard dto) {
        if (log.isInfoEnabled()) {
            log.info("([ registerStockholder ]) $user='{}' $dto='{}' $idx_cardInfo='{}'", user, dto, idxCardInfo);
        }

        return ResponseEntity.ok().body(commonCardService.registerCard(user.idx(), dto, idxCardInfo, cardType));
    }

    @ApiOperation("벤처기업사 조회")
    @GetMapping(URI.VENTURE)
    public ResponseEntity<List<String>> getVenture(
        @ApiIgnore @CurrentUser CustomUser user) {
        if (log.isInfoEnabled()) {
            log.info("([ getCardIssuanceByUser ]) $user='{}'", user);
        }

        return ResponseEntity.ok().body(commonCardService.getVentureBusiness());
    }

    @ApiOperation("벤처기업정보 등록")
    @PostMapping(URI.VENTURE)
    public ResponseEntity<CardIssuanceDto.VentureRes> registerVenture(
        @ApiIgnore @CurrentUser CustomUser user,
        @RequestParam Long idxCardInfo,
        @RequestParam(defaultValue = "GOWID") CardType cardType,
        @RequestBody @Valid CardIssuanceDto.RegisterVenture dto) {
        if (log.isInfoEnabled()) {
            log.info("([ registerVenture ]) $user='{}' $dto='{}' $idx_cardInfo='{}'", user, dto, idxCardInfo);
        }

        return ResponseEntity.ok().body(commonCardService.registerVenture(user.idx(), dto, idxCardInfo, cardType));
    }

    @ApiOperation("카드 희망한도 저장")
    @PostMapping(URI.CARD_HOPE_LIMIT)
    public ResponseEntity<CardIssuanceDto.CardRes> saveHopeLimit(
        @ApiIgnore @CurrentUser CustomUser user,
        @RequestBody @Valid CardIssuanceDto.HopeLimitReq dto) {
        if (log.isInfoEnabled()) {
            log.info("([ saveHopeLimit ]) $user='{}' $dto='{}'", user, dto);
        }

        return ResponseEntity.ok().body(commonCardService.saveHopeLimit(user.idx(), dto));
    }

    @ApiOperation("결제계좌 등록")
    @PostMapping(URI.ACCOUNT)
    public ResponseEntity<CardIssuanceDto.AccountRes> registerAccount(
        @ApiIgnore @CurrentUser CustomUser user,
        @RequestParam Long idxCardInfo,
        @RequestParam(defaultValue = "GOWID") CardType cardType,
        @RequestBody @Valid CardIssuanceDto.RegisterAccount dto) {
        if (log.isInfoEnabled()) {
            log.info("([ registerAccount ]) $user='{}' $dto='{}' $idx_cardInfo='{}'", user, dto, idxCardInfo);
        }

        return ResponseEntity.ok().body(commonCardService.registerAccount(user.idx(), dto, idxCardInfo, cardType));
    }

    @ApiOperation("대표자 종류")
    @GetMapping(URI.CEO)
    public ResponseEntity<CardIssuanceDto.CeoTypeRes> getCeoType(
        @ApiIgnore @CurrentUser CustomUser user) {
        if (log.isInfoEnabled()) {
            log.info("([ getCeo ]) $user='{}'", user);
        }

        return ResponseEntity.ok().body(commonCardService.getCeoType(user.idx()));
    }

    @ApiOperation("대표자 등록/수정")
    @PostMapping(URI.CEO)
    public ResponseEntity<CardIssuanceDto.CeoRes> registerCEO(
        @ApiIgnore @CurrentUser CustomUser user,
        @RequestParam Long idxCardInfo,
        @RequestParam(defaultValue = "GOWID") CardType cardType,
        @RequestBody @Valid CardIssuanceDto.RegisterCeo dto) {
        if (log.isInfoEnabled()) {
            log.info("([ registerCEO ]) $user='{}' $dto='{}' $idx_cardInfo='{}'", user, dto, idxCardInfo);
        }

        return ResponseEntity.ok().body(commonCardService.registerCeo(user.idx(), dto, idxCardInfo, cardType));
    }

    @ApiOperation("관리책임자 업데이트")
    @PostMapping(URI.MANAGER)
    public ResponseEntity<CardIssuanceDto.ManagerRes> registerManager(
        @ApiIgnore @CurrentUser CustomUser user,
        @RequestParam Long idxCardInfo,
        @RequestParam(defaultValue = "GOWID") CardType cardType,
        @RequestBody @Valid CardIssuanceDto.RegisterManager dto) {
        if (log.isInfoEnabled()) {
            log.info("([ registerManager ]) $user='{}' $dto='{}' $idx_cardInfo='{}'", user, dto, idxCardInfo);
        }

        return ResponseEntity.ok().body(commonCardService.registerManager(user.idx(), dto, idxCardInfo, cardType));
    }

    @ApiOperation(value = "신분증 본인 확인")
    @PostMapping(URI.CEO_ID)
    public ResponseEntity<?> verifyIdentification(
        HttpServletRequest request,
        @ApiIgnore @CurrentUser CustomUser user,
        @RequestParam(defaultValue = "GOWID") CardType cardType,
        @ModelAttribute @Valid CardIssuanceDto.IdentificationReq dto) {
        if (log.isInfoEnabled()) {
            log.info("([ verifyIdentification ]) $user='{}' $dto='{}'", user, dto);
        }

        commonCardService.verifyCeoIdentification(request, user.idx(), cardType, dto);
        return ResponseEntity.ok().build();
    }

    @ApiOperation("발급화면 진행상황 저장")
    @PatchMapping(URI.ISSUANCE_DEPTH)
    public ResponseEntity saveIssuanceDepth(
        @ApiIgnore @CurrentUser CustomUser user,
        @RequestParam IssuanceDepth depthKey,
        @RequestParam(defaultValue = "GOWID") CardType cardType) {
        if (log.isInfoEnabled()) {
            log.info("([ saveIssuanceDepth ]) $user='{}' $depthKey='{}', $cardType='{}'", user, depthKey, cardType);
        }

        IssuanceDepthResponseDto issuanceDepthResponseDto = commonCardService.saveIssuanceDepth(user.idx(), depthKey, cardType);
        return ResponseEntity.ok().body(issuanceDepthResponseDto);
    }

    @ApiOperation("금융소비자 정보 유형 정보 업데이트")
    @PatchMapping(URI.FINANCIAL_CONSUMERS)
    public ResponseEntity saveConsumerType(
        @ApiIgnore @CurrentUser CustomUser user,
        @RequestParam(defaultValue = "GOWID") CardType cardType,
        @RequestBody FinancialConsumersRequestDto financialConsumersRequestDto) {
        if (log.isInfoEnabled()) {
            log.info("([ saveIssuanceDepth ]) $user='{}', $cardType='{}'", user, cardType);
        }

        FinancialConsumersResponseDto response = commonCardService.updateFinancialConsumersInfo(user.idx(), cardType, financialConsumersRequestDto);
        return ResponseEntity.ok().body(response);
    }

}
