package com.nomadconnection.dapp.core.domain;


import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class ResAccount extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
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
    private String resAccountHolder;// 예금주
    private String resManagementBranch;// 관리지점
    private String resAccountStatus;// 계좌상태
	private String resWithdrawalAmt;// 출금가능금액
	private String commEndDate;// 종료일자
	private String commStartDate;// 시작일자
	private String resFinalRoundNo;// 최종회차
	private String resMonthlyPayment;// 월부금
	private String resValidPeriod;// 유효기간
	private String resType;// 구분
	private String resRate;// 이율
	private String resContractAmount;// 계약금액
	private String resPaymentMethods;// 납입방법
	private String resBalanceNum;// 잔고좌수
    private String resPrincipal;// 대출원금
    private String resDatePayment;// 다음이자납입일
    private String resState;// 연체여부
}
