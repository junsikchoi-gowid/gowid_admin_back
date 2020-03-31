package com.nomadconnection.dapp.core.domain.repository.querydsl;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminCustomRepository {

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class SearchRiskDto {
		@ApiModelProperty("법인명")
		public String idxCorpName;

		@ApiModelProperty("법인 등급")
		private String grade;

		@ApiModelProperty("긴급중지")
		private String emergencyStop;

		@ApiModelProperty("카드발급여부")
		private String cardIssuance;

		@ApiModelProperty("udpateAt")
		private String updateAt;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class RiskCustomDto {

		@ApiModelProperty("법인명")
		private String idxCorpName;

		@ApiModelProperty("최신잔고")
		private Float balance;

		@ApiModelProperty("법인 등급")
		private String grade;

		@ApiModelProperty("긴급중지")
		private String emergencyStop;

		@ApiModelProperty("카드발급여부")
		private String cardIssuance;

		@ApiModelProperty("udpateAt")
		private String updateAt;

		@ApiModelProperty("법인명(식별자)")
		public Long idxCorp;

		@ApiModelProperty("이용약관(식별자)")
		public Long idx;

		@ApiModelProperty("사용자")
		public Long idxUser;

		@ApiModelProperty("일정")
		private String date;

		@ApiModelProperty("대표이사 연대보증 여부")
		private String ceoGuarantee;

		@ApiModelProperty("요구 보증금")
		private float depositGuarantee;

		@ApiModelProperty("보증금 납입 여부")
		private String depositPayment;



		@ApiModelProperty("벤처인증여부")
		private String ventureCertification;

		@ApiModelProperty("투자여부")
		private String vcInvestment;



		@ApiModelProperty("등급별 한도율")
		private Integer gradeLimitPercentage;

		@ApiModelProperty("최소 잔고")
		private float minStartCash;

		@ApiModelProperty("최소 유지 잔고")
		private float minCashNeed;

		@ApiModelProperty("현재잔고")
		private float currentBalance;

		@ApiModelProperty("계좌 스크래핑 오류발생 여부")
		private Integer error;

		@ApiModelProperty("잔고의 45일 평균값")
		private float dma45;

		@ApiModelProperty("잔고의 45일 중간값")
		private float dmm45;

		@ApiModelProperty("보증금제외 현재잔고")
		private Float actualBalance;

		@ApiModelProperty("한도기준잔고")
		private float cashBalance;

		@ApiModelProperty("발급가능여부")
		private String cardAvailable;

		@ApiModelProperty("한도계산값")
		private float cardLimitCalculation;

		@ApiModelProperty("실시간 한도")
		private float realtimeLimit;

		@ApiModelProperty("부여 한도")
		private float cardLimit;

		@ApiModelProperty("변경 잔고 ")
		private float cardLimitNow;
	}

	Page<RiskCustomDto> riskList(SearchRiskDto risk, Long idx, Pageable pageable);
}
