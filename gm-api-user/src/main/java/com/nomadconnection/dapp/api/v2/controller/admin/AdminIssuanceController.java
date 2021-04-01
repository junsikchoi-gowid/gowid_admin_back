package com.nomadconnection.dapp.api.v2.controller.admin;

import com.nomadconnection.dapp.api.v2.dto.AdminDto;
import com.nomadconnection.dapp.api.v2.service.admin.AdminCardIssuanceService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CardIssunaceInfoCustomRepository;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AdminIssuanceController extends AdminBaseController {
    public static class URI {
        public static final String ISSUANCES = "/issuances";
    }

    private final AdminCardIssuanceService adminCardIssuanceService;

    // Todo : 발급정보가 N 개로 관리됨에 따라 어드민 화면구성 및 데이터 변경 필요

    @PreAuthorize("hasAnyAuthority('GOWID_ADMIN')")
    @ApiOperation( value = "발급정보 조회")
    @GetMapping(value = URI.ISSUANCES + "/{idxCardIssuanceInfo}")
    public ResponseEntity<CardIssunaceInfoCustomRepository.CardIssuanceInfoDto> getIssuanceInfo(
        @PathVariable Long idxCardIssuanceInfo){
        return ResponseEntity.ok().body(adminCardIssuanceService.getIssuanceInfo(idxCardIssuanceInfo));
    }

    @PreAuthorize("hasAnyAuthority('GOWID_CARD')")
    @ApiOperation( value = "발급정보 업데이트")
    @PatchMapping(value = URI.ISSUANCES + "/{idxCardIssuanceInfo}")
    public ResponseEntity<?> updateIssuanceStatus(
        @RequestBody AdminDto.UpdateIssuanceStatusDto dto,
        @PathVariable Long idxCardIssuanceInfo,
        @ApiIgnore @CurrentUser CustomUser user){
        if (log.isInfoEnabled()) {
            log.info("([ admin updateIssuanceStatus ]) $user='{}' $idxCardIssuanceInfo='{}'", user, idxCardIssuanceInfo);
        }
        return adminCardIssuanceService.updateIssuanceStatus(idxCardIssuanceInfo, dto);
    }
}
