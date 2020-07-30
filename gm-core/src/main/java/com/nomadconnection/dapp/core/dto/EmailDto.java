package com.nomadconnection.dapp.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDto {

	private String licenseNo;

	private String companyName;

	private String hopeLimit;

	private String grantLimit;

}
