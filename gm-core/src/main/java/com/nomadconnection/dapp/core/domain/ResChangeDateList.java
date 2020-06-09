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
public class ResChangeDateList extends BaseTime {

	// 법인등기부등본 - 상호(명칭) 리스트 객체

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long idx;
	private Long idxParent;

	private String resNumber; //호수(매수)
	private String resChangeDate; //변경일자
	private String resNote; //비고
	private String type; //객체타입
}
