package com.nomadconnection.dapp.api.controller.external;

import com.nomadconnection.dapp.api.dto.ExternalDto;
import com.nomadconnection.dapp.api.service.external.ExternalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping(ExternalController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "[07] 외부연결", description = ExternalController.URI.BASE)
public class ExternalController {

	public static class URI {
		public static final String BASE = "/external/v1";
		public static final String DATA = "/data";			// 리스크
	}

	private final ExternalService service;

	@ApiOperation(value = "리스크 설정 저장", notes = "" + "\n")
	@GetMapping( URI.DATA )
	public Page getData(@PageableDefault Pageable page, @ModelAttribute ExternalDto externalDto) {
		return service.getData(page, externalDto);
	}
}
