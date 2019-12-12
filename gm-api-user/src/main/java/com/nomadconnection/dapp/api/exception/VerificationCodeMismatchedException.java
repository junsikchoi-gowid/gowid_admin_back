package com.nomadconnection.dapp.api.exception;


import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Builder
@Accessors(fluent = true)
public class VerificationCodeMismatchedException extends RuntimeException {

	private String code;
}
