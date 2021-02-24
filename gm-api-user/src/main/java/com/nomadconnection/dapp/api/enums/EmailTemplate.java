package com.nomadconnection.dapp.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailTemplate {
	VERIFY_CODE_REGISTER("[Gowid] 회원가입 이메일 인증번호", VerifyCode.PASSWORD_RESET.getCode(),"signup"),
	VERIFY_CODE_PASSWORD_RESET("[Gowid] 비밀번호 재설정 이메일 인증번호", VerifyCode.PASSWORD_RESET.getCode(),"password-init"),
	VERIFY_CODE_DEFAULT("[Gowid] 이메일 인증번호", VerifyCode.VERIFY_DEFAULT.getCode(), "mail-template"),
	LIMIT_RECALCULATION_SUPPORT("[Gowid] 한도 재심사 요청 안내", "SUPPORT", "limit-review-support"),
	LIMIT_RECALCULATION_USER("[Gowid] 한도 재심사 요청 안내", "USER", "limit-review");

	private String subject;
	private String code;
	private String template;

}
