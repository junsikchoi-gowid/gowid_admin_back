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
public class ResStandardFinancialList extends BaseTime {

	// 재무제표 

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long idx;
	private Long idxCorp;

	private String commStartDate; //시작일자	String	△	[사업연도_시작일자] 법인인 경우 필수, yyyyMMdd
	private String commEndDate; //종료일자	String	△	[사업연도_종료일자] 법인인 경우 필수, yyyyMMdd
	private String resUserNm; //성명	String	△
	private String resIssueNo; //발급(승인)번호	String	O
	private String resUserAddr; //주소	String	△
	private String resUserIdentiyNo; //주민번호	String	△
	private String resCompanyNm; //상호(사업장명)	String	△
	private String resCompanyIdentityNo; //사업자등록번호	String	△
	private String resBusinessItems; //종목	String	△
	private String resBusinessTypes; //업태	String	△
	private String resAttachDocument; //첨부파일	String	O	"|" 로 구분
	private String resReportingDate; //작성일자	String	O	[신고일] yyyyMMdd
	private String resAttrYear; //귀속연도	String	△	[사업연도] 개인(사업자)인 경우 필수, yyyy
}
