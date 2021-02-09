package com.nomadconnection.dapp.api.v2.controller.auth;

import com.nomadconnection.dapp.api.dto.AccountDto;
import com.nomadconnection.dapp.api.enums.VerifyCode;
import com.nomadconnection.dapp.api.v2.dto.AuthDto;
import com.nomadconnection.dapp.api.v2.service.auth.AuthService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import com.nomadconnection.dapp.jwt.dto.TokenDto;
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
        public static final String TOKEN_ISSUE = "/token/issue";
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
            "\n - 인증 후 REGISTER 일 때, 인증번호 삭제됨" +
            "\n"
    )
    @GetMapping(URI.VERIFY)
    public ResponseEntity<?> checkVerificationCode(@RequestParam String email, @RequestParam String code, @RequestParam VerifyCode verifyType) {
        log.info("([ checkVerificationCode ]) $email='{}' $code='{}'", email, code);
        return authService.checkVerificationCode(email, code, verifyType);
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

    @ApiOperation(value = "토큰 발급(v2)", notes = "" +
            "\n ### Remarks" +
            "\n" +
            "\n - JWT 사용" +
            "\n   - 발급된 토큰 정보를 서버에 저장하지 않음" +
            "\n   - 만료되기 전까지 사용가능하며, 타 기기에서의 다중 로그인도 가능함" +
            "\n   - 개인식별번호(PIN)를 사용한 인증이 어떻게 처리되는지 확인 필요함" +
            "\n     - 최초 로그인 시 인증토큰을 발급하고, 이후 등록된 PIN/TOUCH_ID 인증 통과시 발급된 인증토큰을 사용하는 방식인가?" +
            "\n   - <mark>일단은 현 상태로 두고, 기획의도 확인 후 수정 예정</mark>" +
            "\n - 인증토큰(액세스): 발급 10분 후 만료(현재는 이렇게 되어 있음)" +
            "\n - 인증토큰(갱신): 발급 7일 후 만료(현재는 이렇게 되어 있음)" +
            "\n - 로그인 실패시, 해당 유저가 지출관리 앱 사용자일 경우 별도 결과코드 리턴. " +
            "\n")
    @PostMapping(URI.TOKEN_ISSUE)
    public TokenDto.TokenSet issueTokenSet(
            @RequestBody AccountDto dto
    ) {
        if (log.isInfoEnabled()) {
            log.info("([ issueTokenSet ]) $dto='{}'", dto);
        }
        return authService.issueTokenSet(dto);
    }

}
