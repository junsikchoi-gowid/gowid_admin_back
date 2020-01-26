package com.nomadconnection.dapp.jwt.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class AccessTokenNotFoundException extends RuntimeException {

	private final String header;
	private final String bearerToken;
}
