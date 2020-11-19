package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import com.nomadconnection.dapp.api.dto.SurveyDto;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.exception.survey.SurveyAlreadyExistException;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.common.SurveyType;
import com.nomadconnection.dapp.core.domain.etc.Survey;
import com.nomadconnection.dapp.core.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SurveyServiceTests extends AbstractSpringBootTest {

	@Autowired
	private SurveyService surveyService;
	@Autowired
	private UserService userService;

	User user;

	Survey build(User user, SurveyType surveyType) {
		return Survey.builder()
			.title(CommonCodeType.SURVEY_FUNNELS).answer(surveyType)
			.user(user).build();
	}

	@BeforeEach
	void init() {
		user = userService.getUser(67L);
	}

	@Test
	@Order(1)
	@DisplayName("유저_설문조사정보_저장")
	@Disabled
	void saveUserSurvey() {
		Survey survey = build(user, SurveyType.FORUM);
		SurveyDto surveyResult = surveyService.save(user.idx(), SurveyDto.from(survey));
		SurveyDto expectedResult = SurveyDto.from(survey);

		assertEquals(surveyResult.getTitle(), expectedResult.getTitle());
		assertEquals(surveyResult.getAnswer(), expectedResult.getAnswer());
		assertEquals(surveyResult.getDetail(), expectedResult.getDetail());
	}

	@Test
	@Order(2)
	@DisplayName("유저_설문조사정보_상세정보_포함_저장")
	@Disabled
	void saveUserSurveyWithDetail() {
		Survey survey = build(user, SurveyType.SNS);
		survey.setDetail("페이스북에서 봤어요");
		SurveyDto surveyResult = surveyService.save(user.idx(), SurveyDto.from(survey));
		SurveyDto expectedResult = SurveyDto.from(survey);

		assertEquals(surveyResult.getDetail(), expectedResult.getDetail());
	}

	@Test
	@Order(3)
	@DisplayName("유저_설문조사정보_상세정보_미포함_저장")
	void saveUserSurveyWithOutDetail()  {
		Survey survey = build(user, SurveyType.SNS);
		assertThrows(BadRequestException.class, () -> surveyService.save(user.idx(), SurveyDto.from(survey)));
	}

	@Test
	@Order(4)
	@DisplayName("유저_설문조사정보_중복저장")
	void saveDuplicateSurvey()  {
		Survey survey = build(user, SurveyType.FORUM);
		assertThrows(SurveyAlreadyExistException.class, () -> surveyService.save(user.idx(), SurveyDto.from(survey)));
	}

	@Test
	@Order(5)
	@DisplayName("유저_설문조사정보_전체_조회")
	void findAllUserSurveyResult() {
		List<SurveyDto> userSurveys = surveyService.findAll(user.idx());
		assertNotNull(userSurveys);
	}

	@Test
	@Order(6)
	@DisplayName("유저_설문조사정보_주제별_조회")
	void findUserSurveyResult() {
		CommonCodeType surveyTitle = CommonCodeType.SURVEY_FUNNELS;
		List<SurveyDto> userSurveys = surveyService.findByTitle(user.idx(), surveyTitle);
		assertThat(userSurveys).filteredOn(survey -> survey.getTitle().equals(surveyTitle.toString()));
	}

}