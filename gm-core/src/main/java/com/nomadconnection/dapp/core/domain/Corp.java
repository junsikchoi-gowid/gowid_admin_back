package com.nomadconnection.dapp.core.domain;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.embed.Address;
import com.nomadconnection.dapp.core.domain.embed.BankAccount;
import com.nomadconnection.dapp.core.domain.embed.CorpStockholdersListResx;
import com.querydsl.core.types.dsl.DateTimePath;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Corp extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long idx;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "idxUser", foreignKey = @ForeignKey(name = "FK_User_Corp"))
	private User user; // 법인을 등록한 사용자

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "idxRiskConfig", foreignKey = @ForeignKey(name = "FK_Corp_RiskConfig"))
	private RiskConfig riskConfig; // 법인 리스크 정보

	private String resBusinessItems;
	private String resBusinessTypes;
	private String resBusinessmanType;

	@EqualsAndHashCode.Include
	private String resCompanyIdentityNo;

	private String resCompanyNm; // 법인명
	private String resIssueNo;
	private String resIssueOgzNm;
	private String resJointIdentityNo;
	private String resJointRepresentativeNm;
	private String resOpenDate;
	private String resOriGinalData;
	private String resRegisterDate;
	private String resUserAddr;
	private String resUserIdentiyNo;
	private String resUserNm;

	@Enumerated(EnumType.STRING)
	private CorpStatus status; // pending/denied/approvedv

}
