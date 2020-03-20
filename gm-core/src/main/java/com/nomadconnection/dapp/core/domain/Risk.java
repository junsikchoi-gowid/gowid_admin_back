package com.nomadconnection.dapp.core.domain;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
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
public class Risk extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long idx;
	private Long idxUser;

	private String date;
	private boolean ceoGuarantee;
	private Float depositGuarantee;
	private boolean depositPayment;
	private String cardIssuance;
	private boolean ventureCertification;
	private boolean vcInvestment;
	private String grade;
	private Integer gradeLimitPercentage;
	private Float minStartCash;
	private Float minCashNeed;
	private Float currentBalance;
	private Integer error;
	private Float dma45;
	private Float dmm45;
	private Float actualBalance;
	private Float cashBalance;
	private boolean cardAvailable;
	private Float cardLimitCalculation;
	private Float realtimeLimit;
	private Float cardLimit;
	private Float cardLimitNow;
	private boolean emergencyStop;
	private Integer cardRestartCount;
	private boolean cardRestart;

}
