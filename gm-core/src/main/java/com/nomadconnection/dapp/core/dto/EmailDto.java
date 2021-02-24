package com.nomadconnection.dapp.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.text.NumberFormat;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDto {

	public EmailDto(String licenseNo, String companyName, String hopeLimit, String grantLimit, String email) {
		long hopeLimitLong = Long.parseLong(StringUtils.isEmpty(hopeLimit) ? "0" : hopeLimit);
		long grantLimitLong = Long.parseLong(StringUtils.isEmpty(grantLimit) ? "0" : grantLimit);
		this.licenseNo = licenseNo;
		this.companyName = companyName;
		this.hopeLimit = NumberFormat.getInstance().format(hopeLimitLong);
		this.grantLimit = NumberFormat.getInstance().format(grantLimitLong);
		this.email = email;
	}

	private String licenseNo;

	private String companyName;

	private String hopeLimit;

	private String grantLimit;

	private String email;

	private String receiver;
	private String[] receivers;
	private String sender;
	private String subject;
	private String template;
	private Map<String, Object> context;


}
