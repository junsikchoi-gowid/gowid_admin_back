package com.nomadconnection.dapp.core.dto.limit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.limit.ContactType;
import com.nomadconnection.dapp.core.domain.limit.LimitRecalculation;
import com.nomadconnection.dapp.core.domain.limit.ReviewStatus;
import com.nomadconnection.dapp.core.domain.user.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LimitRecalculationDto {

	private LimitRecalculationResult result;

	private LimitRecalculationDetail detail;


	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LimitRecalculationCondition {
		private String companyName;
		private String licenseNo;
		private String manager;
		private String email;
		private IssuanceStatus issuanceStatus;
		private ReviewStatus reviewStatus;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LimitRecalculationResult {
		private Long idxCorp;

		private String companyName;
		private String licenseNo;
		private String manager;
		private String email;
		private IssuanceStatus issuanceStatus;
		private ReviewStatus reviewStatus;

		private CardCompany cardCompany;
		private Long hopeLimit;
		private Long calculatedLimit;
		private Long grantLimit;
		private LocalDate date;
	}

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class LimitRecalculationDetail {

		@DateTimeFormat(pattern = "yyyyMMdd")
		@NotEmpty
		private LocalDate date;
		private Long currentUsedAmount;
		private ContactType contactType;
		private String accountInfo;
		private String contents;

		private String companyName;
		private Long cardLimit;
		private Long hopeLimit;
	}

	public static LimitRecalculationDto from(LimitRecalculation limitRecalculation){
		Corp corp = limitRecalculation.getCorp();
		User user = corp.user();

		return LimitRecalculationDto.builder()
			.result(
				LimitRecalculationResult.builder()
					.idxCorp(corp.idx())
					.companyName(corp.resCompanyNm())
					.licenseNo(corp.resCompanyIdentityNo())
					.cardCompany(user.cardCompany())
					.hopeLimit(0L)
					.calculatedLimit(0L)
					.grantLimit(0L)
					.manager(user.name())
					.email(user.email())
					.issuanceStatus(null)
					.reviewStatus(limitRecalculation.getReviewStatus())
					.date(limitRecalculation.getDate())
				.build()
			)
			.detail(
				LimitRecalculationDetail.builder()
				.date(limitRecalculation.getDate())
				.currentUsedAmount(limitRecalculation.getCurrentUsedAmount())
				.contactType(limitRecalculation.getContactType())
				.accountInfo(limitRecalculation.getAccountInfo())
				.contents(limitRecalculation.getContents())
				.build()
			)
			.build();
	}

	public static List<LimitRecalculationDto> from(List<LimitRecalculation> limitRecalculations){
		return limitRecalculations.stream()
			.map(LimitRecalculationDto::from)
			.collect(Collectors.toList());
	}

}

