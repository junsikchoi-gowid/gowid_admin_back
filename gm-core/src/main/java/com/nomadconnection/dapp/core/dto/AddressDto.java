package com.nomadconnection.dapp.core.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class AddressDto {

	@ApiModelProperty("우편번호")
	private String zip;

	@ApiModelProperty("기본주소")
	private String basic;

	@ApiModelProperty("상세주소")
	private String detail;
}
