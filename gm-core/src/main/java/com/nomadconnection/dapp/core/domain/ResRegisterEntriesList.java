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
public class ResRegisterEntriesList extends BaseTime {

	// 법인등기부등본

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long idx;
	private Long idxCorp;

	private String resDocTitle; // 문서제목
	private String resRegistrationNumber; // 등기번호
	private String resRegNumber; // 등록번호
	private String commCompetentRegistryOffice; // 관할등기소
	private String resPublishRegistryOffice; // 발행등기소
	private String resPublishDate; // 발행일자
	private String resIssueNo; // 발급번호
}
