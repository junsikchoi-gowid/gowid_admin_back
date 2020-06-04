package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CertificationType {
	RESIDENT("주민등록증", "101"),
	DRIVER("운전면허증", "201"),
	FOREIGN("외국인등록증", "301"),
	;

	private String description;
	private String code;
}
