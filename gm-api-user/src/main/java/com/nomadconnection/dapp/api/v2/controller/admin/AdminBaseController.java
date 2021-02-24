package com.nomadconnection.dapp.api.v2.controller.admin;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Api(tags = "어드민 V2")
@CrossOrigin(allowCredentials = "true")
@RequestMapping(AdminBaseController.URI.BASE)
public class AdminBaseController {

	public static class URI {
		public static final String BASE = "/admin/v2";
	}

}
