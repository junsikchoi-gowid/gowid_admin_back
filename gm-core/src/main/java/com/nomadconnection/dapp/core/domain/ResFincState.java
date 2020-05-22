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
public class ResFincState extends BaseTime {

	// 재무제표

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long idx;

	private String resCompanyIdentityNo; //사업자등록번호
	private String resIssueNo; //발급(승인)번호
	private String resUserIdentiyNo; //주민번호
	private String resCompanyNm; //상호(사업장명)
	private String resIssueFlag; //발급가능여부
	private String commStartDate; //시작일자
	private String commEndDate; //종료일자
	private String resUserNm; //성명
	private String resUserAddr; //주소
	private String resBusinessItems; //종목
	private String resBusinessTypes; //업태
	private String resReportingDate; //작성일자
	private String resAttrYear; //귀속연도
	private String resBalanceSheet; //총자산
	private String resIncomeStatement; //매출
	private String resBalanceSheet_amt; //납입자본금
	private String resBalanceSheet_total; //자기자본금
	private String financialPeriod; //재무조사일
}
