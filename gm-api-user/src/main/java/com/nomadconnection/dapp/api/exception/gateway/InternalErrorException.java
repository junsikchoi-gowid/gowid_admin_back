package com.nomadconnection.dapp.api.exception.gateway;

import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class InternalErrorException extends RuntimeException {
	private final String code;
	private final String desc;

	public InternalErrorException(ErrorCode.External externalError) {
		code = externalError.getCode();
		desc = externalError.getDesc();
	}

	public InternalErrorException(ErrorCode.External externalError, String addString) {
		code = externalError.getCode();
		desc = externalError.getDesc() + " - " + addString;
	}
}
