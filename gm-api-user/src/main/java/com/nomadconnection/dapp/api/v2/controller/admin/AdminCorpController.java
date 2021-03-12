package com.nomadconnection.dapp.api.v2.controller.admin;

import com.nomadconnection.dapp.api.v2.dto.AdminDto;
import com.nomadconnection.dapp.api.v2.service.admin.AdminCorpService;
import com.nomadconnection.dapp.core.annotation.ApiPageable;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CorpCustomRepository;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Page<CorpCustomRepository.CorpListDto>> getCorpList(
        @ModelAttribute CorpCustomRepository.SearchCorpListDtoV2 dto,
        @PageableDefault Pageable pageable){
        return ResponseEntity.ok().body(adminCorpService.getCorpList(dto, pageable));
    }

    @PreAuthorize("hasAnyAuthority('GOWID_ADMIN')")
    @ApiOperation( value = "법인정보 조회")
    @GetMapping(value = URI.CORPS + "/{idxCorp}")
    public ResponseEntity<AdminDto.CorpDto> getCorpInfo(
        @PathVariable Long idxCorp){
        return ResponseEntity.ok().body(adminCorpService.getCorpInfo(idxCorp));
    }
}
