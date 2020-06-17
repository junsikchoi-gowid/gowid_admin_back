package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.KcbDto;
import com.nomadconnection.dapp.api.service.KcbService;
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
import java.io.IOException;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping(KcbController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "kcb 휴대폰 인증", description = KcbController.URI.BASE)
public class KcbController {

    public static class URI {
        public static final String BASE = "/kcb/v1";
        public static final String CERT = "/cert";
        public static final String SMS = "/sms";
    }

    private final KcbService service;

    @ApiOperation("본인인증 요청")
    @PostMapping(URI.SMS)
    public ResponseEntity<KcbDto.Response> authenticationSms(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestBody @Valid KcbDto.Authentication dto) throws IOException {
        if (log.isInfoEnabled()) {
            log.info("([ authenticationSms ]) $user='{}', $dto='{}'", user, dto);
        }

        return ResponseEntity.ok().body(service.authenticationSms(dto));
    }

    @ApiOperation("본인인증 확인")
    @PostMapping(URI.CERT)
    public ResponseEntity<KcbDto.Response> cert(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestBody @Valid KcbDto.Cert dto) throws IOException {
        if (log.isInfoEnabled()) {
            log.info("([ cert ]) $user='{}', $dto='{}'", user, dto);
        }

        return ResponseEntity.ok().body(service.certSms(dto));
    }
}
