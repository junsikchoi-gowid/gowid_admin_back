package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.service.ScrapingService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;


@Slf4j
@CrossOrigin
@RestController
@RequestMapping(ScrapingController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "스크래핑", description = ScrapingController.URI.BASE)
public class ScrapingController {

    public static class URI {
        public static final String BASE = "/batch/v1";
        public static final String STOP = "/stop";    // 입출금 거래내역
        public static final String STOP_CORP = "/stopcorp";    // 입출금 거래내역
        public static final String SCRAPING_ACCOUNT = "/account-all";    // 은행 기업 보유계좌 + 거래내역
        public static final String SCRAPING_ACCOUNT_HISTORY = "/account-history";    // 입출금 거래내역
        public static final String SCRAPING_ACCOUNT_ID = "/account/id";    // 계좌별 조회
        public static final String SCRAPING_BANK_ID = "/bank/id";    // 계좌별 조회

    }

    private final Boolean boolDebug = true;
    private final ScrapingService service;

    @ApiOperation(value = "최근 1년 거래내역 가져오기", notes = "" + "\n")
    @GetMapping(URI.SCRAPING_ACCOUNT)
    public ResponseEntity scrapingRegister(@ApiIgnore @CurrentUser CustomUser user, @RequestParam Long idxCorp) {
        if (log.isDebugEnabled()) {
            log.debug("([AccountTransactionList ]) $dto='{}'", idxCorp);
        }
        return service.scrapingRegister1YearAll2(user.idx(), idxCorp);
    }

    @ApiOperation(value = "10년간 데이터 가져오기", notes = "" + "\n")
    @GetMapping(URI.SCRAPING_ACCOUNT_HISTORY)
    public boolean scrapingRegisterAll(@ApiIgnore @CurrentUser CustomUser user, @RequestParam Long idxUser) {
        if (log.isDebugEnabled()) {
            log.debug("([AccountTransactionList ]) $dto='{}'", idxUser);
        }
        return service.aWaitScraping10Years(user.idx(), idxUser);
    }

    @ApiOperation(value = "스크래핑 중지(유저)", notes = "" + "\n")
    @GetMapping( URI.STOP )
    public ResponseEntity scrapingProcessKillUser(@ApiIgnore @CurrentUser CustomUser user,
                                              @RequestParam(required = false) Long idxUser) {
        return service.scrapingProcessKill(user.idx(), idxUser, "user");
    }

    @ApiOperation(value = "스크래핑 중지(법인)", notes = "" + "\n")
    @GetMapping( URI.STOP_CORP )
    public ResponseEntity scrapingProcessKillCorp(@ApiIgnore @CurrentUser CustomUser user,
                                              @RequestParam(required = false) Long idxCorp) {
        return service.scrapingProcessKill(user.idx(), idxCorp, "corp");
    }

    @ApiOperation(value = "계좌별 스크래핑", notes = "" + "\n")
    @GetMapping( URI.SCRAPING_ACCOUNT_ID )
    public ResponseEntity scrapingAccount(@ApiIgnore @CurrentUser CustomUser user, @RequestParam String idxAccount
            , @RequestParam String strStart, @RequestParam String strEnd) throws ParseException, IOException, InterruptedException {
        return service.scrapingAccount(user.idx(), idxAccount ,strStart , strEnd);
    }

    @ApiOperation(value = "은행 스크래핑", notes = "" + "\n")
    @GetMapping( URI.SCRAPING_BANK_ID )
    public ResponseEntity scrapingBank(@ApiIgnore @CurrentUser CustomUser user
            ,@RequestParam String strConnetedId, @RequestParam String strBankCode
                ) throws ParseException{
        return service.scrapingBank(user.idx(), strConnetedId,strBankCode);
    }
}
