package com.nomadconnection.dapp.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountDto {
	@ApiModelProperty("계좌번호")
	public String account;

	@ApiModelProperty("예금주")
	public String accountHolder;
}
