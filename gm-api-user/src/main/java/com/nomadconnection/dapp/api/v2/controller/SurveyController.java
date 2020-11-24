package com.nomadconnection.dapp.api.v2.controller;

import com.nomadconnection.dapp.api.controller.UserController;
import com.nomadconnection.dapp.api.dto.SurveyDto;
import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.service.SurveyService;
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
@CrossOrigin(allowCredentials = "true")
@RequiredArgsConstructor
@RequestMapping(SurveyController.URI.BASE)
@Api(tags = "사용자", description = UserController.URI.BASE)
public class SurveyController {

	public static class URI {
		public static final String BASE = "/survey";
		public static final String ANSWER = "/answer";
	}

	private final SurveyService surveyService;

	@GetMapping
	@ApiOperation(value = "설문조사 주제 조회")
	public ApiResponse<?> findSurvey(@RequestParam String surveyTitle){
		return ApiResponse.OK(surveyService.findSurvey(surveyTitle));
	}

	@GetMapping(URI.ANSWER)
	@ApiOperation(value = "설문조사 응답내역 조회")
	public ApiResponse<?> find(@ApiIgnore @CurrentUser CustomUser user, @RequestParam String surveyTitle) {
		return ApiResponse.OK(surveyService.findAnswerByTitle(user.idx(), surveyTitle));
	}

	@PostMapping(URI.ANSWER)
	@ApiOperation(value = "설문조사 저장")
	public ApiResponse<?> save(@ApiIgnore @CurrentUser CustomUser user,
	                                 @RequestBody SurveyDto dto) {
		return ApiResponse.OK(surveyService.saveAnswer(user.idx(), dto));
	}

	@DeleteMapping(URI.ANSWER)
	@ApiOperation(value = "설문조사 삭제")
	public ApiResponse<?> delete(@ApiIgnore @CurrentUser CustomUser user,
	                                 @RequestBody SurveyDto dto) {
		surveyService.deleteAnswer(user.idx(), dto);
		return ApiResponse.OK();
	}

	@DeleteMapping(URI.ANSWER)
	@ApiOperation(value = "설문조사 삭제")
	public ApiResponse<?> delete(@ApiIgnore @CurrentUser CustomUser user,
	                                 @RequestBody SurveyDto dto) throws Exception {
		surveyService.delete(user.idx(), dto);
		return ApiResponse.OK();
	}


}
