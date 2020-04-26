package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.service.ScrapingService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


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
        public static final String STOP = "/stop";    // 입출금 거래내역
        public static final String SCRAPING_ACCOUNT = "/account-all";    // 은행 기업 보유계좌 + 거래내역
        public static final String SCRAPING_ACCOUNT_HISTORY = "/account-history";    // 입출금 거래내역
        public static final String SCRAPING_ACCOUNT_ID = "/account/id";    // 계좌별 조회

    }

    private final Boolean boolDebug = true;
    private final ScrapingService service;

    @ApiOperation(value = "최근 1년 거래내역 가져오기", notes = "" + "\n")
    @GetMapping(URI.SCRAPING_ACCOUNT)
    public boolean scrapingRegister(@ApiIgnore @CurrentUser CustomUser user, @RequestParam Long idxCorp) throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("([AccountTransactionList ]) $dto='{}'", idxCorp);
        }
        return service.aWaitcrapingRegister1YearAll(user.idx(), idxCorp);
    }

    @ApiOperation(value = "10년간 데이터 가져오기", notes = "" + "\n")
    @GetMapping(URI.SCRAPING_ACCOUNT_HISTORY)
    public boolean scrapingRegisterAll(@ApiIgnore @CurrentUser CustomUser user, @RequestParam Long idxUser) throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("([AccountTransactionList ]) $dto='{}'", idxUser);
        }
        return service.aWaitScraping10Years(user.idx(), idxUser);
    }

    @ApiOperation(value = "스크래핑 중지", notes = "" + "\n")
    @GetMapping( URI.STOP )
    public ResponseEntity scrapingProcessKill(@RequestParam Long idxUser) {
        return service.scrapingProcessKill(idxUser);
    }

    @ApiOperation(value = "계좌별 스크래핑", notes = "" + "\n")
    @GetMapping( URI.SCRAPING_ACCOUNT_ID )
    public ResponseEntity scrapingAccount(@ApiIgnore @CurrentUser CustomUser user, @RequestParam String idxAccount
            , @RequestParam String strStart, @RequestParam String strEnd) throws ParseException {
        return service.scrapingAccount(user.idx(), idxAccount ,strStart , strEnd);
    }
}
