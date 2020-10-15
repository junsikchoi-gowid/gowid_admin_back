package com.nomadconnection.dapp.core.domain.risk;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskTrans extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private Long idx;

    private String date; // 날짜
    private boolean ceoGuarantee; //대표이사 연대보증 여부
    private double depositGuarantee;    //요구 보증금
    private boolean depositPayment;    //보증금 납입 여부
    private boolean cardIssuance;    //카드발급여부
    private boolean ventureCertification;    //벤처인증여부
    private boolean vcInvestment;    //투자여부
    private String grade;    //법인 등급
    private int gradeLimitPercentage;    //등급별 한도율
    private double minStartCash;    //최소 잔고
    private double minCashNeed;    //최소 유지 잔고
    private double currentBalance;    //현재잔고
    private int error;    //계좌 스크래핑 오류발생 여부
    private double dma45;    //잔고의 45일 평균값
    private double dmm45;    //잔고의 45일 중간값
    private double actualBalance;    //보증금제외 현재잔고
    private double cashBalance;    //한도기준잔고
    private boolean cardAvailable;    //발급가능여부
    private double cardLimitCalculation;    //한도계산값
    private double realtimeLimit;    //실시간 한도
    private double cardLimit;    //부여 한도
    private double cardLimitNow;    //변경 잔고
    private double confirmedLimit;    //승인 한도
    private boolean emergencyStop;    // 긴급중지
    private int cardRestartCount;    //숫자
    private boolean cardRestart;    // 카드재시작?
    private boolean pause;    // 일시정지
    private double recentBalance;    // 최근 잔고
    private String errCode; // 에러코드 일부값
    private String cardType; // 카드타입
    private Long idxCorp; // 법인
    private String resCompanyNm; // 법인명
    private Long idxUser; // 유저
    private String cardCompany; // 카드별 회사

}
