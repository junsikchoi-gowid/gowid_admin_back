package com.nomadconnection.dapp.api.exception.v2.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Results {
	private final String code;
	private final String desc;
	private String extraMessage;
}
