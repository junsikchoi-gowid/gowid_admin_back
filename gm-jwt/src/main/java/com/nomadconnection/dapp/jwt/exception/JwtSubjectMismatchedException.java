package com.nomadconnection.dapp.jwt.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class JwtSubjectMismatchedException extends RuntimeException {

	private final String jwt;
	private final String subject;
}
