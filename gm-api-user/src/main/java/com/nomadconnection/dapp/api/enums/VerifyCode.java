package com.nomadconnection.dapp.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VerifyCode {
	VERIFY_DEFAULT("verify_default"),
	REGISTER("register"),
	PASSWORD_RESET("password_reset");

	private String code;
}
