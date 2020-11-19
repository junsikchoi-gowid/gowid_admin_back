package com.nomadconnection.dapp.core.domain.etc;

import com.nomadconnection.dapp.core.abstracts.AbstractJpaTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class SurveyTests extends AbstractJpaTest {

	@Test
	public void SurveyEquality(){
		Survey survey = Survey.builder().idx(1L).build();
		Survey anotherSurvey = Survey.builder().idx(1L).build();
		assertEquals(survey, anotherSurvey);
	}

	@Test
	public void SurveyNotEquality(){
		Survey survey = Survey.builder().idx(1L).build();
		Survey anotherSurvey = Survey.builder().idx(2L).build();
		assertNotEquals(survey, anotherSurvey);
	}

}
