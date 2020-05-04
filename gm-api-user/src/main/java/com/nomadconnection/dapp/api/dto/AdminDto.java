package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.ResBatchList;
import com.nomadconnection.dapp.core.domain.Risk;
import com.nomadconnection.dapp.core.domain.repository.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.ResAccountRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.AdminCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.ResBatchListCustomRepository;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class AdminDto {
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RiskDto {

		@ApiModelProperty("법인ID")
		public Long idxCorp;

		@ApiModelProperty("법인명 ")
		public String idxCorpName;

		@ApiModelProperty("변경 잔고 ")
		public double cardLimitNow;

		@ApiModelProperty("부여 한도")
		public double cardLimit;

		@ApiModelProperty("법인 등급")
		public String grade;

		@ApiModelProperty("최신잔고")
		public double balance;

		@ApiModelProperty("기준잔고")
		public double cashBalance;

		@ApiModelProperty("현재잔고")
		public double currentBalance;

		@ApiModelProperty("cardRestartCount")
		public Integer cardRestartCount;

		@ApiModelProperty("긴급중지")
		public Boolean emergencyStop;

		@ApiModelProperty("카드발급여부")
		public Boolean cardIssuance;

		@ApiModelProperty("카드발급여부")
		public Boolean cardAvailable;

		@ApiModelProperty("updatedAt")
		public LocalDateTime updatedAt;

		@ApiModelProperty("errCode")
		public String errCode;

		@ApiModelProperty("pause")
		public Boolean pause;

		public static RiskDto from(AdminCustomRepository.SearchRiskResultDto searchRiskResultDto){
			RiskDto riskDto = RiskDto.builder()
					.idxCorp(searchRiskResultDto.getIdxCorp())
					.idxCorpName(searchRiskResultDto.getIdxCorpName())
					.cardLimitNow(searchRiskResultDto.getCardLimitNow())
					.cardLimit(searchRiskResultDto.getCardLimit())
					.grade(searchRiskResultDto.getGrade())
					.balance(searchRiskResultDto.getBalance())
					.currentBalance(searchRiskResultDto.getCurrentBalance())
					.cashBalance(searchRiskResultDto.getCashBalance())
					.cardRestartCount(searchRiskResultDto.getCardRestartCount())
					.emergencyStop(searchRiskResultDto.getEmergencyStop())
					.cardIssuance(searchRiskResultDto.getCardIssuance())
					.cardAvailable(searchRiskResultDto.getCardAvailable())
					.updatedAt(searchRiskResultDto.getUpdatedAt())
					.errCode(searchRiskResultDto.getErrCode())
					.pause(searchRiskResultDto.getPause())
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

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class StopDto {
		@ApiModelProperty("idxCorp")
		public Long idxCorp ;

		@ApiModelProperty("true/false")
		public String booleanValue ;
	}



	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CashListDto {
		public Long idxUser;
		public Long idxCorp;
		public String resCompanyNm;
		public Double resAccountIn;
		public Double resAccountOut;
		public Double resAccountInOut;
		public Long burnRate;
		public Integer runWay;
		public Double befoBalance;
		public LocalDateTime createdAt;
		public String errCode;
		public String errStatus;

		public static CashListDto from(ResAccountRepository.CashResultDto cashResultDto) {
			CashListDto cashListDto = CashListDto.builder()
					.idxUser(cashResultDto.getIdxUser())
					.idxCorp(cashResultDto.getIdxCorp())
					.resCompanyNm(cashResultDto.getResCompanyNm())
					.resAccountIn(cashResultDto.getResAccountIn())
					.resAccountOut(cashResultDto.getResAccountOut())
					.resAccountInOut(cashResultDto.getResAccountInOut())
					.burnRate(cashResultDto.getBurnRate())
					.runWay(cashResultDto.getRunWay())
					.befoBalance(cashResultDto.getBefoBalance())
					.createdAt(cashResultDto.getCreatedAt())
					.errCode(cashResultDto.getErrCode())
					.errStatus(cashResultDto.getErrStatus())
					.build();
			return cashListDto;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CashListDetailDto {
		public String sumDate;
		public Long sumResAccountIn;
		public Long sumResAccountOut;
		public Long sumResAccountInOut;
		public Long lastResAfterTranBalance;

		public static CashListDetailDto from(ResAccountRepository.CaccountMonthDto caccountMonthDto) {
			CashListDetailDto cashListDetailDto = CashListDetailDto.builder()
					.sumDate(caccountMonthDto.getSumDate())
					.sumResAccountIn(caccountMonthDto.getSumResAccountIn())
					.sumResAccountOut(caccountMonthDto.getSumResAccountOut())
					.sumResAccountInOut(caccountMonthDto.getSumResAccountIn()-caccountMonthDto.getSumResAccountOut())
					.lastResAfterTranBalance(caccountMonthDto.getLastResAfterTranBalance())
					.build();
			return cashListDetailDto;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ErrorSearchDto {
		@ApiModelProperty("법인 idx")
		public Long idxCorp;

		@ApiModelProperty("법인명 ")
		public String corpName;

		@ApiModelProperty("에러메세지")
		private String errorMessage;

		@ApiModelProperty("에러코드 true/false")
		private String errorCode;

		@ApiModelProperty("금일여부 true/false")
		private String boolToday;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ErrorResultDto {
		@ApiModelProperty("법인 idx")
		public Long idxCorp;

		@ApiModelProperty("발생시간")
		public LocalDateTime updatedAt;

		@ApiModelProperty("법인명")
		public String corpName;

		@ApiModelProperty("은행")
		public String bankName;

		@ApiModelProperty("계좌번호")
		public String account;

		@ApiModelProperty("에러메세지")
		public String errorMessage;

		@ApiModelProperty("에러코드")
		public String errorCode;

		@ApiModelProperty("transactionId")
		public String transactionId;

		public static ErrorResultDto from (ResBatchListCustomRepository.ErrorResultDto dto){

			ErrorResultDto errorResultDto = ErrorResultDto.builder()
					.updatedAt(dto.getUpdatedAt())
					.corpName(dto.getCorpName()==null?"":dto.getCorpName())
					.bankName(dto.getBankName()==null?"":dto.getBankName())
					.account(dto.getAccount()==null?"":dto.getAccount())
					.errorMessage(dto.getErrMessage()==null?"":dto.getErrMessage())
					.errorCode(dto.getErrCode()==null?"":dto.getErrCode())
					.transactionId(dto.getTransactionId()==null?"":dto.getTransactionId())
					.build();

			return errorResultDto;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ScrapingListDto {
		@ApiModelProperty("법인ID")
		public Long idxCorp;

		@ApiModelProperty("법인명 ")
		public String idxCorpName;

		@ApiModelProperty("성공계좌")
		public String successAccountCnt;

		@ApiModelProperty("진행계좌")
		public String processAccountCnt;

		@ApiModelProperty("총계좌개수")
		public String allAccountCnt;

		@ApiModelProperty("createdAt")
		public LocalDateTime createdAt;

		@ApiModelProperty("updatedAt")
		public LocalDateTime updatedAt;

		@ApiModelProperty("endFlag")
		public boolean endFlag;

		@ApiModelProperty("user")
		public Long idxUser;

		public static ScrapingListDto from (CorpRepository.ScrapingResultDto dto){

			ScrapingListDto scrapingListDto = ScrapingListDto.builder()
					.createdAt(dto.getCreatedAt())
					.updatedAt(dto.getUpdatedAt())
					.idxCorp(dto.getIdxCorp())
					.idxCorpName(dto.getIdxCorpName())
					.successAccountCnt(dto.getSuccessAccountCnt()==null?"":dto.getSuccessAccountCnt())
					.processAccountCnt(dto.getProcessAccountCnt()==null?"":dto.getProcessAccountCnt())
					.allAccountCnt(dto.getAllAccountCnt()==null?"":dto.getAllAccountCnt())
					.endFlag(dto.getEndFlag())
					.idxUser(dto.getIdxUser())
					.build();

			return scrapingListDto;
		}
	}
}
