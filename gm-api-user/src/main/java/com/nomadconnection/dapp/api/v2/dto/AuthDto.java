package com.nomadconnection.dapp.api.v2.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

public class AuthDto {

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PasswordBeforeLogin {

		@ApiModelProperty("인증번호")
		private String code;

		@ApiModelProperty("email")
		private String email;

		@ApiModelProperty("비밀번호")
		private String newPassword;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PasswordAfterLogin {
		@ApiModelProperty("이전 비밀번호")
		private String oldPassword;

		@ApiModelProperty("이후 비밀번호")
		private String newPassword;
	}
}
