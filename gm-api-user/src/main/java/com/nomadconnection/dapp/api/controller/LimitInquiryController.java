package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.LimitInquiryDto;
import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.service.LimitInquiryService;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@CrossOrigin(allowCredentials = "true")
@RequiredArgsConstructor
@RequestMapping(LimitInquiryController.URI.BASE)
@Api(tags = "한도조회", description = LimitInquiryController.URI.BASE)
public class LimitInquiryController {

    public static class URI {
        public static final String BASE = "/limit-inquiry";
    }

    private final LimitInquiryService limitInquiryService;


    @ApiOperation(value = "한도 조회 요청내용 저장", notes = "" +
            "\n ### Remarks" +
            "\n")
    @PostMapping
    public ApiResponse<?> saveLimitInquiry(@Valid @RequestBody LimitInquiryDto request) {
        limitInquiryService.saveLimitInquiry(request);
        return ApiResponse.builder()
                .result(ApiResponse.ApiResult.builder()
                        .code(ErrorCode.Api.SUCCESS.getCode())
                        .build())
                .build();
    }
}
