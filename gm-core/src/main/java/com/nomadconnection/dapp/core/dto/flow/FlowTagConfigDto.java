package com.nomadconnection.dapp.core.dto.flow;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.limit.ReviewStatus;
import lombok.*;

import javax.persistence.Column;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowTagConfigDto {

	private Long idx;
	private Corp corp;
	private String flowCode;
	private String code1;
	private String code2;
	private String code3;
	private String code4;
	private String codeLv1;
	private String codeLv2;
	private String codeLv3;
	private String codeLv4;
	private String codeDesc;
	private Boolean enabled;
	private Integer tagOrder;
}

