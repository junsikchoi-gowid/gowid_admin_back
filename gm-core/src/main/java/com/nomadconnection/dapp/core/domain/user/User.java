package com.nomadconnection.dapp.core.domain.user;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.benefit.BenefitPaymentHistory;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.consent.Consent;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.embed.Authentication;
import com.nomadconnection.dapp.core.domain.embed.UserProfileResx;
import com.nomadconnection.dapp.core.domain.embed.UserReception;
import com.nomadconnection.dapp.core.domain.etc.SurveyAnswer;
import com.nomadconnection.dapp.core.domain.saas.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email","enabledDate"}, name = "UK_User_Email"))
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class User extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;


	private String email; // 아이디
	private String password;
//	private String pin; // 개인식별번호(6 Digits)
	private String name;
	private String mdn;

	@Enumerated(EnumType.STRING)
	private CardCompany cardCompany; // 카드회사 enum

	private Long creditLimit; // 월한도(예정) -> Card::creditLimit 월한도(적용)

	private Boolean consent; // 선택약관동의여부

	@Embedded
	@Builder.Default
	private UserReception reception = new UserReception();

	@Column(columnDefinition = "DATETIME default 99991231010101")
	private LocalDateTime enabledDate; // 삭제된 날짜

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idxCorp", foreignKey = @ForeignKey(name = "FK_Corp_User"))
	private Corp corp;

	@Embedded
	private UserProfileResx profileResx;

	@Embedded
	@Builder.Default
	private Authentication authentication = new Authentication();

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "AuthoritiesMapping",
			joinColumns = @JoinColumn(name = "idxUser", foreignKey = @ForeignKey(name = "FK_User_AuthoritiesMapping")),
			inverseJoinColumns = @JoinColumn(name = "idxAuthority", foreignKey = @ForeignKey(name = "FK_Authority_AuthoritiesMapping")))
	@Builder.Default
	private Set<Authority> authorities = new HashSet<>();

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(
			name = "ConsentMapping",
			joinColumns = @JoinColumn(name = "idxUser", foreignKey = @ForeignKey(name = "FK_User_ConsentMapping")),
			inverseJoinColumns = @JoinColumn(name = "idxConsent", foreignKey = @ForeignKey(name = "FK_Consent_ConsentMapping"))
	)
	@Builder.Default
	private List<Consent> consents = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<BenefitPaymentHistory> benefitPaymentHistories;

	@OneToMany(mappedBy = "user")
	private List<SurveyAnswer> surveyAnswers;

	@Column(columnDefinition = "varchar(60) comment '외부 아이디' ")
	private String externalId;

	@Column(columnDefinition = "varchar(60) comment '회사명' ")
	private String corpName;

	@Column(columnDefinition = "varchar(40) comment '직책' ")
	private String position;

	@Builder.Default
	private boolean isReset = false; // 초기화여부

	@Builder.Default
	@Column(columnDefinition = "bit(1) NOT NULL default b'1' comment '서비스 담당자 여부' ")
	private boolean isMaster = true;

	@Builder.Default
	@Column(columnDefinition = "bit(1) NOT NULL default b'0' comment '임시비밀번호 인지 여부' ")
	private boolean hasTmpPassword = false;

	@OneToMany(mappedBy = "user")
	private List<SaasIssueReport> saasIssueReports;

	@OneToMany(mappedBy = "user")
	private List<SaasPaymentInfo> saasPaymentInfos;

	@OneToMany(mappedBy = "user")
	private List<SaasPaymentHistory> saasPaymentHistories;

	@OneToOne(mappedBy = "user")
	private SaasTrackerProgress saasTrackerProgress;

	@OneToMany(mappedBy = "user")
	private List<SaasCheckInfo> saasCheckInfos;
}
