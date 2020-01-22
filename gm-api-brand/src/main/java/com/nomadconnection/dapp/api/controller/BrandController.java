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
import org.springframework.security.crypto.password.PasswordEncoder;
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
        public static final String USERDELETE = "/userdelete";
        public static final String USERPASSWORDCHANGE_PRE = "/password/pre";
        public static final String USERPASSWORDCHANGE_AFTER = "/password/after";
    }

    private final BrandService service;

    @ApiOperation(value = "아이디(이메일) 찾기", notes = "" +
            "\n ### Remarks" +
            "\n" +
            "\n - <mark>액세스토큰 불필요</mark>" +
            "\n", tags = "1. 브랜드")
    @GetMapping(URI.ACCOUNT)
    public ResponseEntity Account(@ModelAttribute BrandDto.FindAccount dto) {
        if (log.isDebugEnabled()) {
            log.debug("([ getAccount ]) $dto.account.find='{}'", dto);
        }
        return service.findAccount(dto.getName(), dto.getMdn());
    }

    @ApiOperation(value = "카드사(삼성/현대) 선택", notes = "" +
            "\n ### Remarks" +
            "\n", tags = "1. 브랜드")
    @GetMapping(URI.COMPANYCARD)
    public ResponseEntity CompanyCard(
            @ApiIgnore @CurrentUser CustomUser user,
            @ModelAttribute BrandDto.CompanyCard dto) {
        if (log.isDebugEnabled()) {
            log.debug("([ getAccount ]) $dto.account.find='{}'", dto);
        }
        return service.companyCard(dto, user.idx());
    }

    @ApiOperation(value = "사용자 삭제 Email 정보 입력", notes = "" +
            "\n ### Remarks" +
            "\n", tags = "1. 브랜드")
    @GetMapping(URI.USERDELETE)
    public ResponseEntity deleteEmail(@RequestParam String email) {
        return service.deleteEmail(email);
    }


    @ApiOperation(value = "비밀번호 변경 - 로그인전", notes = "" +
            "\n ### Remarks" +
            "\n 로그인후 인증번호 삭제됨" +
            "\n", tags = "1. 브랜드")
    @PostMapping(URI.USERPASSWORDCHANGE_PRE)
    public ResponseEntity passwordPre(
            @ModelAttribute BrandDto.PasswordPre dto)
    {
        return service.passwordAuthPre(dto.getEmail(), dto.getCode(), dto.getPassword());
    }

    @ApiOperation(value = "비밀번호 변경 - 로그인후", notes = "" +
            "\n ### Remarks" +
            "\n", tags = "1. 브랜드")
    @PostMapping(URI.USERPASSWORDCHANGE_AFTER)
    public ResponseEntity passwordAfter(
            @ApiIgnore @CurrentUser CustomUser user,
            @ModelAttribute BrandDto.PasswordAfter dto
    ){
        return service.passwordAuthAfter(user.idx(), dto.getPrePassword(), dto.getAfterPassword());
    }
}
