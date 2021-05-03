package com.nomadconnection.dapp.core.dto.flow;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowReportExcelDto {
	public String codeLv1;
	public String codeLv2;
	public String codeLv3;
	public String codeLv4;
	public String flowDate;
	public Double flowTotal;
}

