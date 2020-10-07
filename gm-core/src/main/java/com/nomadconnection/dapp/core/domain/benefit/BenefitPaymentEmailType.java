package com.nomadconnection.dapp.core.domain.benefit;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BenefitPaymentEmailType {

	BENEFIT_PAYMENT_SUCCESS_TEMPLATE("mail-template-benefit-payment-success"),
	BENEFIT_PAYMENT_SUCCESS_EMAIL_TITLE("[베네핏 테스트 메일] 결제가 완료되었습니다."),

	BENEFIT_PAYMENT_ORDER_TEMPLATE("mail-template-benefit-payment-order"),
	BENEFIT_PAYMENT_ORDER_EMAIL_TITLE("[베네핏 테스트 메일] 발주서 전달드립니다."),

	BENEFIT_PAYMENT_FAILED_TEMPLATE("mail-template-benefit-payment-failed"),
	BENEFIT_PAYMENT_FAILED_EMAIL_TITLE("결제를 실패했습니다."),

	BENEFIT_GOWID_EMAIL_ADDR("commerce@gowid.com")
	;

	private String value;
}
