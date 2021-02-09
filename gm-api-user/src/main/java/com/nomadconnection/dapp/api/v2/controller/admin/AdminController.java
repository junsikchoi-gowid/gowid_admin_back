package com.nomadconnection.dapp.api.v2.controller.admin;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("AdminV2Controller")
@RequiredArgsConstructor
@RequestMapping(AdminController.URI.BASE)
@CrossOrigin(allowCredentials = "true")
@Api(tags = "어드민 V2")
public class AdminController {

	public static class URI {
		public static final String BASE = "/admin/v2";
	}

}
