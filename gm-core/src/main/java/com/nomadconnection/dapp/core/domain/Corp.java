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

	private String resBusinessItems; // 종목
	private String resBusinessTypes; // 업태
	private String resBusinessmanType; // 사업자종류
	private String resBusinessCode; // 업종코드

	@EqualsAndHashCode.Include
	private String resCompanyIdentityNo; // 사업자등록번호

	private String resCompanyNm; // 법인명드
	private String resCompanyEngNm; // 법인명(영문)
	private String resCompanyNumber; // 사업장번호
	private String resIssueNo; // 발급(승인)번호
	private String resIssueOgzNm; // 발급기관
	private String resJointIdentityNo; //공동사업자 주민번호
	private String resJointRepresentativeNm; // 공동사업자 성명(법인명)
	private String resOpenDate; // 개업일
	private String resOriGinalData; // 원문 DATA
	private String resRegisterDate; // 사업자등록일
	private String resUserAddr; // 사업장소재지(주소)
	private String resUserIdentiyNo; // 주민(법인)등록번호
	private String resUserNm; // 성명(대표자)
	private String resUserType; // 대표자 종류

	@Enumerated(EnumType.STRING)
	private CorpStatus status; // pending/denied/approved

}
