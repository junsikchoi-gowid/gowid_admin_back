package com.nomadconnection.dapp.api.exception.api;

import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemException extends RuntimeException {
	private String code;
	private String desc;

	public SystemException(ErrorCode.External errorType) {
		code = errorType.getCode();
		desc = errorType.getDesc();
	}

	public SystemException(ErrorCode.External errorType, String addString) {
		code = errorType.getCode();
		desc = errorType.getDesc() + " - " + addString;
	}

	public SystemException(ErrorCode.Api errorType) {
		code = errorType.getCode();
		desc = errorType.getDesc();
	}

	public SystemException(ErrorCode.Api errorType, String addString) {
		code = errorType.getCode();
		desc = errorType.getDesc() + " - " + addString;
	}
}
