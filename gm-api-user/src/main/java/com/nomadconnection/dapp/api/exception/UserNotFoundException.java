package com.nomadconnection.dapp.api.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class UserNotFoundException extends RuntimeException {

	private final Long id;
	private final String email;
	private final String name;
	private final String mdn;
}
