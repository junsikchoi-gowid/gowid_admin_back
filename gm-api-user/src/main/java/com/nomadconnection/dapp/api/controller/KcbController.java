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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(KcbController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "법인카드 발급", description = KcbController.URI.BASE)
public class KcbController {

    @SuppressWarnings("WeakerAccess")
    public static class URI {
        public static final String BASE = "/kcb/v1";
        public static final String CERT = "/cert";
    }

    private final KcbService service;

    @ApiOperation("법인정보 등록")
    @PostMapping(URI.CERT)
    public ResponseEntity cert(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestBody @Valid KcbDto.Cert dto) throws IOException {
        if (log.isInfoEnabled()) {
            log.info("([ registerCorporation ]) $user='{}', $dto='{}'", user, dto);
        }

        return ResponseEntity.ok().body(service.certSms(dto));
    }
}
