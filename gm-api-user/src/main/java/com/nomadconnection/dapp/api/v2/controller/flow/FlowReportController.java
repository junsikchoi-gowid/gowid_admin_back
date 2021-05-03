package com.nomadconnection.dapp.api.v2.controller.flow;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.api.v2.dto.FlowDto;
import com.nomadconnection.dapp.api.v2.service.flow.FlowReportService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.exception.response.GowidResponse;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static com.nomadconnection.dapp.core.exception.response.GowidResponse.*;

@Slf4j
@CrossOrigin(allowCredentials = "true")
@RequiredArgsConstructor
@RestController
@Api(tags = {"FlowCash"},  value = "FlowCash" )
@RequestMapping(FlowBaseController.URI.BASE)
@Validated
@SuppressWarnings("unchecked")
public class FlowReportController extends FlowBaseController {
    public static class URI {
        public static final String REPORT_MONTH_STATUS  = "/report/month/status";
        public static final String REPORT_MONTH_TABLE   = "/report/month/table";
        public static final String REPORT_MONTH_TABLE_FILE   = "/report/month/table/file";
        public static final String REPORT   = "/report";
    }
 
    private final FlowReportService flowReportService;

    @PreAuthorize("hasRole('ROLE_CBT') and hasAnyRole('MASTER','VIEWER')")
    @ApiOperation( value = "현황 월별 내역 ", notes = "hasRole : ROLE_CBT "+ "\n"
            + "hasAnyRole : MASTER, VIEWER " + "\n" )
    @GetMapping(value = URI.REPORT_MONTH_STATUS)
    public GowidResponse<List<FlowDto.FlowReportByPeriodDto>> getReportStatusMonth(@ApiIgnore @CurrentUser CustomUser user,
                                                                                   @RequestParam(defaultValue = "yyyymmdd") String searchDate){

        log.info("[getReportStatusMonth] user = {}, corp = {} " ,user.idx(), user.corp().idx());

        if(searchDate.isEmpty()) searchDate = CommonUtil.getNowYYYYMMDD();
        
        List<FlowDto.FlowReportByPeriodDto> list = flowReportService.getReportStatusMonth(user.corp().idx(), searchDate);

        return ok(list);
    }

    @PreAuthorize("hasRole('ROLE_CBT') and hasAnyRole('MASTER','VIEWER')")
    @ApiOperation( value = "현황 계정별 내역(-3월~당월)", notes = "hasRole : ROLE_CBT "+ "\n"
            + "hasAnyRole : MASTER, VIEWER " + "\n" )
    @GetMapping(value = URI.REPORT_MONTH_TABLE)
    public GowidResponse<FlowDto.FlowCashInfo> getReportTableMonth(@ApiIgnore @CurrentUser CustomUser user,
                                                                   @RequestParam(defaultValue = "yyyymmdd") String searchDate){

        log.info("[getReportTableMonth] user = {}, corp = {} " ,user.idx(), user.corp().idx());

        if(searchDate.isEmpty()) searchDate = CommonUtil.getNowYYYYMMDD();

        FlowDto.FlowCashInfo data = flowReportService.getReportTableMonth(user.corp().idx(), searchDate);

        return ok(data);
    }

    @PreAuthorize("hasRole('ROLE_CBT') and hasAnyRole('MASTER','VIEWER')")
    @ApiOperation( value = "현황 계정별 내역(-3월~당월) 엑셀 다운로드", notes = "hasRole : ROLE_CBT "+ "\n"
            + "hasAnyRole : MASTER, VIEWER " + "\n" )
    @GetMapping(value = URI.REPORT_MONTH_TABLE_FILE)
    public ResponseEntity<ByteArrayResource> getReportTableMonthFile(@ApiIgnore @CurrentUser CustomUser user,
                                                                   @RequestParam(defaultValue = "yyyymmdd") String searchDate) throws IOException {

        log.info("[getReportTableMonthFile] user = {}, corp = {} " ,user.idx(), user.corp().idx());

        if(searchDate.isEmpty()) searchDate = CommonUtil.getNowYYYYMMDD();

        FlowDto.FlowExcelPath flowExcelPath = flowReportService.getReportTableMonthFile(user.corp().idx(), searchDate);

        final ByteArrayResource resource = new ByteArrayResource(flowExcelPath.file);

        return ResponseEntity
                .ok()
                .contentLength(flowExcelPath.file.length)
                .header("Content-Transfer-Encoding", "binary")
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + URLEncoder.encode(flowExcelPath.fileName, "UTF-8") + "\"")
                .body(resource);
    }



    @PreAuthorize("hasRole('ROLE_CBT') and hasAnyRole('MASTER','VIEWER')")
    @ApiOperation( value = "통계데이터 생성", notes = "hasRole : ROLE_CBT "+ "\n"
            + "hasAnyRole : MASTER, VIEWER " + "\n" )
    @GetMapping(value = URI.REPORT)
    public GowidResponse<FlowDto.FlowReportDto> getRepost(@ApiIgnore @CurrentUser CustomUser user){

        log.info("[getReportTableMonth] user = {}, corp = {} " ,user.idx(), user.corp().idx());

        FlowDto.FlowReportDto data = flowReportService.createReport(user.corp());

        return ok(data);
    }

}
