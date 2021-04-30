package com.nomadconnection.dapp.api.v2.controller.flow;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Api(tags = "플로우 V2", description = FlowBaseController.URI.BASE)
@CrossOrigin(allowCredentials = "true")
@RequestMapping(com.nomadconnection.dapp.api.v2.controller.admin.AdminBaseController.URI.BASE)
public class FlowBaseController {

    public static class URI {
        public static final String BASE = "/flow/v2";
    }

}
