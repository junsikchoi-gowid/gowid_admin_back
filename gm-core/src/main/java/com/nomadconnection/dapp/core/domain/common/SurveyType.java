package com.nomadconnection.dapp.core.domain.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.nomadconnection.dapp.core.domain.common.CommonCodeType.SURVEY_FUNNELS;
import static com.nomadconnection.dapp.core.domain.common.SurveyType.DetailType.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SurveyType {

	KEYWORD(SURVEY_FUNNELS,"키워드 검색", NONE)
	, SNS(SURVEY_FUNNELS,"소셜 네트워크 서비스", SELECT)
	, NEWS(SURVEY_FUNNELS,"뉴스 혹은 신문 기사", NONE)
	, SHINHAN(SURVEY_FUNNELS,"신한 퓨처스랩", NONE)
	, LOTTE(SURVEY_FUNNELS,"롯데 엑셀러레이터", NONE)
	, DREAMPLUS(SURVEY_FUNNELS,"드림플러스", NONE)
	, FORUM(SURVEY_FUNNELS,"코리아 스타트업포럼", NONE)
	, FRIENDS(SURVEY_FUNNELS,"지인소개", NONE)
	, ETC(SURVEY_FUNNELS,"기타: 직접 입력", TEXT);

	private CommonCodeType title;
	private String answer;
	private DetailType detailType;

	enum DetailType {
		NONE, SELECT, TEXT
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	enum SelectBoxItem {
		FACEBOOK(SNS), LINKEDIN(SNS), ETC(SNS);
		private SurveyType surveyType;

		public static List<?> findItems(SurveyType surveyType){
			return Arrays.stream(SelectBoxItem.values())
					.filter(type -> type.getSurveyType().equals(surveyType))
					.collect(Collectors.toList());
		}
	}

	public static boolean existsDetail(SurveyType surveyType){
		return !NONE.equals(surveyType.getDetailType());
	}

	public static boolean existsSelectItem(SurveyType surveyType){
		return SELECT.equals(surveyType.getDetailType());
	}

	public static List<?> findSelectBoxItems(SurveyType surveyType){
		return SelectBoxItem.findItems(surveyType);
	}


}
