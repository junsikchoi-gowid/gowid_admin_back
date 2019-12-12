package com.nomadconnection.dapp.jwt.exception;

import com.nomadconnection.dapp.jwt.dto.TokenDto;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class UnacceptableJwtException extends RuntimeException {

	private final String jwt;

	private final TokenDto.TokenType tokenType;
	private final TokenDto.TokenType expectedTokenType;
}
