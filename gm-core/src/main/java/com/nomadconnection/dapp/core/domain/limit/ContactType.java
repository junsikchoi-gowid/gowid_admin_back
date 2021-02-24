package com.nomadconnection.dapp.core.domain.limit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum  ContactType {
	EMAIL("이메일"), PHONE("휴대폰"), BOTH("이메일/휴대폰");

	private final String contact;
}
