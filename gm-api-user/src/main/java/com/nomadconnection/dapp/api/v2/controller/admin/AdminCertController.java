package com.nomadconnection.dapp.api.v2.controller.admin;

import com.nomadconnection.dapp.api.v2.service.admin.AdminCertService;
import com.nomadconnection.dapp.core.annotation.ApiPageable;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.domain.res.ConnectedMngRepository;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AdminCertController extends AdminBaseController {
    public static class URI {
        public static final String CERTS = "/certs";
    }

    private final AdminCertService adminCertService;

    @PreAuthorize("hasAnyAuthority('GOWID_ADMIN')")
    @ApiOperation( value = "인증서 조회")
    @ApiPageable
    @GetMapping(value = URI.CERTS + "/{idxUser}")
    public ResponseEntity<List<ConnectedMngRepository.ConnectedMngDto>> getCertList(
        @ApiIgnore @CurrentUser CustomUser user,
        @PathVariable Long idxUser){
        return ResponseEntity.ok().body(adminCertService.getCertList(idxUser));
    }
}
