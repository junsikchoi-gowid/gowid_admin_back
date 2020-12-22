package com.nomadconnection.dapp.api.controller.external;

import com.nomadconnection.dapp.api.dto.external.ExternalLinkReq;
import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.service.external.ExternalLinkService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController
@RequestMapping(ExternalController.URI.BASE)
@RequiredArgsConstructor
@CrossOrigin(allowCredentials = "true")
@Api(tags = "외부연결", description = ExternalController.URI.BASE)
public class ExternalLinkController {

    public static class URI {
        public static final String LINKS = "/links";
    }

    private final ExternalLinkService externalLinkService;

    @ApiOperation(value = "쿼타북 APIKey 저장")
    @PostMapping(URI.LINKS)
    public ApiResponse<?> application(@ApiIgnore @CurrentUser CustomUser user,
                                      @RequestBody ExternalLinkReq request) {

        request.validation();
        externalLinkService.saveExternalKey(request, user);

        return ApiResponse.SUCCESS();
    }


}
