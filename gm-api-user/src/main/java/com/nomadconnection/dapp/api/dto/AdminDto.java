package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.Risk;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class AdminDto {

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RiskDto {

		@ApiModelProperty("법인명(식별자)")
		public Long idxCorp;

		@ApiModelProperty("법인명")
		public String idxCorpName;

		@ApiModelProperty("이용약관(식별자)")
		public Long idx;

		@ApiModelProperty("사용자")
		public Long idxUser;

		@ApiModelProperty("일정")
		private String date;

		@ApiModelProperty("대표이사 연대보증 여부")
		private boolean ceoGuarantee;

		@ApiModelProperty("요구 보증금")
		private double depositGuarantee;

		@ApiModelProperty("보증금 납입 여부")
		private boolean depositPayment;

		@ApiModelProperty("카드발급여부")
		private boolean cardIssuance;

		@ApiModelProperty("벤처인증여부")
		private boolean ventureCertification;

		@ApiModelProperty("투자여부")
		private boolean vcInvestment;

		@ApiModelProperty("법인 등급")
		private String grade;

		@ApiModelProperty("등급별 한도율")
		private Integer gradeLimitPercentage;

		@ApiModelProperty("최소 잔고")
		private double minStartCash;

		@ApiModelProperty("최소 유지 잔고")
		private double minCashNeed;

		@ApiModelProperty("현재잔고")
		private double currentBalance;

		@ApiModelProperty("계좌 스크래핑 오류발생 여부")
		private Integer error;

		@ApiModelProperty("잔고의 45일 평균값")
		private double dma45;

		@ApiModelProperty("잔고의 45일 중간값")
		private double dmm45;

		@ApiModelProperty("보증금제외 현재잔고")
		private double actualBalance;

		@ApiModelProperty("한도기준잔고")
		private double cashBalance;

		@ApiModelProperty("발급가능여부")
		private boolean cardAvailable;

		@ApiModelProperty("한도계산값")
		private double cardLimitCalculation;

		@ApiModelProperty("실시간 한도")
		private double realtimeLimit;

		@ApiModelProperty("부여 한도")
		private double cardLimit;

		@ApiModelProperty("변경 잔고 ")
		private double cardLimitNow;

		@ApiModelProperty("긴급중지")
		private boolean emergencyStop;


		public static RiskDto from(Risk risk){
			RiskDto riskDto = RiskDto.builder()
					.idxUser(risk.user().idx())
					.date(risk.date())
					.ceoGuarantee(risk.ceoGuarantee())
					.depositGuarantee(risk.depositGuarantee())
					.depositPayment(risk.depositPayment())
					.cardIssuance(risk.cardIssuance())
					.ventureCertification(risk.ventureCertification())
					.vcInvestment(risk.vcInvestment())
					.grade(risk.grade())
					.gradeLimitPercentage(risk.gradeLimitPercentage())
					.minStartCash(risk.minStartCash())
					.minCashNeed(risk.minCashNeed())
					.currentBalance(risk.currentBalance())
					.error(risk.error())
					.dma45(risk.dma45())
					.dmm45(risk.dmm45())
					.actualBalance(risk.actualBalance())
					.cashBalance(risk.cashBalance())
					.cardAvailable(risk.cardAvailable())
					.cardLimitCalculation(risk.cardLimitCalculation())
					.realtimeLimit(risk.realtimeLimit())
					.cardLimit(risk.cardLimit())
					.cardLimitNow(risk.cardLimitNow())
					.emergencyStop(risk.emergencyStop())
					.build();
			return riskDto;
		}

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RiskBalanceDto {
		@ApiModelProperty("현재잔고")
		public Double riskBalance ;
	}
}
