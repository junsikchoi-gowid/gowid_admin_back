package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.BrandDto;
import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.api.service.EmailService;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping(UserEtcController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "회원관리", description = UserEtcController.URI.BASE)
public class UserEtcController {

    public static class URI {
        public static final String BASE = "/brand/v1";
        public static final String ACCOUNT = "/account";
        public static final String COMPANYCARD = "/companycard";
        public static final String USERDELETE = "/userdelete";
        public static final String USERPASSWORDCHANGE_AFTER = "/password/after";
        public static final String RECEPTION = "/reception";
        public static final String INDUCE_EMAIL = "/induceemail";
    }

    private final UserService service;
    private final EmailService emailService;

    @Deprecated
    @ApiOperation(value = "아이디(이메일) 찾기", notes = "" +
            "\n ### Remarks" +
            "\n" +
            "\n - <mark>액세스토큰 불필요</mark>" +
            "\n")
    @GetMapping(URI.ACCOUNT)
    public ResponseEntity Account(@ModelAttribute BrandDto.FindAccount dto) {
        if (log.isInfoEnabled()) {
            log.info("([ getAccount ]) $dto.account.find='{}'", dto);
        }
        return service.findAccount(dto.getName(), dto.getMdn());
    }

    @ApiOperation(value = "카드사(삼성/현대) 선택", notes = "" +
            "\n ### Remarks" +
            "\n")
    @PutMapping(URI.COMPANYCARD)
    public ResponseEntity CompanyCard(
            @ApiIgnore @CurrentUser CustomUser user,
            @ModelAttribute BrandDto.CompanyCard dto) {
        if (log.isInfoEnabled()) {
            log.info("([ getAccount ]) $dto.account.find='{}'", dto);
        }
        return service.companyCard(dto, user.idx());
    }

    @Deprecated
    @ApiOperation(value = "비밀번호 변경 - 로그인후", notes = "" +
            "\n ### Remarks" +
            "\n")
    @PostMapping(URI.USERPASSWORDCHANGE_AFTER)
    public ResponseEntity passwordAfter(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestBody BrandDto.PasswordAfter dto) {
        if (log.isInfoEnabled()) {
            log.info("([ passwordAfter ]) $user={}, $dto{}", user, dto);
        }
        return service.passwordAuthAfter(user.idx(), dto.getPrePassword(), dto.getAfterPassword());
    }


    @ApiOperation(value = "수신거부 등록", notes = "" +
            "\n ### Remarks" +
            "\n ")
    @GetMapping(URI.RECEPTION)
    public ResponseEntity saveReception(@RequestParam String key) {
        return service.saveReception(key);
    }

    @Deprecated
    @ApiOperation(value = "수신거부 삭제", notes = "" +
            "\n ### Remarks" +
            "\n")
    @DeleteMapping(URI.RECEPTION)
    public ResponseEntity deleteReception(@RequestParam String key) {
        return service.deleteReception(key);
    }

    @Deprecated
    @ApiOperation(value = "사용자 삭제 Email 정보 입력", notes = "" +
            "\n ### Remarks" +
            "\n")
    @DeleteMapping(URI.USERDELETE)
    public ResponseEntity deleteEmail(@RequestParam String email) {
        return service.deleteUserByEmail(email);
    }

    @ApiOperation(value = "메일 발송(포잉)", notes = "" +
            "\n ### Remarks" +
            "\n")
    @PutMapping(URI.INDUCE_EMAIL)
    public ResponseEntity<?> induceEmail(@RequestParam String email) {
        emailService.induceEmail(email);
        return new ResponseEntity<>(null,HttpStatus.OK);
    }

}
