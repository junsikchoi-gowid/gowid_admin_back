package com.nomadconnection.dapp.core.dto.limit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.limit.ReviewStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
public class LimitRecalculationPageDto {

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
	@JsonInclude
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

}

