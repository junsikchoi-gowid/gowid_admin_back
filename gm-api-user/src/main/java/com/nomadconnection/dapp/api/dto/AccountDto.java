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
public class AccountDto {

	@ApiModelProperty("이메일(계정)")
	private String email;

	@ApiModelProperty("비밀번호")
	private String password;

	@Override
	public String toString() {
		return String.format("%s(email='%s', password='********')", getClass().getSimpleName(), email);
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FindAccount {

		@ApiModelProperty("이름")
		private String name;

		@ApiModelProperty("연락처")
		private String mdn;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PasswordReset {

		@ApiModelProperty("인증키")
		private String key;

		@ApiModelProperty("비밀번호")
		private String password;

		@Override
		public String toString() {
			return String.format("%s(key=%s, password=********)", getClass().getSimpleName(), key);
		}
	}
}
