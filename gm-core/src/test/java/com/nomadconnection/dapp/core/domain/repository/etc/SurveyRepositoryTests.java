package com.nomadconnection.dapp.core.domain.repository.etc;

import com.nomadconnection.dapp.core.abstracts.AbstractJpaTest;
import com.nomadconnection.dapp.core.domain.common.CommonCodeDetail;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.common.SurveyType;
import com.nomadconnection.dapp.core.domain.etc.Survey;
import com.nomadconnection.dapp.core.domain.repository.common.CommonCodeDetailRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.nomadconnection.dapp.core.domain.common.SurveyType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SurveyRepositoryTests extends AbstractJpaTest {

	@Autowired
	private SurveyRepository surveyRepository;

	@Autowired
	private CommonCodeDetailRepository commonCodeDetailRepository;

	@Autowired
	private UserRepository userRepository;

	private Survey survey;
	private CommonCodeType surveyTitle;
	private final String TEST_EMAIL = "lhjang@gowid.com";
	User user;

	@BeforeEach
	void init(){
		surveyTitle = CommonCodeType.SURVEY_FUNNELS;
		survey = Survey.builder().idx(1L).title(surveyTitle).build();
		user = userRepository
			.findByAuthentication_EnabledAndEmail(true, TEST_EMAIL).orElse(User.builder().build());
	}

	@Test
	@DisplayName("설문조사_선택항목_조회")
	void findCodeDetailList() {
		List<CommonCodeDetail> commonCodeDetailList = commonCodeDetailRepository.findAllByCode(surveyTitle);
		assertEquals(9, commonCodeDetailList.size());
		assertEquals(KEYWORD.toString(), commonCodeDetailList.get(0).code1());
	}

	Survey findSurveyCode(CommonCodeType code, SurveyType surveyDetailCode) {
		SurveyType surveyType = SurveyType.valueOf(commonCodeDetailRepository.getByCodeAndCode1(code, surveyDetailCode.toString()).code1());
		survey.setAnswer(surveyType);
		survey.setUser(user);

		return survey;
	}

	@Test
	@DisplayName("유저_설문조사_저장")
	void save() {
		SurveyType surveyType = KEYWORD;
		Survey survey = findSurveyCode(surveyTitle, surveyType);

		Survey userSurvey = surveyRepository.save(survey);

		assertEquals(surveyType, userSurvey.getAnswer());
		assertEquals("", userSurvey.getDetail().orElse("").toString());
	}

	@Test
	@DisplayName("유저_설문조사_상세정보포함_저장")
	void saveWithDetail() {
		String etcDetail = "어쩌다 알게됐어요";
		SurveyType surveyType = ETC;
		Survey survey = findSurveyCode(surveyTitle, surveyType);
		survey.setDetail(etcDetail);

		Survey userSurvey = surveyRepository.save(survey);

		assertEquals(surveyType, userSurvey.getAnswer());
		assertEquals(etcDetail, userSurvey.getDetail().get());
		assertNotNull(userSurvey.getDetail());
	}

	@Test
	@Transactional
	@Disabled
	@DisplayName("유저_설문조사_저장_및_조회")
	void saveAndFind() throws Exception {
		String snsDetail = "페이스북";
		SurveyType surveyType = SNS;
		Survey survey = findSurveyCode(surveyTitle, surveyType);
		survey.setDetail(snsDetail);

		Survey userSurvey = surveyRepository.save(survey);
		List<Survey> dbSurveys = surveyRepository.findAllByUser(userSurvey.getUser()).orElseThrow(
			() -> new Exception("not found."));
		Survey dbSurvey = dbSurveys.get(0);

		assertEquals(TEST_EMAIL, dbSurvey.getUser().email());
		assertEquals(CommonCodeType.SURVEY_FUNNELS.getDescription(), dbSurvey.getTitle());
		assertEquals(surveyType.toString(), dbSurvey.getAnswer());
		assertEquals(snsDetail, dbSurvey.getDetail().get());
	}

}