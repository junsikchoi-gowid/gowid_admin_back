package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.service.ScrapingService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;


@Slf4j
@CrossOrigin(allowCredentials = "true")
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

        public static final String SCRAPING_YEAR = "/scraping/year";    // 스크래핑 전체
        public static final String SCRAPING_EXCHANGE = "/scraping/exchange"; // 환율정보

    }

    private final ScrapingService service;

    @ApiOperation(value = "최근 1년 거래내역 가져오기", notes = "" + "\n")
    @GetMapping(URI.SCRAPING_ACCOUNT)
    public ResponseEntity scrapingRegister(@ApiIgnore @CurrentUser CustomUser user, @RequestParam Long idxCorp) {
        if (log.isInfoEnabled()) {
            log.info("([ scrapingRegister ]) $user='{}' $idxCorp='{}'", user, idxCorp);
        }
        // return service.scrapingRegister1YearAll2(user.idx(), idxCorp);
        service.scraping3Years(null, null, idxCorp);

        return ResponseEntity.ok().body(
                BusinessResponse.builder().normal(BusinessResponse.Normal.builder().status(true).build()).build());
    }

    @ApiOperation(value = "10년간 데이터 가져오기", notes = "" + "\n")
    @GetMapping(URI.SCRAPING_ACCOUNT_HISTORY)
    public void scrapingRegisterAll(@ApiIgnore @CurrentUser CustomUser user, @RequestParam Long idxUser) {
        if (log.isInfoEnabled()) {
            log.info("([ scrapingRegisterAll ]) $user='{}' $idxUser='{}'", user, idxUser);
        }
        service.runExecutor(idxUser);
    }

    @ApiOperation(value = "스크래핑 중지(유저)", notes = "" + "\n")
    @GetMapping( URI.STOP )
    public ResponseEntity scrapingProcessKillUser(@ApiIgnore @CurrentUser CustomUser user,
                                              @RequestParam(required = false) Long idxUser) {
        if (log.isInfoEnabled()) {
            log.info("([ scrapingProcessKillUser ]) $user='{}' $idxUser='{}'", user, idxUser);
        }
        return service.scrapingProcessKill(user.idx(), idxUser, "user");
    }

    @ApiOperation(value = "스크래핑 중지(법인)", notes = "" + "\n")
    @GetMapping( URI.STOP_CORP )
    public ResponseEntity scrapingProcessKillCorp(@ApiIgnore @CurrentUser CustomUser user,
                                              @RequestParam(required = false) Long idxCorp) {
        if (log.isInfoEnabled()) {
            log.info("([ scrapingProcessKillCorp ]) $user='{}' $idxCorp='{}'", user, idxCorp);
        }
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


    @ApiOperation(value = "은행 스크래핑 ALL 배치기능 "
            , notes = "" + "\n"
            + "관리자만 스크래핑 가능 " + "\n"
    )
    @GetMapping( URI.SCRAPING_YEAR )
    public ResponseEntity<?> scrapingYear(@ApiIgnore @CurrentUser CustomUser user, @RequestParam Long idxUser ) throws Exception {
        service.scraping3Years(user, idxUser, null);
        return new ResponseEntity<>(null,HttpStatus.OK);
    }

    @ApiOperation(value = "환율정보"
            , notes = "" + "\n"
            + "관리자만 스크래핑 가능 " + "\n"
    )
    @GetMapping( URI.SCRAPING_EXCHANGE )
    public ResponseEntity<?> scrapExchange(@ApiIgnore @CurrentUser CustomUser user) throws Exception {
        service.scrapExchange();
        return new ResponseEntity<>(null,HttpStatus.OK);
    }

}
