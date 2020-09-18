package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.BrandFaqDto;
import com.nomadconnection.dapp.api.service.FaqService;
import com.nomadconnection.dapp.core.annotation.ApiPageable;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping(FaqController.URI.BASE)
@RequiredArgsConstructor
@Api(tags = "회원관리", description = FaqController.URI.BASE)
public class FaqController {

    public static class URI {
        public static final String BASE = "/brand/v1";
        public static final String FAQ = "/faq";
        public static final String FAQ_SAVE = "/faqsave";
        public static final String FAQ_DEL = "/faqdel";
    }

    private final FaqService service;

    @ApiOperation(
            value = "Brand 문의하기 조회",
            notes = "### Remarks \n - <mark>액세스토큰 불필요</mark>"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "정상"),
            @ApiResponse(code = 500, message = "")
    })
    @ApiPageable
    @GetMapping(URI.FAQ)
    public Page<BrandFaqDto> getConsents(
            @ModelAttribute BrandFaqDto dto,
            Pageable pageable
    ) {
        if (log.isInfoEnabled()) {
            log.info("([ getConsents ]) BFaqDtoController FaqList $dto={}", dto);
        }

        return service.faqs(dto, pageable);
    }

    //==================================================================================================================
    //
    //	문의하기 등록
    //
    //==================================================================================================================

    @ApiOperation(value = "Brand 문의하기 저장",
            notes = "### Remarks \n - <mark>액세스토큰 불필요</mark>")
    @PostMapping(URI.FAQ_SAVE)
    public ResponseEntity faqSave(
            @RequestBody BrandFaqDto dto
    ) {
        if (log.isInfoEnabled()) {
            log.info("([ faqSave ]) $dto='{}'", dto);
        }

        return service.faqSave(dto);
    }

    //==================================================================================================================
    //
    //	새로운 정보 등록: 부서명
    //
    //==================================================================================================================

    @ApiOperation(value = "Brand 문의하기 삭제",
            notes = "### Remarks \n - <mark>액세스 필요</mark> \n - <mark> 마스터권한 필요</mark>")
    @DeleteMapping(URI.FAQ_DEL)
    public ResponseEntity faqDel(
            @ApiIgnore @CurrentUser org.springframework.security.core.userdetails.User user,
            @RequestParam List<Long> faqIds
    ) {
        if (log.isInfoEnabled()) {
            log.info("([ faqDel ]) $faqIds='{}'", faqIds);
        }

        return service.faqDel(faqIds, user);
    }
}
