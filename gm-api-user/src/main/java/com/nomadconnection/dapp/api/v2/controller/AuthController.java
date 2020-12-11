package com.nomadconnection.dapp.api.v2.controller;

import com.nomadconnection.dapp.api.enums.VerifyCode;
import com.nomadconnection.dapp.api.v2.service.auth.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;

@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController("AuthV2Controller")
@RequestMapping(AuthController.URI.BASE)
@RequiredArgsConstructor
@Api(tags = "인증", description = AuthController.URI.BASE)
@Validated
public class AuthController {

    public static class URI {
        public static final String BASE = "/auth/v2";
        public static final String SEND = "/send";
        public static final String VERIFY = "/verify";
    }

    private final AuthService authService;

    @ApiOperation(value = "인증코드(4 digits, EMAIL) 발송 요청", notes = "" +
            "\n ### Remarks" +
            "\n" +
            "\n - <mark>액세스토큰 불필요</mark>" +
            "\n - 인증메일 발송 실패: <mark>500(INTERNAL SERVER ERROR)</mark>" +
            "\n")
    @GetMapping(URI.SEND)
    public ResponseEntity<?> sendVerificationCode(@Email(message = "잘못된 이메일 형식입니다.") @RequestParam String email,
                                                  @RequestParam VerifyCode type) {

        log.info("([ sendVerificationCode ]) $email='{}' $type='{}'", email, type);
        return authService.sendVerificationCode(email, type);
    }

    //==================================================================================================================
    //
    //	인증코드(4 digits, SMS/EMAIL) 확인
    //
    //==================================================================================================================

    @ApiOperation(value = "인증코드(4 digits, EMAIL) 확인", notes = "" +
            "\n ### Remarks" +
            "\n" +
            "\n - <mark>액세스토큰 불필요</mark>" +
            "\n - 현재는 인증코드의 만료시간이 없음" +
            "\n - 확인에 성공하는 경우, 인증코드 삭제됨" +
            "\n - 확인성공: 200 OK" +
            "\n - <s>확인실패: 400 BAD REQUEST</s>" +
            "\n - code 인증번호(4 digits)" +
            "\n - key 연락처(폰) or 메일주소" +
            "\n ### Errors" +
            "\n" +
            "\n - 200 OK: " +
            "\n 	- <pre>{ \"error\": \"MISMATCHED_VERIFICATION_CODE\" }</pre>" +
            "\n"
    )
    @GetMapping(URI.VERIFY)
    public ResponseEntity<?> checkVerificationCode(@RequestParam String email, @RequestParam String code) {
        log.info("([ checkVerificationCode ]) $email='{}' $code='{}'", email, code);
        return authService.checkVerificationCode(email, code);
    }

}
