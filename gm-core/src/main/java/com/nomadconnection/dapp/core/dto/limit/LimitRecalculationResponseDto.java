package com.nomadconnection.dapp.core.dto.limit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nomadconnection.dapp.core.domain.limit.ContactType;
import com.nomadconnection.dapp.core.domain.limit.LimitRecalculation;
import com.nomadconnection.dapp.core.domain.limit.LimitRecalculationHistory;
import com.nomadconnection.dapp.core.domain.limit.ReviewStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LimitRecalculationResponseDto {

	private LimitRecalculationDto result;

	private List<LimitRecalculationHistoryDto> histories;

	@Getter
	@Builder
	@RequiredArgsConstructor
	@JsonInclude
	private static class LimitRecalculationDto {

		private final String companyName;

		private final String licenseNo;

		private final ReviewStatus reviewStatus;

		public static LimitRecalculationDto toDto(LimitRecalculation recalculation){
			LimitRecalculationDto recalculationDto =
				LimitRecalculationDto.builder()
					.companyName(recalculation.corp().resCompanyNm())
					.licenseNo(recalculation.corp().resCompanyIdentityNo())
					.reviewStatus(ReviewStatus.REQUESTED).build();

			return recalculationDto;
		}
	}

	@Getter
	@Builder
	@RequiredArgsConstructor
	@JsonInclude
	private static class LimitRecalculationHistoryDto {

		private final LocalDateTime requestAt;

		private final Long currentUsedAmount;

		private final Long hopeLimit;

		private final String accountInfo;

		private final String contents;

		private final ContactType contactType;

		public static LimitRecalculationHistoryDto toDto(LimitRecalculationHistory history){
			LimitRecalculationHistoryDto historyDto =
				LimitRecalculationHistoryDto.builder()
				.requestAt(history.getDate())
				.currentUsedAmount(history.getCurrentUsedAmount())
				.hopeLimit(history.getHopeLimit())
				.accountInfo(history.getAccountInfo())
				.contents(history.getContents())
				.contactType(history.getContactType())
				.build();
			return historyDto;
		}
	}

	public static LimitRecalculationResponseDto toDto(LimitRecalculation recalculation, List<LimitRecalculationHistory> limitRecalculationHistory){
		LimitRecalculationDto result =
			LimitRecalculationDto.toDto(recalculation);

		List<LimitRecalculationHistoryDto> histories =
			limitRecalculationHistory.stream()
				.map(LimitRecalculationHistoryDto::toDto)
				.collect(Collectors.toList());

		return LimitRecalculationResponseDto.builder()
			.result(result)
			.histories(histories)
			.build();
	}
}

