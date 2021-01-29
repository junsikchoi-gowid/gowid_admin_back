package com.nomadconnection.dapp.api.v2.controller.card;

import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.service.shinhan.IssuanceService;
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

import javax.validation.Valid;

@Slf4j
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping(ShinhanCardControllerV2.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "신한 법인카드 발급", description = ShinhanCardControllerV2.URI.BASE)
public class ShinhanCardControllerV2 {

    public static class URI {
        public static final String BASE = "/card/v2/shinhan";
        public static final String APPLY = "/apply";
    }

    private final IssuanceService issuanceService;

    @ApiOperation(value = "법인카드 발급 신청")
    @PostMapping(URI.APPLY)
    public ResponseEntity<CardIssuanceDto.IssuanceRes> application(
        @ApiIgnore @CurrentUser CustomUser user,
        @ModelAttribute @Valid CardIssuanceDto.IssuanceReq request) throws Exception {
        if (log.isInfoEnabled()) {
            log.info("([ application ]) $user='{}' $dto='{}'", user, request);
        }

        SignatureHistory signatureHistory = issuanceService.verifySignedBinaryAndSave(user.idx(), request.getSignedBinaryString());
        issuanceService.issuance(user.idx(), request, signatureHistory.getIdx());

        return ResponseEntity.ok().build();
    }

}

