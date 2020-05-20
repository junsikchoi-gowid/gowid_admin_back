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
public class ResStockItemList extends BaseTime {

	// 법인등기부등본 - 본점주소 리스트

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long idx;
	private Long idxParent;

	private String resNumber; //순번
	private String resStockType; // 주식종류
	private String resStockCnt; // 주식수
	private String resTCntIssuedStock; // 발행주식의 총수
	private String resCapital; // 자본금의 액

}
