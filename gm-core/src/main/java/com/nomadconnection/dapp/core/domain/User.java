package com.nomadconnection.dapp.core.domain;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.embed.Authentication;
import com.nomadconnection.dapp.core.domain.embed.UserProfileResx;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email","enabledDate"}, name = "UK_User_Email"))
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
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
	private CardCompany cardCompany; // 카드회사 enum
	private Long creditLimit; // 월한도(예정) -> Card::creditLimit 월한도(적용)

	private Boolean consent; // 선택약관동의여부

	@Column(columnDefinition = "DATETIME default 99991231010101")
	private LocalDateTime enabledDate; // 삭제된 날짜

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idxCorp", foreignKey = @ForeignKey(name = "FK_Corp_User"))
	private Corp corp; // 소속법인

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idxDept", foreignKey = @ForeignKey(name = "FK_Dept_User"))
	private Dept dept; // 부서

//	@OneToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "idxCard", foreignKey = @ForeignKey(name = "FK_Card_User"))
//	@Where(clause = "disabled=false")
//	private Card card; // 활성화된 카드

//	@Embedded
//	private Address address;

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

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(
			name = "ConsentMapping",
			joinColumns = @JoinColumn(name = "idxUser", foreignKey = @ForeignKey(name = "FK_User_ConsentMapping")),
			inverseJoinColumns = @JoinColumn(name = "idxConsent", foreignKey = @ForeignKey(name = "FK_Consent_ConsentMapping"))
    )
	@Builder.Default
	private List<Consent> consents = new ArrayList<>();

}
