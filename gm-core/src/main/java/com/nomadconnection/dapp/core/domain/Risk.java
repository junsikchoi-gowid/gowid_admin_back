package com.nomadconnection.dapp.core.domain;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.*;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Risk extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long idx;
	private Long idxUser;

	private String date; // 날짜
	private boolean ceoGuarantee; //대표이사 연대보증 여부
	private Float depositGuarantee;	//요구 보증금
	private boolean depositPayment;	//보증금 납입 여부
	private boolean cardIssuance;	//카드발급여부
	private boolean ventureCertification;	//벤처인증여부
	private boolean vcInvestment;	//투자여부
	private String grade;	//법인 등급
	private Integer gradeLimitPercentage;	//등급별 한도율
	private Float minStartCash;	//최소 잔고
	private Float minCashNeed;	//최소 유지 잔고
	private Float currentBalance;	//현재잔고
	private Integer error;	//계좌 스크래핑 오류발생 여부
	private Float dma45;	//잔고의 45일 평균값
	private Float dmm45;	//잔고의 45일 중간값
	private Float actualBalance;	//보증금제외 현재잔고
	private Float cashBalance;	//한도기준잔고
	private boolean cardAvailable;	//발급가능여부
	private Float cardLimitCalculation;	//한도계산값
	private Float realtimeLimit;	//실시간 한도
	private Float cardLimit;	//부여 한도
	private Float cardLimitNow;	//변경 잔고
	private boolean emergencyStop;	// 긴급중지
	private Integer cardRestartCount;	//숫자
	private boolean cardRestart;	// 카드재시작?
}
