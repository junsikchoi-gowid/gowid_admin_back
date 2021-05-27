package com.nomadconnection.dapp.api.v2.dto;

import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashBoardDto {

	private Long corpCnt;
	private Long limitPercent;
	private Long maxLimit;
	private Long grantLimit;
	private String cardType;
	private String month;
	private String startDate;
	private String endDate;
	private Long applyCorpCnt;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Card {
		private Integer corpCnt;
		private Double limitPercent;
		private Long maxLimit;
		private Long grantLimit;
		private CardCompany cardCompany;
		private String month;
		private String startDate;
		private String endDate;
		private Long applyCorpCnt;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Month {
		private Integer corpCnt;
		private Long grantLimit;
		private String month;

		public static Month from(CardIssuanceInfoRepository.dashBoardMonthDto dashBoardMonthDto) {
			return Month.builder()
					.month(dashBoardMonthDto.getYearMonth())
					.corpCnt(dashBoardMonthDto.getCorpCnt())
					.grantLimit(dashBoardMonthDto.getTotalGrantLimit())
					.build();
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Week {
		private String startDate;
		private String endDate;
		private String yearWeek;
		private String yearMonth;
		private String week;
		private Long totalGrantLimit;
		private Integer corpCnt;

		public static Week from(CardIssuanceInfoRepository.dashBoardWeekDto dashBoardWeekDto) {
			return Week.builder()
					.corpCnt(dashBoardWeekDto.getCorpCnt())
					.startDate(dashBoardWeekDto.getStart())
					.endDate(dashBoardWeekDto.getEnd())
					.yearMonth(dashBoardWeekDto.getYearMonth())
					.yearWeek(dashBoardWeekDto.getYearWeek())
					.totalGrantLimit(dashBoardWeekDto.getTotalGrantLimit())
					.week(dashBoardWeekDto.getWeek())
					.build();
		}
	}
}

