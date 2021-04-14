package com.nomadconnection.dapp.api.exception.kised;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class KisedException extends RuntimeException {

	private final String code;
	private final String desc;
	private final String shinhanMessage;

}
