package com.nomadconnection.dapp.core.domain.repository.querydsl;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CardTransactionCustomRepository {




	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class MonthAmountDto {
		@ApiModelProperty("카드번호")
		private String cardNo;

		@ApiModelProperty("사용자명")
		private String userName;

		@ApiModelProperty("부서명")
		private String name;

		@ApiModelProperty("총금액")
		private Long amount;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class CardListDto {
		@ApiModelProperty("카드 idx")
		private Long cardIdx;

		@ApiModelProperty("카드번호")
		private String cardNo;

		@ApiModelProperty("사용자 idx")
		private Long idxUser;

//		@ApiModelProperty("사용자명")
//		private String userName;
//
//		@ApiModelProperty("부서명")
//		private String deptName;

		@ApiModelProperty("총금액")
		private Long usedAmount;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class PerDailyDto{
		@ApiModelProperty("날짜/항목/지역")
		private String asUsedAt;

		@ApiModelProperty("요일")
		private String week;

		@ApiModelProperty("총금액")
		private Long usedAmount;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class PerDailyDetailDto{
		@ApiModelProperty("날짜/항목/지역")
		private String asUsedAt;

		@ApiModelProperty("요일")
		private String week;

		@ApiModelProperty("총금액")
		private Long usedAmount;
	}



	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class PerHour{
		@ApiModelProperty("카드번호")
		private String cardNo;

		@ApiModelProperty("사용자명")
		private String userName;

		@ApiModelProperty("부서명")
		private String name;

		@ApiModelProperty("총금액")
		private Long amount;

		@ApiModelProperty("날짜/항목/지역")
		private String typeValue;

		@ApiModelProperty("결재정보")
		private String detailInfo;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class DetailInfo{
		@ApiModelProperty("제출여부")
		private String cardNo;

		@ApiModelProperty("승인일시")
		private String userName;

		@ApiModelProperty("승인번호")
		private String name;

		@ApiModelProperty("거래유형")
		private Long amount;

		@ApiModelProperty("할부")
		private String typeValue;

		@ApiModelProperty("공급가액")
		private String detailInfo;

		@ApiModelProperty("부가세")
		private String detailInfo1;

		@ApiModelProperty("상세 승인 내역")
		private String detailInfo2;

		@ApiModelProperty("가맹점명")
		private String detailInfo3;

		@ApiModelProperty("사업자명")
		private String detailInfo4;


	}

	List<PerDailyDto> findCustomHistoryByDate(String strDate, List<Long> cards);

	Page<PerDailyDetailDto> findHistoryByTypeDate(List<Long> cards, Pageable pageable);

	Page<PerDailyDetailDto> findHistoryByTypeCategory(List<Long> cards, Pageable pageable);

	Page<PerDailyDetailDto> findHistoryByTypeArea(List<Long> cards, Pageable pageable);

	List<CardListDto> findCardAdmin(String iYearMon, Long idx);

	List<CardListDto> findCardUser(String iYearMon, Long idx);

    DetailInfo findDetailInfo(@Param("strDate") String strDate, @Param("cards") List<Long> cards );

}

