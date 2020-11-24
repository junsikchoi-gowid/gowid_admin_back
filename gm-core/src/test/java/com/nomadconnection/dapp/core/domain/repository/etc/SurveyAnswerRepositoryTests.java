package com.nomadconnection.dapp.core.domain.repository.etc;

import com.nomadconnection.dapp.core.abstracts.AbstractJpaTest;
import com.nomadconnection.dapp.core.domain.etc.Survey;
import com.nomadconnection.dapp.core.domain.etc.SurveyAnswer;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import javassist.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.nomadconnection.dapp.core.domain.repository.etc.SurveyAnswerRepositoryTests.Type.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SurveyAnswerRepositoryTests extends AbstractJpaTest {

	@Autowired
	private SurveyRepository surveyRepository;

	@Autowired
	private SurveyAnswerRepository surveyAnswerRepository;

	@Autowired
	private UserRepository userRepository;

	private String surveyTitle;
	private final String TEST_EMAIL = "lhjang@gowid.com";
	User user;

	enum Type {
		KEYWORD, SNS, NEWS, FRIEND, CARD, VC, POSTER, ETC
	}

	@BeforeEach
	void init(){
		surveyTitle = "SURVEY_FUNNELS";
		user = userRepository
			.findByAuthentication_EnabledAndEmail(true, TEST_EMAIL).orElse(User.builder().build());
	}

	@Test
	@DisplayName("설문조사_선택항목_조회")
	void findCodeDetailList() throws NotFoundException {
		List<Survey> surveys = surveyRepository.findAllByTitleAndActivated(surveyTitle, true).orElseThrow(
			() -> new NotFoundException("not found survey")
		);
		assertEquals(KEYWORD.toString(), surveys.get(0).getAnswer());
	}

	SurveyAnswer buildSurveyAnswer(String answer, String detail) {
		return SurveyAnswer.builder().idx(1L).title(surveyTitle).answer(answer).detail(detail).build();
	}

	@Test
	@DisplayName("유저_설문조사_저장")
	void save() {
		String answer = KEYWORD.toString();
		SurveyAnswer surveyAnswer = buildSurveyAnswer(answer, null);

		SurveyAnswer userSurveyAnswer = surveyAnswerRepository.save(surveyAnswer);

		assertEquals(answer, userSurveyAnswer.getAnswer());
		assertEquals("", userSurveyAnswer.getDetail().orElse("").toString());
	}

	@Test
	@DisplayName("유저_설문조사_상세정보포함_저장")
	void saveWithDetail() {
		String etcDetail = "어쩌다 알게됐어요";
		String answer = ETC.toString();
		SurveyAnswer surveyAnswer = buildSurveyAnswer(answer, etcDetail);

		SurveyAnswer userSurveyAnswer = surveyAnswerRepository.save(surveyAnswer);

		assertEquals(surveyAnswer.getAnswer(), userSurveyAnswer.getAnswer());
		assertEquals(etcDetail, userSurveyAnswer.getDetail().get());
		assertNotNull(userSurveyAnswer.getDetail());
	}

	@Test
	@Transactional
	@DisplayName("유저_설문조사_저장_및_조회")
	void saveAndFind() throws Exception {
		String snsDetail = "페이스북";
		String answer = SNS.toString();
		SurveyAnswer surveyAnswer = buildSurveyAnswer(surveyTitle, answer);
		surveyAnswer.setDetail(snsDetail);

		try {
			SurveyAnswer userSurveyAnswer = surveyAnswerRepository.save(surveyAnswer);
			List<SurveyAnswer> dbSurveyAnswers = surveyAnswerRepository.findAllByUser(userSurveyAnswer.getUser()).orElseThrow(
				() -> new Exception("not found."));
			SurveyAnswer dbSurveyAnswer = dbSurveyAnswers.get(0);

			assertEquals(TEST_EMAIL, dbSurveyAnswer.getUser().email());
			assertEquals(surveyTitle, dbSurveyAnswer.getTitle());
			assertEquals(answer, dbSurveyAnswer.getAnswer());
			assertEquals(snsDetail, dbSurveyAnswer.getDetail().get());
		} catch (Exception e){
			e.printStackTrace();
		}

	}

}