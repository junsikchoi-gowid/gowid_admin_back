package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.service.ScrapingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping(ScrapingController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "스크래핑", description = ScrapingController.URI.BASE)
@SuppressWarnings({"unused", "deprecation"})
public class ScrapingController {

    @SuppressWarnings("WeakerAccess")
    public static class URI {
        public static final String BASE = "/batch/v1";
        public static final String JUST_ACCOUNT = "/account";    // 입출금 거래내역
        public static final String SCRAPING_ACCOUNT = "/account-all";    // 은행 기업 보유계좌 + 거래내역
        public static final String SCRAPING_ACCOUNT_HISTORY = "/account-history";    // 입출금 거래내역

    }

    private final Boolean boolDebug = true;
    private final ScrapingService service;

    @ApiOperation(value = "최근 1년 거래내역 가져오기", notes = "" + "\n")
    @PostMapping(URI.SCRAPING_ACCOUNT)
    public boolean scrapingRegister(@RequestBody BankDto.AccountBatch dto) throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("([AccountTransactionList ]) $dto='{}'", dto);
        }
        return service.aWaitcrapingRegister1YearAll(dto.getUserIdx());
    }

    @ApiOperation(value = "10년간 데이터 가져오기", notes = "" + "\n")
    @PostMapping(URI.SCRAPING_ACCOUNT_HISTORY)
    public boolean scrapingRegisterAll(@RequestParam Long idxUser) throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("([AccountTransactionList ]) $dto='{}'", idxUser);
        }
        return service.aWaitScraping10Years(idxUser);
    }

    @ApiOperation(value = "계좌만", notes = "" + "\n")
    @GetMapping(URI.JUST_ACCOUNT)
    public void scrapingRegisterAccount(@RequestParam Long idxUser) throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("([AccountTransactionList ]) $dto='{}'", idxUser);
        }
        service.scrapingAccount(idxUser, (long) 1);
    }
}
