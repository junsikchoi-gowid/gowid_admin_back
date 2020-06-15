package com.nomadconnection.dapp.api.exception;

import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class BusinessException extends RuntimeException {
	private final String error;
	private final String description;

	public BusinessException(ErrorCode.External externalError) {
		error = externalError.getCode();
		description = externalError.getDesc();
	}

	public BusinessException(ErrorCode.External externalError, String addString) {
		error = externalError.getCode();
		description = externalError.getDesc() + " - " + addString;
	}
}
