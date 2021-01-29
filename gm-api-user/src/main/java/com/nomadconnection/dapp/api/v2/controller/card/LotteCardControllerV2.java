package com.nomadconnection.dapp.api.v2.controller.card;

import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.dto.lotte.StatusDto;
import com.nomadconnection.dapp.api.service.lotte.LotteIssuanceService;
import com.nomadconnection.dapp.api.v2.service.card.LotteCardServiceV2;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.domain.common.SignatureHistory;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping(LotteCardControllerV2.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "롯데 법인카드 발급", description = LotteCardControllerV2.URI.BASE)
public class LotteCardControllerV2 {

    public static class URI {
        public static final String BASE = "/card/v2/lotte";
        public static final String APPLY = "/apply";
        public static final String NEW = "/new";
    }

    private final LotteCardServiceV2 lotteCardService;
    private final LotteIssuanceService issuanceService;

    @ApiOperation(value = "법인카드 발급 신규대상자 확인")
    @PostMapping(URI.NEW)
    public ResponseEntity<StatusDto> verifyNewMember(
        @ApiIgnore @CurrentUser CustomUser user) {
        if (log.isInfoEnabled()) {
            log.info("([ verifyNewMember ]) $user='{}'", user);
        }
        return ResponseEntity.ok().body(lotteCardService.verifyNewMember(user.idx()));
    }

    @ApiOperation(value = "법인카드 발급 신청")
    @PostMapping(URI.APPLY)
    public ResponseEntity<CardIssuanceDto.IssuanceRes> application(
        @ApiIgnore HttpSession httpSession,
        @ApiIgnore @CurrentUser CustomUser user,
        @ModelAttribute @Valid CardIssuanceDto.IssuanceReq request) {
        if (log.isInfoEnabled()) {
            log.info("([ application ]) $user='{}' $dto='{}'", user, request);
        }

        if (httpSession.getAttribute(request.getCardIssuanceInfoIdx().toString()) == null) {
            httpSession.setAttribute(request.getCardIssuanceInfoIdx().toString(), true); // value값은 큰 의미 없음
            SignatureHistory signatureHistory = issuanceService.verifySignedBinaryAndSave(user.idx(), request.getSignedBinaryString());
            issuanceService.issuance(user.idx(), request, signatureHistory.getIdx());
            httpSession.removeAttribute(request.getCardIssuanceInfoIdx().toString());
        } else {
            log.info("[ application ] Already running service {}'s {}", user.email(), request.getCardIssuanceInfoIdx());
        }
        return ResponseEntity.ok().build();
    }
}
