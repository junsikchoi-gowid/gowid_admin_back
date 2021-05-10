package com.nomadconnection.dapp.api.v2.controller.flow;

import com.nomadconnection.dapp.api.v2.dto.FlowDto;
import com.nomadconnection.dapp.api.v2.service.flow.FlowReportService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.exception.response.GowidResponse;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

import static com.nomadconnection.dapp.core.exception.response.GowidResponse.ok;

@Slf4j
@CrossOrigin(allowCredentials = "true")
@RequiredArgsConstructor
@RestController
@RequestMapping(FlowBaseController.URI.BASE)
@Validated
@SuppressWarnings("unchecked")
public class FlowTagController extends FlowBaseController {
    public static class URI {
        public static final String TAG                  = "/tag";
    }
 
    private final FlowReportService flowReportService;


    @PreAuthorize("hasRole('CBT') and hasAnyRole('MASTER','VIEWER')")
    @ApiOperation( value = "계정과목 설정", notes = "hasRole : CBT "+ "\n"
            + "hasAnyRole : MASTER, VIEWER " + "\n" )
    @GetMapping(value = URI.TAG)
    public GowidResponse<List<FlowDto.FlowTagConfigDto>> getTag(@ApiIgnore @CurrentUser CustomUser user){

        log.info("[getReportTableMonth] user = {}, corp = {} " ,user.idx(), user.corp().idx());

        List<FlowDto.FlowTagConfigDto> list = flowReportService.getTagConfigList(user.corp().idx());

        return ok(list);
    }

    @PreAuthorize("hasRole('CBT') and hasAnyRole('MASTER','VIEWER')")
    @ApiOperation( value = "계정과목 설정 추가", notes = "hasRole : CBT "+ "\n"
            + "hasAnyRole : MASTER, VIEWER " + "\n" )
    @PostMapping(value = URI.TAG)
    public GowidResponse<FlowDto.FlowTagConfigDto> postTag(@ApiIgnore @CurrentUser CustomUser user,
                                  @RequestBody FlowDto.FlowTagConfigDto dto) {

        log.info("[postTag] user = {}, corp = {} " ,user.idx(), user.corp().idx());

        FlowDto.FlowTagConfigDto data = flowReportService.addFlowTagConfig(dto, user.corp());

        return ok(data);
    }

    @PreAuthorize("hasRole('CBT') and hasAnyRole('MASTER','VIEWER')")
    @ApiOperation( value = "계정과목 설정 수정", notes = "hasRole : CBT "+ "\n"
            + "hasAnyRole : MASTER, VIEWER " + "\n" )
    @PutMapping(value = URI.TAG + "/{idx}")
    public GowidResponse<FlowDto.FlowTagConfigDto> updateTag(@ApiIgnore @CurrentUser CustomUser user,
                                                             @RequestBody FlowDto.FlowTagConfigDto dto,
                                                             @PathVariable(required = false) Long idx) {

        log.info("[updateTag] user = {}, corp = {} " ,user.idx(), user.corp().idx());

        FlowDto.FlowTagConfigDto data = flowReportService.updateFlowTagConfig(user.corp(), idx, dto);

        return ok(data);
    }

    @PreAuthorize("hasRole('CBT') and hasAnyRole('MASTER','VIEWER')")
    @ApiOperation( value = "계정과목 설정 삭제" , notes = "hasRole : CBT "+ "\n"
            + "hasAnyRole : MASTER, VIEWER " + "\n" )
    @DeleteMapping(value = URI.TAG + "/{idx}")
    public GowidResponse<FlowDto.FlowTagConfigDto> deleteTag(@ApiIgnore @CurrentUser CustomUser user,
                                        @PathVariable(required = false) Long idx) {

        log.info("[deleteTag] user = {}, corp = {} ", user.idx(), user.corp().idx());

        FlowDto.FlowTagConfigDto data = flowReportService.deleteFlowTagConfig(idx, user.corp());

        return ok(data);
    }
}
