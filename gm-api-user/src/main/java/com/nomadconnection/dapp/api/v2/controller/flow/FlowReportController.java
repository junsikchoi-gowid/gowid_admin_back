package com.nomadconnection.dapp.api.v2.controller.flow;

import com.nomadconnection.dapp.api.util.CommonUtil;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
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
        public static final String REPORT   = "/report";
    }
 
    private final FlowReportService flowReportService;

    @PreAuthorize("hasAnyRole('MASTER','VIEWER')")
    @ApiOperation( value = "현황 월별 내역 ")
    @GetMapping(value = URI.REPORT_MONTH_STATUS)
    public GowidResponse<List<FlowDto.FlowReportByPeriodDto>> getReportStatusMonth(@ApiIgnore @CurrentUser CustomUser user,
                                                                                   @RequestParam(defaultValue = "yyyymmdd") String searchDate){

        log.info("[getReportStatusMonth] user = {}, corp = {} " ,user.idx(), user.corp().idx());

        if(searchDate.isEmpty()) searchDate = CommonUtil.getNowYYYYMMDD();
        
        List<FlowDto.FlowReportByPeriodDto> list = flowReportService.getReportStatusMonth(user.corp().idx(), searchDate);

        return ok(list);
    }

    @PreAuthorize("hasAnyRole('MASTER','VIEWER')")
    @ApiOperation( value = "현황 계정별 내역(-3월~당월)")
    @GetMapping(value = URI.REPORT_MONTH_TABLE)
    public GowidResponse<FlowDto.FlowCashInfo> getReportTableMonth(@ApiIgnore @CurrentUser CustomUser user,
                                                                   @RequestParam(defaultValue = "yyyymmdd") String searchDate){

        log.info("[getReportTableMonth] user = {}, corp = {} " ,user.idx(), user.corp().idx());

        if(searchDate.isEmpty()) searchDate = CommonUtil.getNowYYYYMMDD();

        FlowDto.FlowCashInfo data = flowReportService.getReportTableMonth(user.corp().idx(), searchDate);

        return ok(data);
    }

    @PreAuthorize("hasAnyRole('MASTER','VIEWER')")
    @ApiOperation( value = "통계데이터 생성")
    @GetMapping(value = URI.REPORT)
    public GowidResponse<FlowDto.FlowReportDto> getRepost(@ApiIgnore @CurrentUser CustomUser user){

        log.info("[getReportTableMonth] user = {}, corp = {} " ,user.idx(), user.corp().idx());

        FlowDto.FlowReportDto data = flowReportService.createReport(user.corp());

        return ok(data);
    }

}
