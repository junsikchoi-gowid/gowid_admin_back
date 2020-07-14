package com.nomadconnection.dapp.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class SearchDto {

	@ApiModelProperty("검색어")
	private String key;

//	@Data
//	@Builder
//	@NoArgsConstructor
//	@AllArgsConstructor
//	public static class SearchCardTransaction {
//
//		//
//		//	검색필터
//		//		- 카드(전체/선택)
//		//		- 검색기간(년월)
//		//	검색타입(날짜/항목/지역)
//		//		- 날짜
//		//			- 일, 페이지
//		//				- 미지정: 전체/선택된 카드의 월 이용금액, 일별 이용금액(총 이용건수)
//		//				- 지정: 전체/선택된 카드의 일 이용금액, 이용내역(시간, 지출항목, 카드소유자/부서, 가맹점주소, 금액)
//		//		- 항목
//		//			-
//		//		- 지역
//		//
//
//		@ApiModelProperty("선택된 카드(전체카드가 선택된 경우 null)")
//		private List<Long> cards;
//
//		@ApiModelProperty("검색년도")
//		private Integer searchYear;
//
//		@ApiModelProperty("검색월")
//		private Integer searchMonth;
//
//		@ApiModelProperty("검색일(옵션)")
//		private Integer searchDay;
//
//		@ApiModelProperty("지출보고유형(옵션)")
//		private ExpenseReportCategory category;
//	}
}
