package com.nomadconnection.dapp.api.exception.api;

import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class BadRequestException extends RuntimeException {
	private final String code;
	private final String desc;

	public BadRequestException(ErrorCode.Api externalError) {
		code = externalError.getCode();
		desc = externalError.getDesc();
	}

	public BadRequestException(ErrorCode.Api externalError, String addString) {
		code = externalError.getCode();
		desc = externalError.getDesc() + " - " + addString;
	}

}
