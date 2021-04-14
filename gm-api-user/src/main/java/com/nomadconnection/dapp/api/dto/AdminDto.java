package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.AdminCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResAccountRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResBatchListRepository;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class AdminDto {
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RiskDto {

		@ApiModelProperty("법인ID")
		public Long idxCorp;

		@ApiModelProperty("법인명")
		public String idxCorpName;

		@ApiModelProperty("변경 잔고")
		public Double cardLimitNow;

		@ApiModelProperty("부여 한도")
		public Double cardLimit;

		@ApiModelProperty("승인한도")
		public Double confirmedLimit;

		@ApiModelProperty("법인 등급")
		public String grade;

		@ApiModelProperty("최신잔고")
		public Double balance;

		@ApiModelProperty("기준잔고")
		public Double cashBalance;

		@ApiModelProperty("현재잔고")
		public Double currentBalance;

		@ApiModelProperty("cardRestartCount")
		public Integer cardRestartCount;

		@ApiModelProperty("긴급중지")
		public Boolean emergencyStop;

		@ApiModelProperty("카드발급여부")
		public IssuanceStatus cardIssuance;

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
					.confirmedLimit(searchRiskResultDto.getConfirmedLimit())
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
	public static class RiskNewDto {

		@ApiModelProperty("법인ID")
		public Long idxCorp;

		@ApiModelProperty("법인명")
		public String idxCorpName;

		@ApiModelProperty("변경 잔고")
		public Double cardLimitNow;

		@ApiModelProperty("부여 한도")
		public Double cardLimit;

		@ApiModelProperty("승인한도")
		public Double confirmedLimit;

		@ApiModelProperty("법인 등급")
		public String grade;

		@ApiModelProperty("최신잔고")
		public Double balance;

		@ApiModelProperty("기준잔고")
		public Double cashBalance;

		@ApiModelProperty("현재잔고")
		public Double currentBalance;

		@ApiModelProperty("cardRestartCount")
		public Integer cardRestartCount;

		@ApiModelProperty("긴급중지")
		public Boolean emergencyStop;

		@ApiModelProperty("카드발급여부")
		public IssuanceStatus cardIssuance;

		@ApiModelProperty("카드발급여부")
		public Boolean cardAvailable;

		@ApiModelProperty("updatedAt")
		public LocalDateTime updatedAt;

		@ApiModelProperty("errCode")
		public String errCode;

		@ApiModelProperty("pause")
		public Boolean pause;

		@ApiModelProperty("pause")
		public CardCompany cardCompany;

		@ApiModelProperty("전략Seg")
		public String cardType;

		@ApiModelProperty("45일 평균")
		private Double dma45;

		@ApiModelProperty("45일 중간값")
		private Double dmm45;

		@ApiModelProperty("전송가능여부")
		private Boolean transFlag;

		private String hopeLimit;

		private Double realtimeLimit;

		private String baseDate;

		public static RiskNewDto from(AdminCustomRepository.SearchRiskResultV2Dto searchRiskResultDto){
			RiskNewDto riskNewDto = RiskNewDto.builder()
					.idxCorp(searchRiskResultDto.getIdxCorp())
					.idxCorpName(searchRiskResultDto.getIdxCorpName())
					.cardLimitNow(searchRiskResultDto.getCardLimitNow())
					.confirmedLimit(searchRiskResultDto.getConfirmedLimit())
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
					.cardCompany(searchRiskResultDto.getCardCompany())
					.cardType(searchRiskResultDto.getCardType())
					.dma45(searchRiskResultDto.getDma45())
					.dmm45(searchRiskResultDto.getDmm45())
					.transFlag(searchRiskResultDto.getTransFlag())
					.hopeLimit(searchRiskResultDto.getHopeLimit())
					.realtimeLimit(searchRiskResultDto.getRealtimeLimit())
					.baseDate(searchRiskResultDto.getBaseDate())
					.build();
			return riskNewDto;
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

		@ApiModelProperty("법인명")
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

		@ApiModelProperty("계좌번호")
		public String accountDisplay;

		@ApiModelProperty("에러메세지")
		public String errorMessage;

		@ApiModelProperty("에러코드")
		public String errorCode;

		@ApiModelProperty("transactionId")
		public String transactionId;

		public static ErrorResultDto from (ResBatchListRepository.ErrorResultDto dto){

			ErrorResultDto errorResultDto = ErrorResultDto.builder()
					.updatedAt(dto.getUpdatedAt())
					.idxCorp(dto.getIdxCorp())
					.corpName(dto.getCorpName()==null?"":dto.getCorpName())
					.bankName(dto.getBankName()==null?"":dto.getBankName())
					.account(dto.getAccount()==null?"":dto.getAccount())
					.accountDisplay(dto.getResAccountDisplay()==null?"":dto.getResAccountDisplay())
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

		@ApiModelProperty("법인명")
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
					.successAccountCnt(dto.getSuccessAccountCnt() == null ? "" : dto.getSuccessAccountCnt())
					.processAccountCnt(dto.getProcessAccountCnt() == null ? "" : dto.getProcessAccountCnt())
					.allAccountCnt(dto.getAllAccountCnt() == null ? "" : dto.getAllAccountCnt())
					.endFlag(dto.getEndFlag())
					.idxUser(dto.getIdxUser())
					.build();

			return scrapingListDto;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Issuance1800Req {

		@ApiModelProperty("user")
		@NotNull(message = "userIdx is empty!")
		public Long userIdx;
	}


	public class cardComTransInfoGrant {
		@ApiModelProperty("idxCorp")
		public List<Long> idxCorp ;

		@ApiModelProperty("true/false")
		public boolean booleanValue ;
	}


	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RiskTransDto {

		@ApiModelProperty("법인ID")
		public Long idxCorp;

		@ApiModelProperty("법인명")
		public String idxCorpName;

		@ApiModelProperty("변경 잔고")
		public Double cardLimitNow;

		@ApiModelProperty("부여 한도")
		public Double cardLimit;

		@ApiModelProperty("승인한도")
		public Double confirmedLimit;

		@ApiModelProperty("법인 등급")
		public String grade;

		@ApiModelProperty("최신잔고")
		public Double balance;

		@ApiModelProperty("기준잔고")
		public Double cashBalance;

		@ApiModelProperty("현재잔고")
		public Double currentBalance;

		@ApiModelProperty("cardRestartCount")
		public Integer cardRestartCount;

		@ApiModelProperty("긴급중지")
		public Boolean emergencyStop;

		@ApiModelProperty("카드발급여부")
		public IssuanceStatus cardIssuance;

		@ApiModelProperty("카드발급여부")
		public Boolean cardAvailable;

		@ApiModelProperty("updatedAt")
		public LocalDateTime updatedAt;

		@ApiModelProperty("errCode")
		public String errCode;

		@ApiModelProperty("pause")
		public Boolean pause;

		public static RiskDto from(AdminCustomRepository.RiskTransDto RiskTransDto){
			RiskDto riskDto = RiskDto.builder()
					.idxCorp(RiskTransDto.getIdxCorp())
					.idxCorpName(RiskTransDto.getIdxCorpName())
					.cardLimitNow(RiskTransDto.getCardLimitNow())
					.confirmedLimit(RiskTransDto.getConfirmedLimit())
					.cardLimit(RiskTransDto.getCardLimit())
					.grade(RiskTransDto.getGrade())
					.balance(RiskTransDto.getBalance())
					.currentBalance(RiskTransDto.getCurrentBalance())
					.cashBalance(RiskTransDto.getCashBalance())
					.cardRestartCount(RiskTransDto.getCardRestartCount())
					.emergencyStop(RiskTransDto.getEmergencyStop())
					.cardIssuance(RiskTransDto.getCardIssuance())
					.cardAvailable(RiskTransDto.getCardAvailable())
					.updatedAt(RiskTransDto.getUpdatedAt())
					.errCode(RiskTransDto.getErrCode())
					.pause(RiskTransDto.getPause())
					.build();
			return riskDto;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class corpInfoDetail{
		@ApiModelProperty("고위드제시한도")
		private String calculatedLimit;

		@ApiModelProperty("고객희망한도")
		private String hopeLimit;

		@ApiModelProperty("벤처기업확인서 보유 여부")
		private Boolean isVerifiedVenture;

		@ApiModelProperty("10억이상 VC투자 여부")
		private Boolean isVC;

		@ApiModelProperty("보증금")
		private Double depositGuarantee;

		@ApiModelProperty("실제카드한도")
		private String grantLimit;

		@ApiModelProperty("45일 평균잔고")
		public Double dma45;

		@ApiModelProperty("45일 중간잔고")
		public Double dmm45;

		@ApiModelProperty("최신잔고")
		public Double currentBalance;

		@ApiModelProperty("기준잔고")
		public Double cashBalance;

		@ApiModelProperty("최소유지잔고")
		public Double minCashNeed;

		@ApiModelProperty("realtimeLimit")
		public Double realtimeLimit;

		@ApiModelProperty("유지일")
		public Integer cardRestartCount;

		@ApiModelProperty("산출한도")
		public Double cardLimitNow;

		@ApiModelProperty("타입B제시한도")
		public String calculatedLimitBtype;

		@ApiModelProperty("전월카드한도")
		public String preMonthLimit;

		@ApiModelProperty("타입")
		public String cardType;

		@ApiModelProperty("발급")
		public IssuanceStatus cardIssuance;

		@ApiModelProperty("카드사")
		public CardCompany cardCompany;

		@ApiModelProperty("등급")
		public String grade;

		@ApiModelProperty("오늘자승인데이터전송승인")
		public boolean transYn;

		@ApiModelProperty("잔고미달")
		public boolean emergencyStop;

		@ApiModelProperty("마지막 신청 단계")
		private String issuanceDepth;

		@ApiModelProperty("담당자")
		private String userName;

		@ApiModelProperty("이메일주소")
		private String email;

		@ApiModelProperty("법인등록일")
		private String registerDate;

		@ApiModelProperty("신청완료일")
		private LocalDateTime applyDate;

		@ApiModelProperty("심사완료일")
		private LocalDateTime decisionDate;

		@ApiModelProperty("카드매수")
		private String cardCount;

		private String phoneNumber;
		private Boolean isSendSms;
		private Boolean isSendEmail;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class corpInfoDetailId {
		@ApiModelProperty("회사정보")
		CorpDto corpDto;

		@ApiModelProperty("카드사")
		private CardCompany cardCompany;

		@ApiModelProperty("고객 희망한도")
		private String hopeLimit;

		@ApiModelProperty("실제카드한도")
		private String grantLimit;

		@ApiModelProperty("이름")
		public String userName;

		@ApiModelProperty("휴대폰 번호")
		public String userNumber;

		@ApiModelProperty("이메일")
		public String userEmail;

		@ApiModelProperty("SMS 수신 동의")
		public Boolean smsFlag;

		@ApiModelProperty("이메일 수신 동의")
		public Boolean emailFlag;
	}
}
