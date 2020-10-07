package com.nomadconnection.dapp.api.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class BenefitNotFoundException extends RuntimeException {

	private final Long id;
}
