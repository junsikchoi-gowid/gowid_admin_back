package com.nomadconnection.dapp.api.exception.api;

import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotRegisteredException extends RuntimeException {
	private String code;
	private String desc;

	public NotRegisteredException(ErrorCode.Api errorCodeType) {
		code = errorCodeType.getCode();
		desc = errorCodeType.getDesc();
	}

	public NotRegisteredException(ErrorCode.Api errorCodeType, String addString) {
		code = errorCodeType.getCode();
		desc = errorCodeType.getDesc() + " - " + addString;
	}

}
