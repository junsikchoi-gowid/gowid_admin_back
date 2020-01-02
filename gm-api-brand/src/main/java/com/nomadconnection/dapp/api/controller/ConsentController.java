package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.BconsentDto;
import com.nomadconnection.dapp.api.service.ConsentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(ConsentController.URI.BASE)
@RequiredArgsConstructor
@Api(tags = "사용자", description = ConsentController.URI.BASE)
@SuppressWarnings({"unused", "deprecation"})
public class ConsentController {

	@SuppressWarnings("WeakerAccess")
	public static class URI {
		public static final String BASE = "/brand/v1";
		public static final String CONSENT = "/consent";
		// public static final String MEMBERS_MEMBER_DEPT = "/members/{member}/dept";
		// public static final String INFO = "/info";
	}

	private final ConsentService service;

	@ApiOperation(
			value = "Brand 이용약관",
			notes = "### Remarks \n - <mark>액세스토큰 불필요</mark>",
			tags = "1. 브랜드"
	)
	@ApiResponses(value={
			@ApiResponse(code = 200, message = "정상"),
			@ApiResponse(code = 201, message = "생성"),
			@ApiResponse(code = 401, message = "권한없음(패스워드 불일치)"),
			@ApiResponse(code = 403, message = "권한없음(패스워드 불일치)"),
			@ApiResponse(code = 404, message = "등록되지 않은 이메일"),
			@ApiResponse(code = 500, message = "")
	})
	@PostMapping(URI.CONSENT)
	@ExceptionHandler(Exception.class)
	public Integer consentList(
			@RequestBody BconsentDto dto
	) {
		log.debug("ConsentController ");

		service.consentList(dto.getIntData());

		return dto.getIntData();
		// service.registerUserCorp(dto);
//		return serviceAuth.issueTokenSet(AccountDto.builder()
//				.email(dto.getEmail())
//				.password(dto.getPassword())
//				.build());
	}
}
