package com.nomadconnection.dapp.api.exception.api;

import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadRequestException extends RuntimeException {
	private String code;
	private String desc;

	public BadRequestException(ErrorCode.Api externalError) {
		code = externalError.getCode();
		desc = externalError.getDesc();
	}

	public BadRequestException(ErrorCode.Api externalError, String addString) {
		code = externalError.getCode();
		desc = externalError.getDesc() + " - " + addString;
	}

}
