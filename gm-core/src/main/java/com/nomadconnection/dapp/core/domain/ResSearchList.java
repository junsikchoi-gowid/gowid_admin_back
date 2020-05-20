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
public class ResSearchList extends BaseTime {

	// 법인등기부등본 - 대표자

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long idx;
	private Long idxParent;

	private String resNumber; //호수(매수)
	private String commCompetentRegistryOffice; //관할등기소
	private String commCompanyType; //법인구분
	private String commBranchType; //본지점구분
	private String resRegistrationNumber; //등기번호
	private String resCompanyNm; //상호
	private String resCancelYN; //취소여부
	private String commRegistryStatus; //등기부상태
}
