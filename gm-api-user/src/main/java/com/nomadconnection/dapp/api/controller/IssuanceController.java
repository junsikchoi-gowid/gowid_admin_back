package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.shinhan.ui.IssuanceDto;
import com.nomadconnection.dapp.api.dto.shinhan.ui.UiResponse;
import com.nomadconnection.dapp.api.service.shinhan.IssuanceService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController
@RequestMapping(IssuanceController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "카드 발급")
public class IssuanceController {

    @NonNull
    private final IssuanceService issuanceService;

    public static class URI {
        public static final String BASE = "/issuance/v1";
    }

    /**
     * todo :
     * 1) request, response 정의
     * 2) 예외 처리
     * 3) 제네릭 타입 적용
     */
    @ApiOperation(value = "법인카드 발급 신청", notes = "" +
            "\n ### Remarks" +
            "\n")
    @PostMapping
    public UiResponse application(
            @ApiIgnore @CurrentUser CustomUser user,
            @RequestBody IssuanceDto request) {

        return issuanceService.application(request.getBusinessLicenseNo());

    }
}
