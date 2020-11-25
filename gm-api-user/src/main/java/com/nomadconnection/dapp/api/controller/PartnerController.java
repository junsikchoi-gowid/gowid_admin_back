package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.service.BankService;
import com.nomadconnection.dapp.api.service.PartnerService;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping(PartnerController.URI.BASE)
@RequiredArgsConstructor
public class PartnerController {
    public static class URI {
        public static final String BASE = "/partner/v1/{externalId}";

        public static final String MONTH_BALANCE 	= "/bank-balance/month";	// (기간별) 월별 입출금 잔고 : 현금 흐름 월별
        public static final String ACCOUNT_LIST		= "/bank-accounts";		// 계좌정보 리스트
        public static final String BURN_RATE 	 	= "/burnrate";		// 통계 Burn Rate: 통계
    }

    private final BankService service;
    private final PartnerService partnerService;

    private Corp getCorp(String externalId) {
        System.out.println("External Id: " + externalId);
        //TODO implement this
        Corp corp = partnerService.getIdxCorp(externalId);

        if(corp == null) {
            throw CorpNotRegisteredException.builder().build();
        }
        return corp;
//        return Corp.builder().build().idx(423L).user(User.builder().build().idx(46L));
    }

    @GetMapping( PartnerController.URI.MONTH_BALANCE )
    public ResponseEntity MonthBalance(@PathVariable String externalId) {
        log.debug("[MonthBalance] externalId {}", externalId);
        Corp corp = getCorp(externalId);
        BankDto.MonthBalance dto = new BankDto.MonthBalance(null); // means current month
        if (log.isInfoEnabled()) {
            log.info("([ MonthBalance ]) $user='{}' $dto='{}'", corp.user().idx(), dto);
        }
        return service.monthBalance(dto, corp.user().idx());
    }

    @GetMapping( PartnerController.URI.ACCOUNT_LIST )
    public ResponseEntity AccountList(@PathVariable String externalId) {
        log.debug("[AccountList] externalId {}", externalId);
        Corp corp = getCorp(externalId);
        if (log.isInfoEnabled()) {
            log.info("([ AccountList ]) $user='{}' $idxCorp='{}' $isMasking='{}'", corp.user().idx(), corp.idx(), true);
        }
        return service.accountListExt(corp.user().idx(), corp.idx(), true);
    }

    @GetMapping( PartnerController.URI.BURN_RATE )
    public ResponseEntity BurnRate(@PathVariable String externalId) {
        log.debug("[BurnRate] externalId {}", externalId);

        Corp corp = getCorp(externalId);
        if (log.isInfoEnabled()) {
            log.info("([ BurnRate ]) $externalId='{}' idxCorp= '{}'", externalId, corp.idx());
        }

        return service.burnRate(corp.user().idx(), corp.idx());
    }
}
