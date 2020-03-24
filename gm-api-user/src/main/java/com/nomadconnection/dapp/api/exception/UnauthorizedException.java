package com.nomadconnection.dapp.api.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class UnauthorizedException extends RuntimeException {

	private final Long idx;
	private final String account;
}
