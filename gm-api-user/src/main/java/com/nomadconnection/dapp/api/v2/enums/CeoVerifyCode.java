package com.nomadconnection.dapp.api.v2.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum  CeoVerifyCode {

	MISMATCHED_CEO_WITH_CORP_REGISTRATION("998"),
	OLD_DRIVER_LICENSE_CODE("999");

	private final String code;

}
