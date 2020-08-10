package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Accessors(fluent = true)
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class CorpExtend {

	@Column(columnDefinition = "BIT(1)  COMMENT   '가상화폐취급여부'")
	private Boolean isVirtualCurrency;

	@Column(columnDefinition = "BIT(1)  COMMENT   '상장여부'")
	private Boolean isListedCompany;

	@Column(columnDefinition = "varchar(100)  COMMENT   '상장거래소코드'")
	private String listedCompanyCode;
}
