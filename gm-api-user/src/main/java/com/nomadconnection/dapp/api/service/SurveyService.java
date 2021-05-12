package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.SurveyDto;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.exception.api.NotRegisteredException;
import com.nomadconnection.dapp.api.exception.survey.SurveyAlreadyExistException;
import com.nomadconnection.dapp.api.exception.survey.SurveyNotRegisteredException;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.etc.Survey;
import com.nomadconnection.dapp.core.domain.etc.SurveyAnswer;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
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

	private final SurveyRepository surveyRepository;
	private final SurveyAnswerRepository surveyAnswerRepository;
	private final CorpRepository corpRepository;

	private final String[] NOT_EXISTS_DETAIL = {"NONE", "TEXT"};
	private final String DEFAULT_SURVEY = "DEFAULT";

	public SurveyDto.SurveyContents findSurvey(String surveyTitle) {
		List<SurveyDto.SurveyContents.SurveyAnswer> answers = new ArrayList<>();
		Survey contents = findSurveyTitle(surveyTitle);

		surveyRepository.findAllByTitleAndActivatedOrderByAnswerOrderAsc(contents.getTitle(), true).orElseThrow(
			() -> new SurveyNotRegisteredException(ErrorCode.Api.NOT_FOUND)
		).stream().map(content -> {
			SurveyDto.SurveyContents.SurveyAnswer answer
				= SurveyDto.SurveyContents.SurveyAnswer.builder().key(content.getAnswer()).title(content.getAnswerName()).subTitle(content.getAnswerSubTitle()).type(content.getAnswerType())
				.items(findSelectBoxItems(content.getItems())).build();
			answers.add(answer);
			return content;
		}).collect(Collectors.toList());

		return SurveyDto.SurveyContents.builder()
			.key(contents.getTitle()).title(contents.getTitleName()).answers(answers).build();
	}

	@Transactional(readOnly = true)
	public List<SurveyDto> findAnswerByTitle(User user, String title) throws NotRegisteredException {
		User contractor;

		if(user.corp() == null) {
			contractor = user;
		} else {
			Long idxCorp = user.corp().idx();
			Optional<Corp> corp = corpRepository.findById(idxCorp);
			contractor = corp.isPresent() ? corp.get().user(): user;
		}

		Survey survey = findSurveyTitle(title);
		List<SurveyAnswer> surveyAnswers = surveyAnswerRepository.findAllByUserAndTitle(contractor, survey.getTitle()).orElseThrow(
			() -> new NotRegisteredException(ErrorCode.Api.NOT_FOUND));
		return SurveyDto.from(surveyAnswers);
	}

	public SurveyDto findAnswerByUser(User user) throws NotRegisteredException {
		List<SurveyAnswer> surveyAnswers = surveyAnswerRepository.findAllByUser(user).orElseThrow(
			() -> new NotRegisteredException(ErrorCode.Api.NOT_FOUND));
		return SurveyDto.from(surveyAnswers.get(0));
	}

	@Transactional(rollbackFor = SurveyAlreadyExistException.class)
	public SurveyDto saveAnswer(User user, SurveyDto dto) {
		try {
			Survey survey = findSurveyTitle(dto.getTitle());
			SurveyAnswer surveyAnswer = SurveyAnswer.builder().title(survey.getTitle()).answer(dto.getAnswer()).detail(dto.getDetail()).user(user).build();
			existsDetail(surveyAnswer);
			return SurveyDto.from(surveyAnswerRepository.save(surveyAnswer));
		}catch (DataIntegrityViolationException e){
			throw new SurveyAlreadyExistException(ErrorCode.Api.SURVEY_ALREADY_EXIST);
		}
	}

	@Transactional(rollbackFor = SurveyAlreadyExistException.class)
	public void deleteAnswer(User user, SurveyDto dto) {
		Survey survey = findSurveyTitle(dto.getTitle());
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

		for (String detail : NOT_EXISTS_DETAIL) {
			if(detail.equals(answerType)) {
				return false;
			}
		}
		return true;
	}

	private Survey findSurveyTitle(String surveyTitle){
		if(DEFAULT_SURVEY.equals(surveyTitle) || StringUtils.isEmpty(surveyTitle)){
			return surveyRepository.findAllGroupByTitle().orElseThrow(
				() -> new SurveyNotRegisteredException(ErrorCode.Api.NOT_FOUND)
			);
		}
		return surveyRepository.findAllByTitleAndActivatedGroupByTitle(surveyTitle).orElseThrow(
			() -> new SurveyNotRegisteredException(ErrorCode.Api.NOT_FOUND)
		);

	}

}