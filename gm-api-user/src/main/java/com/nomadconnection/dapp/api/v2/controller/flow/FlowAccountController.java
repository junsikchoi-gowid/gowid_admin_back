package com.nomadconnection.dapp.api.v2.controller.flow;

import com.nomadconnection.dapp.api.v2.dto.FlowDto;
import com.nomadconnection.dapp.api.v2.service.flow.FlowReportService;
import com.nomadconnection.dapp.core.annotation.ApiPageable;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.exception.response.GowidResponse;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

import static com.nomadconnection.dapp.core.exception.response.GowidResponse.ok;

@Slf4j
@CrossOrigin(allowCredentials = "true")
@RequiredArgsConstructor
@RestController
@Api(tags = {"FlowCash"}, value = "FlowCash")
@RequestMapping(FlowBaseController.URI.BASE)
@Validated
@SuppressWarnings("unchecked")
public class FlowAccountController extends FlowBaseController {
    public static class URI {
        public static final String ACCOUNT = "/account";
        public static final String ACCOUNT_HISTORY = "/account/history";
        public static final String ACCOUNT_HISTORY_EXCEL = "/account/history/excel";

    }

    private final FlowReportService flowReportService;

    @PreAuthorize("hasRole('CBT') and hasAnyRole('MASTER','VIEWER')")
    @ApiOperation(value = "계좌정보", notes = "hasRole : CBT "+ "\n"
            + "hasAnyRole : MASTER, VIEWER " + "\n" )
    @GetMapping(value = FlowAccountController.URI.ACCOUNT)
    public GowidResponse<List<FlowDto.FlowAccountDto>> getFlowAccount(@ApiIgnore @CurrentUser CustomUser user,
                                                                      @ModelAttribute FlowDto.SearchFlowAccount searchFlowAccount) {

        log.info("[getFlowAccount] user = {}, corp = {} ", user.idx(), user.corp().idx());

        List<FlowDto.FlowAccountDto> dto = flowReportService.getFlowAccount(user.corp().idx(), searchFlowAccount);

        return ok(dto);
    }

    @PreAuthorize("hasRole('CBT') and hasAnyRole('MASTER','VIEWER')")
    @ApiOperation(value = "계좌정보 즐겨찾기 저장", notes = "hasRole : CBT "+ "\n"
            + "hasAnyRole : MASTER, VIEWER " + "\n" )
    @PostMapping(value = FlowAccountController.URI.ACCOUNT + "/{idx}")
    public GowidResponse<FlowDto.FlowAccountDto> postAccount(@ApiIgnore @CurrentUser CustomUser user,
                                                             @PathVariable(required = false) Long idx,
                                                             @RequestBody FlowDto.AccountFavoriteDto dto) {

        log.info("[postAccount] user = {}, corp = {} ", user.idx(), user.corp().idx());

        FlowDto.FlowAccountDto data = flowReportService.saveAccount(dto, idx);

        return ok(data);
    }

    @PreAuthorize("hasRole('CBT') and hasAnyRole('MASTER','VIEWER')")
    @ApiOperation(value = "계좌거래내역", notes = "hasRole : CBT "+ "\n"
            + "hasAnyRole : MASTER, VIEWER " + "\n" )
    @GetMapping(value = FlowAccountController.URI.ACCOUNT_HISTORY)
    @ApiPageable
    public GowidResponse<Page<FlowDto.FlowAccountHistoryDto>> getAccountHistory(@ApiIgnore @CurrentUser CustomUser user,
                                                                                @ModelAttribute FlowDto.SearchFlowAccountHistory searchDto,
                                                                                @PageableDefault(size = 100, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("[getAccountHistory] user = {}, corp = {} ", user.idx(), user.corp().idx());

        Page<FlowDto.FlowAccountHistoryDto> list = flowReportService.getFlowAccountHistory(searchDto, pageable);

        return ok(list);
    }

    @PreAuthorize("hasRole('CBT') and hasAnyRole('MASTER','VIEWER')")
    @ApiOperation(value = "계좌거래내역", notes = "hasRole : CBT "+ "\n"
            + "hasAnyRole : MASTER, VIEWER " + "\n" )
    @PostMapping(value = FlowAccountController.URI.ACCOUNT_HISTORY)
    public GowidResponse<FlowDto.FlowAccountHistoryDto> postAccountHistory(@ApiIgnore @CurrentUser CustomUser user,
                                                                           @RequestBody FlowDto.FlowAccountHistoryUpdateDto dto) {

        log.info("[postAccountHistory] user = {}, corp = {} ", user.idx(), user.corp().idx());

        FlowDto.FlowAccountHistoryDto data = flowReportService.saveAccountHistory(dto);

        return ok(data);
    }

    @PreAuthorize("hasRole('CBT') and hasAnyRole('MASTER','VIEWER')")
    @ApiOperation(value = "계좌거래내역 다운로드", notes = "hasRole : CBT "+ "\n"
            + "hasAnyRole : MASTER, VIEWER " + "\n" )
    @GetMapping(value = URI.ACCOUNT_HISTORY_EXCEL)
    @ApiPageable
    ResponseEntity<ByteArrayResource>getExcelAccountHistory(@ApiIgnore @CurrentUser CustomUser user, HttpServletResponse response,
                                                 @ModelAttribute FlowDto.SearchFlowAccountHistory searchDto,
                                                 @PageableDefault(size = 50000, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) throws IOException {


        FlowDto.FlowExcelPath flowExcelPath = flowReportService.getExcelFlowAccountHistory(user.corp().idx(), searchDto, pageable);

        final ByteArrayResource resource = new ByteArrayResource(flowExcelPath.file);

        return ResponseEntity
                .ok()
                .contentLength(flowExcelPath.file.length)
                .header("Content-Transfer-Encoding", "binary")
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + URLEncoder.encode(flowExcelPath.fileName, "UTF-8") + "\"")
                .body(resource);
    }
}
