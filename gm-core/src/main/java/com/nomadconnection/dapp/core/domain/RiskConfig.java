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
public class RiskConfig extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long idx;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "idxCorp", foreignKey = @ForeignKey(name = "FK_RiskConfig_Corp"))
	private Corp corp; // 소속법인

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "idxUser", foreignKey = @ForeignKey(name = "FK_RiskConfig_User"))
	private User user; // 유저정보

	private boolean ceoGuarantee; // 대표이사 연대보증 여부
	private double depositGuarantee; // 요구 보증금
	private boolean depositPayment; // 보증금 납입 여부
	private boolean cardIssuance; // 카드발급여부
	private boolean ventureCertification; // 벤처인증여부 (벤처기업확인서 보유여부)
	private boolean vcInvestment; // 10억원 이상의 vc투자여부
	private boolean enabled;
	private boolean isStockHold25; // 25%이상의 지분을 보유한 개인여부
	private Boolean isStockholderPersonal; // 1대주주 개인여부
	private Boolean isStockholderList; // 1대주주 법인의 주주명부 보유여부
}
