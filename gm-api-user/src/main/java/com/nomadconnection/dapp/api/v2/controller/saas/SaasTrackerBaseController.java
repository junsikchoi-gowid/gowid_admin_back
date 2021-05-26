package com.nomadconnection.dapp.api.v2.controller.saas;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Api(tags = "SaaS Tracker V2")
@CrossOrigin(allowCredentials = "true")
@RequestMapping(SaasTrackerBaseController.URI.BASE)
public class SaasTrackerBaseController {

	public static class URI {
		public static final String BASE = "/saas/v2/tracker";
	}

}
