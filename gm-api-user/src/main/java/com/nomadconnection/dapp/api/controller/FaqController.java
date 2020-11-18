package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.BrandFaqDto;
import com.nomadconnection.dapp.api.service.FaqService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping(FaqController.URI.BASE)
@RequiredArgsConstructor
@Api(tags = "회원관리", description = FaqController.URI.BASE)
public class FaqController {

    public static class URI {
        public static final String BASE = "/brand/v1";
        public static final String FAQ_SAVE = "/faqsave";
    }

    private final FaqService service;

    //==================================================================================================================
    //
    //	문의하기 등록
    //
    //==================================================================================================================

    @ApiOperation(value = "문의하기 저장",
            notes = "### Remarks \n - <mark>액세스토큰 불필요</mark>")
    @PostMapping(URI.FAQ_SAVE)
    public ResponseEntity faqSave(
            @RequestBody BrandFaqDto dto
    ) {
        if (log.isInfoEnabled()) {
            log.info("([ faqSave ]) $dto='{}'", dto);
        }

        return service.faqSend(dto);
    }
}