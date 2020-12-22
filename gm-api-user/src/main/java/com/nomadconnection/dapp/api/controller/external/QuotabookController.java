package com.nomadconnection.dapp.api.controller.external;

import com.nomadconnection.dapp.api.dto.external.quotabook.RoundingReq;
import com.nomadconnection.dapp.api.dto.external.quotabook.RoundingRes;
import com.nomadconnection.dapp.api.dto.external.quotabook.ShareClassesRes;
import com.nomadconnection.dapp.api.dto.external.quotabook.StakeholdersRes;
import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.service.external.QuotabookService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(allowCredentials = "true")
@RequestMapping(QuotabookController.URI.BASE)
@Api(tags = "Quotabook 연동", description = QuotabookController.URI.BASE)
public class QuotabookController {

    public static class URI {
        public static final String BASE = "/quotabook/v1";
        public static final String STAKEHOLDERS = "/stakeholders";
        public static final String SHARE_CLASSES = "/share-classes";
        public static final String ROUNDING = "/rounding";
    }

    private final QuotabookService quotabookService;

    @ApiOperation(value = "주주 명부 조회", notes = "" + "\n")
    @GetMapping(URI.STAKEHOLDERS)
    public ApiResponse<StakeholdersRes> getShareHolders(@ApiIgnore @CurrentUser CustomUser user) {
        StakeholdersRes stakeholdersRes = quotabookService.getStakeHolders(user);
        return ApiResponse.SUCCESS(stakeholdersRes);
    }

    @ApiOperation(value = "주식 유형별 분포", notes = "" + "\n")
    @GetMapping(URI.SHARE_CLASSES)
    public ApiResponse<ShareClassesRes> getShareClasses(@ApiIgnore @CurrentUser CustomUser user) {
        ShareClassesRes response = quotabookService.getShareClasses(user);
        return ApiResponse.SUCCESS(response);
    }

    @ApiOperation(value = "펀딩 내역", notes = "" + "\n")
    @GetMapping(URI.ROUNDING)
    public ApiResponse<RoundingRes> getRounding(@ApiIgnore @CurrentUser CustomUser user,
                                                @RequestParam(value = "current", required = false) Long current,
                                                @RequestParam(value = "pageSize", required = false) Long pageSize
    ) {
        RoundingRes stakeholdersRes = quotabookService.getRounding(user, RoundingReq.builder()
                .current(current)
                .pageSize(pageSize)
                .build());
        return ApiResponse.SUCCESS(stakeholdersRes);
    }

}
