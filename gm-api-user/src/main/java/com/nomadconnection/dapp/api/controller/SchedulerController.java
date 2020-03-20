package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.service.SchedulerService;
import com.nomadconnection.dapp.api.service.ScrapingService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


@Slf4j
@RestController
@RequestMapping(SchedulerController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "스케쥴러", description = SchedulerController.URI.BASE)
@SuppressWarnings({"unused", "deprecation"})
public class SchedulerController {

    @SuppressWarnings("WeakerAccess")
    public static class URI {
        public static final String BASE = "/scheduler/v1";
        public static final String REGISTER = "/register";    // 스케쥴러 시작
        public static final String REMOVE = "/remove";    // 스케쥴러 중지
    }

    private final Boolean boolDebug = true;
    private final SchedulerService service;

    @ApiOperation(value = "스케쥴러 강제시작", notes = "" + "\n")
    @GetMapping(URI.REGISTER)
    public void scheduleRegister(@ApiIgnore @CurrentUser CustomUser user) throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("([scheduleRegister ]) $dto='{}'", user.idx());
        }
        service.register();
    }

    @ApiOperation(value = "스케쥴러 강제중지", notes = "" + "\n")
    @GetMapping(URI.REMOVE)
    public void scheduleRemove(@ApiIgnore @CurrentUser CustomUser user) throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("([scheduleRemove ]) $dto='{}'", user.idx());
        }
        service.remove();
    }
}
