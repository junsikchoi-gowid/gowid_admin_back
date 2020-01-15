package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.BrandDto;
import com.nomadconnection.dapp.api.service.BrandService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController
@RequestMapping(BrandController.URI.BASE)
@RequiredArgsConstructor
@Api(tags = "사용자", description = BrandController.URI.BASE)
@SuppressWarnings({"unused", "deprecation"})
public class BrandController {

    @SuppressWarnings("WeakerAccess")
    public static class URI {
        public static final String BASE = "/brand/v1";
        public static final String ACCOUNT = "/account";
        public static final String COMPANYCARD = "/companycard";
    }

    private final BrandService service;

    @ApiOperation(value = "아이디(이메일) 찾기", notes = "" +
            "\n ### Remarks" +
            "\n" +
            "\n - <mark>액세스토큰 불필요</mark>" +
            "\n")
    @GetMapping(URI.ACCOUNT)
    public ResponseEntity Account(@ModelAttribute BrandDto.FindAccount dto) {
        if (log.isDebugEnabled()) {
            log.debug("([ getAccount ]) $dto.account.find='{}'", dto);
        }
        return service.findAccount(dto.getName(), dto.getMdn());
    }

    @ApiOperation(value = "카드사(삼성/현대) 선택", notes = "" +
            "\n ### Remarks" +
            "\n")
    @GetMapping(URI.COMPANYCARD)
    public ResponseEntity CompanyCard(
            @ApiIgnore @CurrentUser CustomUser user,
            @ModelAttribute BrandDto.CompanyCard dto) {
        if (log.isDebugEnabled()) {
            log.debug("([ getAccount ]) $dto.account.find='{}'", dto);
        }
        return service.companyCard(dto, user.idx());
    }
}
