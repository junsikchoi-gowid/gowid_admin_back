package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.AccountDto;
import com.nomadconnection.dapp.api.dto.AuthDto;
import com.nomadconnection.dapp.api.service.AuthService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.dto.response.ErrorResponse;
import com.nomadconnection.dapp.core.security.CustomUser;
import com.nomadconnection.dapp.jwt.dto.TokenDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(AuthController.URI.BASE)
@RequiredArgsConstructor
@Api(tags = "인증", description = AuthController.URI.BASE)
@Validated
@SuppressWarnings({"unused", "deprecation"})
public class AuthController {

    public static class URI {
        public static final String BASE = "/auth/v1";
        public static final String EXISTS = "/exists";
        public static final String VERIFICATION_CODE = "/verificationCode";
        public static final String SEND_VERIFICATION_CODE = "/sendVerificationCode";
        public static final String CHECK_VERIFICATION_CODE = "/checkVerificationCode";
        public static final String ACCOUNT = "/account";
        public static final String PASSWORD_RESET_EMAIL = "/passwordResetEmail";
        public static final String PASSWORD = "/password";
        public static final String TOKEN_ISSUE = "/token/issue";
        public static final String TOKEN_REISSUE = "/token/reissue";
        public static final String INFO = "/info";
    }

    private final AuthService service;

    //==================================================================================================================
    //
    //	아이디(이메일) 존재여부 확인
    //
    //==================================================================================================================

    @ApiOperation(value = "아이디(이메일) 존재여부 확인", notes = "" +
            "\n ### Remarks" +
            "\n" +
            "\n - <mark>액세스토큰 불필요</mark>" +
            "\n - 존재하지 않는 아이디(이메일) 요청 시, <mark>404(NOT FOUND)</mark> 응답" +
            "\n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value="아이디(이메일)")
    })
    @GetMapping(URI.EXISTS)
    public ResponseEntity exists(@RequestParam String account) {
        if (log.isDebugEnabled()) {
            log.debug("([ exists ]) $account='{}'", account);
        }

        if (!service.isPresent(account)) {
            // return ResponseEntity.notFound().build();
            return ResponseEntity.ok().body(
                    BusinessResponse.builder()
                            .normal(BusinessResponse.Normal.builder()
                                    .status(false)
                                    .value("notFound")
                                    .build()).build()
            );
        }

        return ResponseEntity.ok().body(
                BusinessResponse.builder().build()
        );
    }

    //==================================================================================================================
    //
    //	인증코드(4 digits, SMS/EMAIL) 발송 요청
    //
    //==================================================================================================================

    @ApiOperation(value = "인증코드(4 digits, SMS/EMAIL) 발송 요청", notes = "" +
            "\n ### Remarks" +
            "\n" +
            "\n - 인증문자는 미구현(개발 시 이메일주소를 당분간 사용)" +
            "\n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "연락처(폰) or 이메일")
    })
    @GetMapping(URI.VERIFICATION_CODE)
    public ResponseEntity verificationCode(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestParam String key) {
        if (log.isDebugEnabled()) {
            log.debug("([ verificationCode ]) $user='{}', $key='{}'", user, key);
        }
        if (!service.sendVerificationCode(key)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    //==================================================================================================================
    //
    //	인증코드(4 digits, EMAIL) 발송 요청
    //
    //==================================================================================================================

    @ApiOperation(value = "인증코드(4 digits, EMAIL) 발송 요청", notes = "" +
            "\n ### Remarks" +
            "\n" +
            "\n - <mark>액세스토큰 불필요</mark>" +
            "\n - 인증메일 발송 실패: <mark>500(INTERNAL SERVER ERROR)</mark>" +
            "\n", tags = "1. 브랜드")
    @GetMapping(URI.SEND_VERIFICATION_CODE)
    public ResponseEntity sendVerificationCode(@Email(message = "잘못된 이메일 형식입니다.") @RequestParam String email) {
        if (log.isDebugEnabled()) {
            log.debug("([ sendVerificationCode ]) $email='{}'", email);
        }
        try {
            if (!service.sendVerificationCode(email)) {
                // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                return ResponseEntity.ok().body(BusinessResponse.builder().normal(
                        BusinessResponse.Normal.builder()
                                .status(false)
                                .value(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                                .build()
                ).build());
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("([ sendVerificationCode ]) FAILED TO SEND VERIFICATION CODE, $email='{}', $exception='{} => {}'",
                        email,
                        e.getClass().getSimpleName(), e.getMessage(), e);
            }
            // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            return ResponseEntity.ok().body(BusinessResponse.builder().normal(
                    BusinessResponse.Normal.builder()
                            .status(false)
                            .value(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                            .build()
            ).build());
        }
        return ResponseEntity.ok().body(BusinessResponse.builder().build());
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
            "\n")
    @GetMapping(URI.CHECK_VERIFICATION_CODE)
    public ResponseEntity checkVerificationCode(@RequestParam String key, @RequestParam String code) {
        if (log.isDebugEnabled()) {
            log.debug("([ checkVerificationCode ]) $key='{}', $code='{}'", key, code);
        }
        if (!service.checkVerificationCode(key, code)) {
            return ResponseEntity.ok()
                    .body(ErrorResponse.from(ErrorCode.Mismatched.MISMATCHED_VERIFICATION_CODE));
        }
        return ResponseEntity.ok().body(
                BusinessResponse.builder().build()
        );
    }

    //==================================================================================================================
    //
    //	아이디(이메일) 찾기: 이름, 연락처(폰)
    //
    //==================================================================================================================

    @ApiOperation(value = "아이디(이메일) 찾기", notes = "" +
            "\n ### Remarks" +
            "\n" +
            "\n - <mark>액세스토큰 불필요</mark>" +
            "\n")
    @GetMapping(URI.ACCOUNT)
    public List<String> getAccount(@ModelAttribute AccountDto.FindAccount dto) {
        if (log.isDebugEnabled()) {
            log.debug("([ getAccount ]) $dto.account.find='{}'", dto);
        }
        return service.findAccount(dto.getName(), dto.getMdn());
    }

    //==================================================================================================================
    //
    //	비밀번호 재설정
    //
    //==================================================================================================================

    @ApiOperation(value = "비밀번호 재설정 - 이메일 발송 요청", notes = "" +
            "\n ### Remarks" +
            "\n" +
            "\n - <mark>액세스토큰 불필요</mark>" +
            "\n - <mark>개발서버에서는 링크대신 인증키를 메일로 발송</mark>" +
            "\n" +
            "\n ### Errors" +
            "\n" +
            "\n - 404 NOT FOUND: 등록되지 않은 이메일" +
            "\n 	- <pre>{ \"error\": \"USER_NOT_FOUND\" }</pre>" +
            "\n")
    @PostMapping(URI.PASSWORD_RESET_EMAIL)
    public ResponseEntity sendPasswordResetEmail(
            @RequestParam String email
    ) {
        if (log.isDebugEnabled()) {
            log.debug("([ sendPasswordResetEmail ]) $email='{}'", email);
        }
        service.sendPasswordResetEmail(email);
        // return ResponseEntity.ok().build();
        return ResponseEntity.ok().body(ErrorResponse.from("","정상처리"));
    }

    //	비밀번호 재설정 - 새 비밀번호 설정: 인증키, 새 비밀번호

    @ApiOperation(value = "비밀번호 재설정 - 새 비밀번호 설정", notes = "" +
            "\n ### Remarks" +
            "\n" +
            "\n - <mark>액세스토큰 불필요</mark>" +
            "\n - <mark>인증키</mark>" +
            "\n")
    @PutMapping(URI.PASSWORD)
    public ResponseEntity putPassword(
            @RequestBody AccountDto.PasswordReset dto
    ) {
        if (log.isDebugEnabled()) {
            log.debug("([ putPassword ]) $dto='{}'", dto);
        }
        service.resetPassword(dto.getKey(), dto.getPassword());
        return ResponseEntity.ok().build();
    }

    //==================================================================================================================
    //
    //	인증토큰
    //
    //==================================================================================================================

    @ApiOperation(value = "인증토큰 - 발급", notes = "" +
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
            "\n")
    @PostMapping(URI.TOKEN_ISSUE)
    public TokenDto.TokenSet issueTokenSet(
            @RequestBody AccountDto dto
    ) {
        if (log.isDebugEnabled()) {
            log.debug("([ issueTokenSet ]) $dto='{}'", dto);
        }
        return service.issueTokenSet(dto);
    }

    @ApiOperation(value = "인증토큰 - 갱신", notes = "" +
            "\n ### Remarks" +
            "\n" +
            "\n - <mark>인증토큰(갱신)이 유효한 경우, 인증토큰(액세스)을 계속 갱신할 수 있음</mark>" +
            "\n - <mark>인증토큰(갱신)이 만료되는 경우, 아이디/패스워드로 다시 인증토큰을 발급받아야 함</mark>" +
            "\n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "아이디(이메일)"),
            @ApiImplicitParam(name = "jwt", value = "갱신토큰")
    })
    @PostMapping(URI.TOKEN_REISSUE)
    public TokenDto.Token reissueAccessToken(
            @RequestParam String email,
            @RequestParam String jwt
    ) {
        if (log.isDebugEnabled()) {
            log.debug("([ reissueAccessToken ]) $email='{}'", email);
        }
        return service.reissueAccessToken(email, jwt);
    }

    //==================================================================================================================
    //
    //	정보 조회
    //
    //==================================================================================================================

    @ApiOperation(value = "인증토큰에 매핑된 사용자 정보 조회", notes = "" +
            "\n ### Remarks" +
            "\n" +
            "\n - 식별자(사용자)" +
            "\n - 이메일(계정)" +
            "\n - 이름" +
            "\n - 연락처(폰)" +
            "\n - 식별자(법인)" +
            "\n - 수령지(법인)" +
            "\n")
    @GetMapping(URI.INFO)
    public AuthDto.AuthInfo getAuthInfo(
            @ApiIgnore @CurrentUser CustomUser user
    ) {
        if (log.isDebugEnabled()) {
            log.debug("([ getAuthInfo ]) $user='{}'", user);
        }
        return service.info(user.idx());
    }

    //==================================================================================================================
    //
    //	인증코드(4 digits, EMAIL) 발송 요청
    //
    //==================================================================================================================

    @ApiOperation(value = "인증코드(4 digits, EMAIL) 발송 요청", notes = "" +
            "\n ### Remarks" +
            "\n" +
            "\n - <mark>액세스토큰 불필요</mark>" +
            "\n - 인증메일 발송 실패: <mark>500(INTERNAL SERVER ERROR)</mark>" +
            "\n")
    @PostMapping(URI.SEND_VERIFICATION_CODE)
    public ResponseEntity sendMailVerificationCode(@RequestBody AccountDto dto) {
        if (log.isDebugEnabled()) {
            log.debug("([ sendVerificationCode ]) $email='{}'", dto.getEmail());
        }
        try {
            if (!service.sendEmailVerificationCode(dto)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("([ sendVerificationCode ]) FAILED TO SEND VERIFICATION CODE, $email='{}', $exception='{} => {}'",
                        dto.getEmail(),
                        e.getClass().getSimpleName(), e.getMessage(), e);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().body(ErrorResponse.from("success","정상처리"));
    }
}
