package com.nomadconnection.dapp.core.domain.etc;

import com.nomadconnection.dapp.core.abstracts.AbstractJpaTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


class SurveyAnswerTests extends AbstractJpaTest {

	@Test
	public void SurveyEquality(){
		SurveyAnswer surveyAnswer = SurveyAnswer.builder().idx(1L).build();
		SurveyAnswer anotherSurveyAnswer = SurveyAnswer.builder().idx(1L).build();
		assertEquals(surveyAnswer, anotherSurveyAnswer);
	}

	@Test
	public void SurveyNotEquality(){
		SurveyAnswer surveyAnswer = SurveyAnswer.builder().idx(1L).build();
		SurveyAnswer anotherSurveyAnswer = SurveyAnswer.builder().idx(2L).build();
		assertNotEquals(surveyAnswer, anotherSurveyAnswer);
	}

}
