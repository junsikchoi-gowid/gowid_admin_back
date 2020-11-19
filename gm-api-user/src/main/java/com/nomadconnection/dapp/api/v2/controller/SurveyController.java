package com.nomadconnection.dapp.api.v2.controller;

import com.nomadconnection.dapp.api.controller.UserController;
import com.nomadconnection.dapp.api.dto.SurveyDto;
import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.service.SurveyService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController
@CrossOrigin(allowCredentials = "true")
@RequiredArgsConstructor
@RequestMapping(SurveyController.URI.BASE)
@Api(tags = "사용자", description = UserController.URI.BASE)
public class SurveyController {

	public static class URI {
		public static final String BASE = "/survey";
		public static final String USER = "/user";
	}

	private final SurveyService surveyService;

	@GetMapping
	@ApiOperation(value = "설문조사 주제 조회")
	public ApiResponse<?> findSurvey(@RequestParam CommonCodeType surveyTitle){
		return ApiResponse.OK(surveyService.findSurvey(surveyTitle));
	}

	@GetMapping(URI.USER)
	@ApiOperation(value = "설문조사 응답내역 조회")
	public ApiResponse<?> find(@ApiIgnore @CurrentUser CustomUser user, @RequestParam CommonCodeType surveyTitle){
		return ApiResponse.OK(surveyService.findByTitle(user.idx(), surveyTitle));
	}

	@PostMapping(URI.USER)
	@ApiOperation(value = "설문조사 저장")
	public ApiResponse<?> saveSurvey(@ApiIgnore @CurrentUser CustomUser user,
	                                 @RequestBody SurveyDto dto) {
		return ApiResponse.OK(surveyService.save(user.idx(), dto));
	}


}
