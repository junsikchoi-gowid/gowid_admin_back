package com.nomadconnection.dapp.api.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor
public class ExpiredException extends RuntimeException {

	private final LocalDateTime now;
	private final LocalDateTime expiration;
}
