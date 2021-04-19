package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.corp.CorpStatus;
import com.nomadconnection.dapp.core.domain.embed.Address;
import com.nomadconnection.dapp.jwt.dto.TokenDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthDto {

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AuthInfo {

		@ApiModelProperty("식별자(사용자)")
		private Long idx;

		@ApiModelProperty("식별자(소속법인)")
		private Long idxCorp;

		@ApiModelProperty("식별자(카드)")
		private Long idxCard;

		@ApiModelProperty("이메일(계정)")
		private String email;

		@ApiModelProperty("이름")
		private String name;

		@ApiModelProperty("연락처(폰)")
		private String mdn;

		@ApiModelProperty("제휴카드")
		private String companyCard;

		@ApiModelProperty("수령지(주소)")
		private Address recipientAddress;

		@ApiModelProperty("법인 등록 상태")
		private CorpStatus corpStatus;

		@ApiModelProperty("부가정보")
		private TokenDto.TokenSet.AccountInfo info;

		@ApiModelProperty("sms 수신 동의여부")
		private Boolean isSendSms;

		@ApiModelProperty("Email 수신 동의여부")
		private Boolean isSendEmail;
	}
}
