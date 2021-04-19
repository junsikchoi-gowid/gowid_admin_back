package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.service.EmailService;
import com.nomadconnection.dapp.api.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        public static final String RECEPTION = "/reception";
        public static final String INDUCE_EMAIL = "/induceemail";
        public static final String INDUCE_EMAIL_MOBILE = "/induce-email-mobile";
    }

    private final UserService service;
    private final EmailService emailService;


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

    @ApiOperation(value = "메일 발송(포잉)", notes = "" +
            "\n ### Remarks" +
            "\n")
    @PutMapping(URI.INDUCE_EMAIL)
    public ResponseEntity<?> induceEmail(@RequestParam String email) {
        emailService.induceEmail(email);
        return ResponseEntity.ok().build();
    }


    @ApiOperation(value = "가입 안내 메일", notes = "" +
            "\n ### Remarks" +
            "\n")
    @PutMapping(URI.INDUCE_EMAIL_MOBILE)
    public ResponseEntity<Boolean> induceEmailMobile(@RequestParam String email) {
        emailService.induceEmailMobile(email);
        return ResponseEntity.ok().build();
    }
}
