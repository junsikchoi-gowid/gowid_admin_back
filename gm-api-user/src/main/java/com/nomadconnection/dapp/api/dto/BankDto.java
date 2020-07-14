package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.api.util.MaskingUtils;
import com.nomadconnection.dapp.core.domain.res.ResAccount;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BankDto {

	@ApiModelProperty("이메일(계정)")
	private String email;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AccountList {
		@ApiModelProperty("커넥티드아이디")
		private String connectedId;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AccountTransactionList {
		@ApiModelProperty("시작일자")
		private String startDate;

		@ApiModelProperty("종료일자")
		private String endDate;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SavingsList {

		@ApiModelProperty("시작일자")
		private String startDate;

		@ApiModelProperty("종료일자")
		private String endDate;

		@ApiModelProperty("일자 정렬 순서 \"0\": 최신순, \"1\": 과거순 (default:\"0\")")
		private String orderBy;

		@ApiModelProperty("조회구분 [계좌상세포함여부] \"0\":미포함, \"1\":포함 ( default: \"1\")\n")
		private String inquiryType;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ExchangeList {

		@ApiModelProperty("시작일자")
		private String startDate;

		@ApiModelProperty("종료일자")
		private String endDate;

		@ApiModelProperty("일자 정렬 순서 \"0\": 최신순, \"1\": 과거순 (default:\"0\")")
		private String orderBy;

		@ApiModelProperty("통화코드 KRW: 한국원화, JPY: 일본 엔, USD: 미국 달러, EUR: 유로 ( ISO 4217 코드)")
		private String currency;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FundList {

		@ApiModelProperty("시작일자")
		private String startDate;

		@ApiModelProperty("종료일자")
		private String endDate;

		@ApiModelProperty("일자 정렬 순서 \"0\": 최신순, \"1\": 과거순 (default:\"0\")")
		private String orderBy;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LoanList {

		@ApiModelProperty("시작일자")
		private String startDate;

		@ApiModelProperty("종료일자")
		private String endDate;

		@ApiModelProperty("일자 정렬 순서 \"0\": 최신순, \"1\": 과거순 (default:\"0\")")
		private String orderBy;

		@ApiModelProperty("대출실행번호 보유계좌 조회 결과에서 대출실행번호 있을 경우 입력 필수\n")
		private String accountLoanExecNo;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FastAccountList {
		@ApiModelProperty("통화코드")
		private String currency;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DayBalance {
		@ApiModelProperty("day 20200101")
		private String day;

		@ApiModelProperty("계좌번호")
		private String resAccount;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MonthBalance {
		@ApiModelProperty("month 202001")
		private String month;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MonthBalanceDto {
		@ApiModelProperty("sumDate")
		private String sumDate;

		@ApiModelProperty("sumResAccountIn")
		private String sumResAccountIn;

		@ApiModelProperty("sumResAccountOut")
		private String sumResAccountOut;

		@ApiModelProperty("lastResAfterTranBalance")
		private String lastResAfterTranBalance;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TransactionList {
		@ApiModelProperty("커넥티드아이디 idx")
		private Long idxConnectedId;

		@ApiModelProperty("검색일시 20200101 or 202001")
		private String searchDate;

		@ApiModelProperty("계좌번호")
		private String resAccount;

		@ApiModelProperty("입금 출금 타입 ex) in, out, all")
		private String resInOut;

		@ApiModelProperty("외화검색여부 ")
		private Boolean boolForeign;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BurnRate {
		@ApiModelProperty("BurnRate")
		private Long burnRate;

		@ApiModelProperty("Month")
		private Integer month;
	}


	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Nickname {
		@ApiModelProperty("idx 계좌")
		private Long idxAccount;

		@ApiModelProperty("계좌 별명")
		private String nickName;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ResAccountDto {
		@ApiModelProperty("idx")
		private Long idx;
		private String nickName; // 별칭 시스템용
		private String connectedId; // 커넥트드 아이디
		private String organization; // 기관코드
		private String type; // 종류
		private String resAccount              ; //계좌번호
		private String resAccountDisplay       ; //계좌번호_표시용
		private Double resAccountBalance       ; //현재잔액
		private String resAccountDeposit       ; //예금구분
		private String resAccountNickName      ; //계좌별칭
		private String resAccountCurrency      ; //통화코드
		private String resAccountHolder        ; //예금주
		private String resAccountStartDate     ; //신규일
		private String resAccountEndDate       ; //만기일
		private String resLastTranDate         ; //최종거래일
		private String resAccountName          ; //계좌명(종류)
		private String resOverdraftAcctYN      ; //마이너스 통장 여부
		private String resLoanKind             ; //대출종류
		private String resLoanBalance          ; //대출잔액
		private String resLoanStartDate        ; //대출신규일
		private String resLoanEndDate          ; //대출만기일
		private String resAccountInvestedCost  ; //투자원금
		private String resEarningsRate         ; //수익률[%]
		private String resAccountLoanExecNo    ; //대출실행번호
		private String errCode    ;
		private String errMessage    ;
		private LocalDateTime scrpaingUpdateTime    ;


		public static ResAccountDto from(ResAccount resAccount, Boolean isMasking) {

			String account = resAccount.resAccount();
			String accountDisplay = resAccount.resAccountDisplay();
			if (isMasking != null && isMasking) {
				account = MaskingUtils.maskingBankAccountNumber(resAccount.resAccount());
				accountDisplay = null;
			}

			String nickName = resAccount.nickName();
			if (!StringUtils.isEmpty(resAccount.resAccountName())) {
				nickName = resAccount.resAccountName();
			}
			if (!StringUtils.isEmpty(resAccount.resAccountNickName())) {
				nickName = resAccount.resAccountNickName();
			}
			if (!StringUtils.isEmpty(resAccount.nickName())) {
				nickName = resAccount.nickName();
			}

			return ResAccountDto.builder()
					.idx(resAccount.idx())
					.nickName(nickName)
					.connectedId(resAccount.connectedId())
					.organization(resAccount.organization())
					.type(resAccount.type())
					.resAccount(account)
					.resAccountHolder(resAccount.resAccountHolder())
					.resAccountDisplay(accountDisplay)
					.resAccountBalance(resAccount.resAccountBalance())
					.resAccountDeposit(resAccount.resAccountDeposit())
					.resAccountNickName(resAccount.resAccountNickName())
					.resAccountCurrency(resAccount.resAccountCurrency())
					.resAccountStartDate(resAccount.resAccountStartDate())
					.resAccountEndDate(resAccount.resAccountEndDate())
					.resLastTranDate(resAccount.resLastTranDate())
					.resAccountName(resAccount.resAccountName())
					.resOverdraftAcctYN(resAccount.resOverdraftAcctYN())
					.resLoanKind(resAccount.resLoanKind())
					.resLoanBalance(resAccount.resLoanBalance())
					.resLoanStartDate(resAccount.resLoanStartDate())
					.resLoanEndDate(resAccount.resLoanEndDate())
					.resAccountInvestedCost(resAccount.resAccountInvestedCost())
					.resEarningsRate(resAccount.resEarningsRate())
					.resAccountLoanExecNo(resAccount.resAccountLoanExecNo())
					.build();
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ResAccountErrDto {
		@ApiModelProperty("idx")
		private Long idx;
		private String nickName; // 별칭 시스템용
		private String connectedId; // 커넥트드 아이디
		private String organization; // 기관코드
		private String type; // 종류
		private String resAccount; //계좌번호
		private String resAccountDisplay; //계좌번호_표시용
		private String resAccountBalance; //현재잔액
		private String resAccountDeposit; //예금구분
		private String resAccountNickName; //계좌별칭
		private String resAccountCurrency; //통화코드
		private String resAccountStartDate; //신규일
		private String resAccountEndDate; //만기일
		private String resLastTranDate; //최종거래일
		private String resAccountName; //계좌명(종류)
		private String resOverdraftAcctYN; //마이너스 통장 여부
		private String resLoanKind; //대출종류
		private String resLoanBalance; //대출잔액
		private String resLoanStartDate; //대출신규일
		private String resLoanEndDate; //대출만기일
		private String resAccountInvestedCost; //투자원금
		private String resEarningsRate; //수익률[%]
		private String resAccountLoanExecNo; //대출실행번호
		private String errCode; //에러코드
		private String errMessage; //에러메시지

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ResAccountHistoryDto {
		private String resAccountDeposit;
		private String resAccount;
		private String accountName;
		private String resAccountBalance;
		private String resAccountTrDate;
		private String resAccountInOut;
		private String resAccountDesc3;
		private String resAfterTranBalance;

		public static ResAccountHistoryDto from(ResAccountHistoryDto resAccountHistoryDto){
			return ResAccountHistoryDto.builder()
					.accountName(resAccountHistoryDto.accountName)
					.resAccountDeposit(resAccountHistoryDto.resAccountDeposit)
					.resAccount(resAccountHistoryDto.resAccount)
					.resAccountBalance(resAccountHistoryDto.resAccountBalance)
					.resAccountTrDate(resAccountHistoryDto.resAccountTrDate)
					.resAccountInOut(resAccountHistoryDto.resAccountInOut)
					.resAccountDesc3(resAccountHistoryDto.resAccountDesc3)
					.resAfterTranBalance(resAccountHistoryDto.resAfterTranBalance)
					.build();
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CheckAccount {
		@ApiModelProperty("분(지난시간)")
		private String min;

		@ApiModelProperty("인증서관련여부")
		private String resType;

		@ApiModelProperty("에러코드")
		private String code;

		@ApiModelProperty("에러내용")
		private String message;

		@ApiModelProperty("총 계좌수")
		private Integer total;

		@ApiModelProperty("처리 계좌수")
		private Integer checkCnt;

		@ApiModelProperty("에러 계좌수")
		private Integer errorCnt;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AccountBatch{
		@ApiModelProperty("시작일자")
		private String startDate;

		@ApiModelProperty("종료일자")
		private String endDate;

		@ApiModelProperty("User Idx")
		private Long userIdx;

		@ApiModelProperty("Corp Idx")
		private Long idxCorp;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MonthInOutSum {
		@ApiModelProperty("월 - 202012")
		private String date;
    }
}
