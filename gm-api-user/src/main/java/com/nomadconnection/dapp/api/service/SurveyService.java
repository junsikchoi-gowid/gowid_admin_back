package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.SurveyDto;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.exception.api.NotRegisteredException;
import com.nomadconnection.dapp.api.exception.survey.SurveyAlreadyExistException;
import com.nomadconnection.dapp.api.exception.survey.SurveyNotRegisteredException;
import com.nomadconnection.dapp.core.domain.etc.Survey;
import com.nomadconnection.dapp.core.domain.etc.SurveyAnswer;
import com.nomadconnection.dapp.core.domain.repository.etc.SurveyAnswerRepository;
import com.nomadconnection.dapp.core.domain.repository.etc.SurveyRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyService {

	private final UserService userService;
	private final SurveyRepository surveyRepository;
	private final SurveyAnswerRepository surveyAnswerRepository;

	private final String NONE = "NONE";
	private final String DEFAULT_SURVEY = "DEFAULT";

	public SurveyDto.SurveyContents findSurvey(String surveyTitle) {
		List<SurveyDto.SurveyContents.SurveyAnswer> answers = new ArrayList<>();
		Survey contents = findSurveyTitle(surveyTitle);

		surveyRepository.findAllByTitleAndActivated(contents.getTitle(), true).orElseThrow(
			() -> new SurveyNotRegisteredException(ErrorCode.Api.NOT_FOUND)
		).stream().map(content -> {
			SurveyDto.SurveyContents.SurveyAnswer answer = SurveyDto.SurveyContents.SurveyAnswer.builder().key(content.getAnswer()).title(content.getAnswerName()).type(content.getAnswerType())
				.items(findSelectBoxItems(content.getItems())).build();
			answers.add(answer);
			return content;
		}).collect(Collectors.toList());

		return SurveyDto.SurveyContents.builder()
			.key(contents.getTitle()).title(contents.getTitleName()).answers(answers).build();
	}

	public List<SurveyDto> findAnswerByTitle(Long userIdx, String title) throws NotRegisteredException {
		Survey survey = findSurveyTitle(title);
		User user = userService.getUser(userIdx);
		List<SurveyAnswer> surveyAnswers = surveyAnswerRepository.findAllByUserAndTitle(user, survey.getTitle()).orElseThrow(
			() -> new NotRegisteredException(ErrorCode.Api.NOT_FOUND));
		return SurveyDto.from(surveyAnswers);
	}

	@Transactional(rollbackFor = SurveyAlreadyExistException.class)
	public SurveyDto saveAnswer(Long userIdx, SurveyDto dto) {
		try {
			Survey survey = findSurveyTitle(dto.getTitle());
			User user = userService.getUser(userIdx);
			SurveyAnswer surveyAnswer = SurveyAnswer.builder().title(survey.getTitle()).answer(dto.getAnswer()).detail(dto.getDetail()).user(user).build();
			existsDetail(surveyAnswer);
			return SurveyDto.from(surveyAnswerRepository.save(surveyAnswer));
		}catch (DataIntegrityViolationException e){
			throw new SurveyAlreadyExistException(ErrorCode.Api.SURVEY_ALREADY_EXIST);
		}
	}

	@Transactional(rollbackFor = SurveyAlreadyExistException.class)
	public void deleteAnswer(Long userIdx, SurveyDto dto) {
		Survey survey = findSurveyTitle(dto.getTitle());
		User user = userService.getUser(userIdx);
		SurveyAnswer surveyAnswer = surveyAnswerRepository.findAllByUserAndTitleAndAnswer(user, survey.getTitle(), dto.getAnswer()).orElseThrow(
			() -> new BadRequestException(ErrorCode.Api.NOT_FOUND, "Already deleted.")
		);
		surveyAnswerRepository.delete(surveyAnswer);
	}

	private List<String> findSelectBoxItems(String items){
		return Optional.ofNullable(items)
			.map(item -> Arrays.asList(items.split(","))).orElse(null);
	}

	private void existsDetail(SurveyAnswer surveyAnswer) {
		boolean requiredDetail = existsDetail(surveyAnswer.getTitle(), surveyAnswer.getAnswer()) && StringUtils.isEmpty(surveyAnswer.getDetail().get());
		if(requiredDetail){
			throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, "must be enter the detail");
		}
	}

	private boolean existsDetail(String title, String answer){
		String answerType = surveyRepository.findAllByTitleAndAnswer(title, answer).orElseThrow(
			() -> new SurveyNotRegisteredException(ErrorCode.Api.NOT_FOUND)
		).getAnswerType();

		return !NONE.equals(answerType);
	}

	private Survey findSurveyTitle(String surveyTitle){
		if(DEFAULT_SURVEY.equals(surveyTitle)){
			return surveyRepository.findAllGroupByTitle().orElseThrow(
				() -> new SurveyNotRegisteredException(ErrorCode.Api.NOT_FOUND)
			);
		}
		return surveyRepository.findAllByTitleAndActivatedGroupByTitle(surveyTitle).orElseThrow(
			() -> new SurveyNotRegisteredException(ErrorCode.Api.NOT_FOUND)
		);

	}

}