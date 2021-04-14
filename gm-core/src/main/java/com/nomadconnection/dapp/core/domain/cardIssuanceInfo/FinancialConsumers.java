package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class FinancialConsumers {

	@Column(columnDefinition = "BIT(1) DEFAULT NULL COMMENT '상시근로자 5인이상 여부'")
	private Boolean overFiveEmployees;

	public FinancialConsumers updateOverFiveEmployees(boolean overFiveEmployees){
		this.overFiveEmployees = overFiveEmployees;
		return this;
	}

}
