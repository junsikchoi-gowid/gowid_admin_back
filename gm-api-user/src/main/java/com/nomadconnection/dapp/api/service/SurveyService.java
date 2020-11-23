package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.SurveyDto;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.exception.survey.SurveyAlreadyExistException;
import com.nomadconnection.dapp.api.exception.survey.SurveyNotRegisteredException;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.common.SurveyType;
import com.nomadconnection.dapp.core.domain.etc.Survey;
import com.nomadconnection.dapp.core.domain.repository.etc.SurveyRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyService {

	private final UserService userService;
	private final SurveyRepository surveyRepository;

	public SurveyDto.SurveyContents findSurvey(CommonCodeType surveyTitle) {
		Map<CommonCodeType, String> title = new HashMap<>();
		Map<SurveyType, String> answer = new HashMap<>();
		Map<SurveyType, List<?>> selectBoxList = new HashMap<>();
		title.put(surveyTitle, surveyTitle.getDescription());

		Stream.of(SurveyType.values()).map(type -> {
			answer.put(type, type.getAnswer());
			if(SurveyType.existsSelectItem(type)) {
				selectBoxList.put(type, SurveyType.findSelectBoxItems(type));
			}
			return type;
		}).collect(Collectors.toList());


		return SurveyDto.SurveyContents.builder()
			.surveyTitle(title).surveyType(answer).selectBoxList(selectBoxList).build();
	}

	@Deprecated
	public List<SurveyDto> findAll(Long userIdx) throws SurveyNotRegisteredException {
		User user = userService.getUser(userIdx);
		List<Survey> surveys = surveyRepository.findAllByUser(user).orElseThrow(() -> new SurveyNotRegisteredException(ErrorCode.Api.NOT_FOUND));
		return SurveyDto.from(surveys);
	}

	public List<SurveyDto> findByTitle(Long userIdx, CommonCodeType title) throws SurveyNotRegisteredException {
		User user = userService.getUser(userIdx);
		List<Survey> surveys = surveyRepository.findAllByUserAndTitle(user, title).orElseThrow(() -> new SurveyNotRegisteredException(ErrorCode.Api.NOT_FOUND));
		return SurveyDto.from(surveys);
	}

	@Transactional(rollbackFor = SurveyAlreadyExistException.class)
	public SurveyDto save(Long userIdx, SurveyDto dto) {
		try {
			User user = userService.getUser(userIdx);
			Survey survey = Survey.builder().title(dto.getTitle()).answer(dto.getAnswer()).detail(dto.getDetail()).user(user).build();
			existsDetail(survey);
			return SurveyDto.from(surveyRepository.save(survey));
		}catch (DataIntegrityViolationException e){
			throw new SurveyAlreadyExistException(ErrorCode.Api.SURVEY_ALREADY_EXIST);
		}
	}

	private void existsDetail(Survey survey) {
		SurveyType surveyType = survey.getAnswer();
		boolean requiredDetail = SurveyType.existsDetail(surveyType) && StringUtils.isEmpty(survey.getDetail().get());
		if(requiredDetail){
			throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, "must be enter the detail");
		}
	}

}