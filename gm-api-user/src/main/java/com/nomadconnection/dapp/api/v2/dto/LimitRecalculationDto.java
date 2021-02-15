package com.nomadconnection.dapp.api.v2.dto;

import com.nomadconnection.dapp.api.dto.SurveyDto;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.etc.SurveyAnswer;
import com.nomadconnection.dapp.core.domain.limit.ContactType;
import com.nomadconnection.dapp.core.domain.limit.GrantStatus;
import com.nomadconnection.dapp.core.domain.limit.LimitRecalculation;
import com.nomadconnection.dapp.core.domain.limit.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LimitRecalculationDto {

	private Long idx;
	private Corp corp;
	private LocalDate date;
	private Long currentUsedAmount;
	private Long hopeLimit;
	private ContactType contactType;
	private String contact;
	private String contents;
	private ReviewStatus reviewStatus;
	private GrantStatus grantStatus;

	public static LimitRecalculationDto from(LimitRecalculation limitRecalculation){
		return LimitRecalculationDto.builder()
			.idx(limitRecalculation.getIdx())
			.corp(limitRecalculation.getCorp())
			.date(limitRecalculation.getDate())
			.currentUsedAmount(limitRecalculation.getCurrentUsedAmount())
			.hopeLimit(limitRecalculation.getHopeLimit())
			.contactType(limitRecalculation.getContactType())
			.contact(limitRecalculation.getContact())
			.contents(limitRecalculation.getContents())
			.reviewStatus(limitRecalculation.getReviewStatus())
			.grantStatus(limitRecalculation.getGrantStatus())
			.build();
	}

	public static List<LimitRecalculationDto> from(List<LimitRecalculation> limitRecalculations){
		return limitRecalculations.stream()
			.map(LimitRecalculationDto::from)
			.collect(Collectors.toList());
	}

}

