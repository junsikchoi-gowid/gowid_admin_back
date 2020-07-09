package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.BrandConsentDto;
import com.nomadconnection.dapp.api.service.ConsentService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping(ConsentController.URI.BASE)
@RequiredArgsConstructor
@Api(tags = "회원관리", description = ConsentController.URI.BASE)
public class ConsentController {

    public static class URI {
        public static final String BASE = "/brand/v1";
        public static final String CONSENT = "/consent";
        public static final String CONSENT_SAVE = "/consentsave";
        public static final String CONSENT_DEL = "/consentdel/{consent}";
    }

    private final ConsentService service;

    @ApiOperation(
            value = "Brand 이용약관 조회",
            notes = "### Remarks \n - <mark>액세스토큰 불필요</mark>"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "정상"),
            @ApiResponse(code = 201, message = "생성"),
            @ApiResponse(code = 401, message = "권한없음(패스워드 불일치)"),
            @ApiResponse(code = 403, message = "권한없음(패스워드 불일치)"),
            @ApiResponse(code = 404, message = "등록되지 않은 이메일"),
            @ApiResponse(code = 500, message = "")
    })
    @GetMapping(URI.CONSENT)
    public ResponseEntity consents(@RequestParam(required = false) String typeCode) {
        if (log.isDebugEnabled()) {
            log.debug("getConsents List");
        }

        return service.consents(typeCode);
    }

    //==================================================================================================================
    //
    //	새로운 정보 등록: 부서명
    //
    //==================================================================================================================

    @ApiOperation(value = "Brand 이용약관 등록",
            notes = "### Remarks \n - <mark>액세스 필요</mark> \n - <mark> 마스터권한 필요</mark>")
    @PostMapping(URI.CONSENT_SAVE)
    public ResponseEntity consentSave(
            @ApiIgnore @CurrentUser org.springframework.security.core.userdetails.User user,
            @RequestBody BrandConsentDto dto
    ) {
        if (log.isDebugEnabled()) {
            log.debug("([ postConsent ]) $user='{}', $dto='{}' ", user, dto);
        }

        return service.postConsent(user, dto);
    }

    //==================================================================================================================
    //
    //	새로운 정보 등록: 부서명
    //
    //==================================================================================================================

    @ApiOperation(value = "Brand 이용약관 삭제",
            notes = "### Remarks \n - <mark>액세스 필요</mark> \n - <mark> 마스터권한 필요</mark>")
    @DeleteMapping(URI.CONSENT_DEL)
    public ResponseEntity putConsentDel(
            @ApiIgnore @CurrentUser org.springframework.security.core.userdetails.User user,
            @PathVariable Long consent
    ) {
        if (log.isDebugEnabled()) {
            log.debug("([ postConsent ]) $user='{}', $dto='{}' ", user, consent);
        }

        return service.consentDel(user, consent);
    } 
}
