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
public class ResAccountHistory extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;


    private String resAccount; // 계좌번호
    private String resAccountTrDate;           // 거래일자
    private String resAccountTrTime;           // 거래시각
    private String resAccountOut;           // 출금금액
    private String resAccountIn;           // 입금금액
    private String resAccountDesc1;           // 거래내역 비고1
    private String resAccountDesc2;           // 거래내역 비고2
    private String resAccountDesc3;           // 거래내역 비고3
    private String resAccountDesc4;           // 거래내역 비고4
    private String resAfterTranBalance;           // 거래후 잔액
    private String commStartDate;           // 시작일자
    private String commEndDate;           // 종료일자
    private String resValuationAmt;           // 평가금액
    private String resTranAmount;           // 거래금액
    private String resTranNum;           // 거래좌수
    private String resBasePrice;           // 기준가격
    private String resBalanceNum;           // 잔고좌수
    private String resRoundNo;           // 회차
    private String resMonth;           // 월분
    private String resTransTypeNm; //거래구분
    private String resType; //이자종류
    private String resPrincipal; //원금
    private String resInterest; //이자금액
    private String resOverdueInterest; //연체이자
    private String resReturnInterest; //환출이자
    private String resFee; //수수료
    private String resLoanBalance; //대출잔액
    private String resInterestRate; //대출이율
}
