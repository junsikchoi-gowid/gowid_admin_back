package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import com.nomadconnection.dapp.api.dto.SurveyDto;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.exception.survey.SurveyAlreadyExistException;
import com.nomadconnection.dapp.core.domain.etc.SurveyAnswer;
import com.nomadconnection.dapp.core.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import java.util.List;

import static com.nomadconnection.dapp.api.service.SurveyAnswerServiceTests.Type.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SurveyAnswerServiceTests extends AbstractSpringBootTest {

	@Autowired
	private SurveyService surveyService;
	@Autowired
	private UserService userService;

	User user;

	final String surveyTitle = "DEFAULT";

	enum Type {
		KEYWORD, SNS, NEWS, FRIEND, CARD, VC, POSTER, ETC
	}

	SurveyAnswer build(User user, String title, String answer) {
		return SurveyAnswer.builder()
			.title(title).answer(answer)
			.user(user).build();
	}

	@BeforeEach
	void init() {
		user = userService.getUser(67L);
	}

	@Test
	@Order(1)
	@DisplayName("유저_설문조사정보_저장")
	void saveUserSurvey() {
		SurveyAnswer surveyAnswer = build(user, surveyTitle, KEYWORD.toString());

		try {
			SurveyDto surveyResult = surveyService.saveAnswer(user, SurveyDto.from(surveyAnswer));
			SurveyDto expectedResult = SurveyDto.from(surveyAnswer);

			assertEquals(surveyResult.getTitle(), expectedResult.getTitle());
			assertEquals(surveyResult.getAnswer(), expectedResult.getAnswer());
			assertEquals(surveyResult.getDetail(), expectedResult.getDetail());
		} catch (SurveyAlreadyExistException e){
			//Do Nothing
		}

	}

	@Test
	@Order(2)
	@DisplayName("유저_설문조사정보_상세정보_포함_저장")
	void saveUserSurveyWithDetail() {
		SurveyAnswer surveyAnswer = build(user, surveyTitle, SNS.toString());
		surveyAnswer.setDetail("페이스북");
		try {
			SurveyDto surveyResult = surveyService.saveAnswer(user, SurveyDto.from(surveyAnswer));
			SurveyDto expectedResult = SurveyDto.from(surveyAnswer);
			assertEquals(surveyResult.getDetail(), expectedResult.getDetail());
		} catch (SurveyAlreadyExistException e){
			//Do Nothing
		}
	}

	@Test
	@Order(3)
	@DisplayName("유저_설문조사정보_상세정보_미포함_저장")
	void saveUserSurveyWithOutDetail()  {
		SurveyAnswer surveyAnswer = build(user, surveyTitle, SNS.toString());
		assertThrows(BadRequestException.class, () -> surveyService.saveAnswer(user, SurveyDto.from(surveyAnswer)));
	}

	@Test
	@Order(4)
	@DisplayName("유저_설문조사정보_중복저장")
	void saveDuplicateSurvey()  {
		SurveyAnswer surveyAnswer = build(user, surveyTitle, NEWS.toString());
		try {
			surveyService.saveAnswer(user, SurveyDto.from(surveyAnswer));
			assertThrows(SurveyAlreadyExistException.class, () -> surveyService.saveAnswer(user, SurveyDto.from(surveyAnswer)));
		} catch (SurveyAlreadyExistException e){
			// Do nothing
		}
	}

	@Test
	@Order(5)
	@DisplayName("유저_설문조사정보_주제별_조회")
	void findUserSurveyByTitleResult() {
		List<SurveyDto> userSurveys = surveyService.findAnswerByTitle(user, surveyTitle);
		assertThat(userSurveys).filteredOn(survey -> survey.getTitle().equals(surveyTitle));
	}

	@Test
	@Order(6)
	@DisplayName("유저_설문조사정보_삭제")
	void delete() {
		SurveyAnswer surveyAnswer = build(user, surveyTitle, VC.toString());
		SurveyDto dto = SurveyDto.from(surveyAnswer);
		surveyService.saveAnswer(user, dto);
		surveyService.deleteAnswer(user, dto);
	}

	@Test
	@Order(7)
	@DisplayName("유저_설문조사정보_상세정보_공백_저장")
	void saveUserSurveyWithEmptyStringDetail()  {
		SurveyAnswer surveyAnswer = build(user, surveyTitle, ETC.toString());
		SurveyDto surveyResult = surveyService.saveAnswer(user, SurveyDto.from(surveyAnswer));
		assertEquals("", surveyResult.getDetail());
	}

	@Test
	@Order(8)
	@DisplayName("유저_설문조사정보_유저별_조회")
	void findUserSurveyResult()  {
		SurveyDto surveyResult = surveyService.findAnswerByUser(user);
		assertEquals("", surveyResult.getAnswer());
	}

}