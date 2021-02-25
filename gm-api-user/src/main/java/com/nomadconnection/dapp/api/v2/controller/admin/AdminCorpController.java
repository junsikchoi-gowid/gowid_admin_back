package com.nomadconnection.dapp.api.v2.controller.admin;

import com.nomadconnection.dapp.api.v2.service.admin.AdminCorpService;
import com.nomadconnection.dapp.core.annotation.ApiPageable;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AdminCorpController extends AdminBaseController {
    public static class URI {
        public static final String CORPS = "/corps";
    }

    private final AdminCorpService adminCorpService;

    @PreAuthorize("hasAnyAuthority('GOWID_ADMIN')")
    @ApiOperation( value = "법인목록 조회")
    @ApiPageable
    @GetMapping(value = URI.CORPS)
    public ResponseEntity<?> getCorpList(
        @RequestParam(required = false) String keyWord,
        @PageableDefault Pageable pageable){
        return adminCorpService.getCorpList(keyWord, pageable);
    }

    @PreAuthorize("hasAnyAuthority('GOWID_ADMIN')")
    @ApiOperation( value = "법인정보 조회")
    @GetMapping(value = URI.CORPS + "/{idxCorp}")
    public ResponseEntity<?> getCorpInfo(
        @PathVariable Long idxCorp){
        return adminCorpService.getCorpInfo(idxCorp);
    }
}
