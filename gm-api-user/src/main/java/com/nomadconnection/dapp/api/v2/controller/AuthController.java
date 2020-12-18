package com.nomadconnection.dapp.api.v2.controller;

import com.nomadconnection.dapp.api.dto.BrandDto;
import com.nomadconnection.dapp.api.enums.VerifyCode;
import com.nomadconnection.dapp.api.v2.dto.AuthDto;
import com.nomadconnection.dapp.api.v2.service.auth.AuthService;
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

import javax.validation.constraints.Email;

@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController("AuthV2Controller")
@RequestMapping(AuthController.URI.BASE)
@RequiredArgsConstructor
@Api(tags = "인증 v2", description = AuthController.URI.BASE)
@Validated
public class AuthController {

    public static class URI {
        public static final String BASE = "/auth/v2";
        public static final String SEND = "/send";
        public static final String VERIFY = "/verify";
        public static final String CHANGE_PASSWORD_BEFORE_LOGIN = "/password/before-login";
        public static final String CHANGE_PASSWORD_AFTER_LOGIN = "/password/after-login";
    }

    private final AuthService authService;

    @ApiOperation(value = "인증코드(4 digits, EMAIL) 발송 요청", notes = "" +
            "\n ### Remarks" +
            "\n" +
            "\n - <mark>액세스토큰 불필요</mark>" +
            "\n - 유효시간 : 605s" +
            "\n")
    @GetMapping(URI.SEND)
    public ResponseEntity<?> sendVerificationCode(@Email(message = "잘못된 이메일 형식입니다.") @RequestParam String email,
                                                  @RequestParam VerifyCode type) {
        log.info("([ sendVerificationCode ]) $email='{}' $type='{}'", email, type);
        return authService.sendVerificationCode(email, type);
    }

    @ApiOperation(value = "인증코드(4 digits, EMAIL) 확인", notes = "" +
            "\n ### Remarks" +
            "\n" +
            "\n - <mark>액세스토큰 불필요</mark>" +
            "\n - 인증 후 인증번호 삭제됨" +
            "\n"
    )
    @GetMapping(URI.VERIFY)
    public ResponseEntity<?> checkVerificationCode(@RequestParam String email, @RequestParam String code) {
        log.info("([ checkVerificationCode ]) $email='{}' $code='{}'", email, code);
        return authService.checkVerificationCode(email, code);
    }

    @ApiOperation(value = "비밀번호 변경 - 로그인전", notes = "" +
        "\n ### Remarks" +
        "\n - <mark>액세스토큰 불필요</mark>" +
        "\n - 변경 후 인증번호 삭제됨" +
        "\n")
    @PostMapping(URI.CHANGE_PASSWORD_BEFORE_LOGIN)
    public ResponseEntity changePasswordPre(
        @RequestBody AuthDto.PasswordBeforeLogin dto) {
        log.info("([ passwordPre ]) $dto={}", dto);
        return authService.changePasswordBeforeLogin(dto);
    }

    @ApiOperation(value = "비밀번호 변경 - 로그인후")
    @PostMapping(URI.CHANGE_PASSWORD_AFTER_LOGIN)
    public ResponseEntity changePasswordAfter(
        @ApiIgnore @CurrentUser CustomUser user,
        @RequestBody AuthDto.PasswordAfterLogin dto) {
        log.info("([ passwordAfter ]) $user={}, $dto{}", user, dto);
        return authService.changePasswordAfterLogin(user.idx(), dto);
    }


}
