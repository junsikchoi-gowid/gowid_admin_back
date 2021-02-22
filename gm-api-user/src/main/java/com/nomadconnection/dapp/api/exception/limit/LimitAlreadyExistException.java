package com.nomadconnection.dapp.api.exception.limit;

import com.nomadconnection.dapp.api.exception.AlreadyExistException;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.Getter;

@Getter
public class LimitAlreadyExistException extends AlreadyExistException {

	private String code;
	private String message;

	public LimitAlreadyExistException(ErrorCode.Api errorCodeType) {
		super(errorCodeType);
		code = errorCodeType.getCode();
		message = errorCodeType.getDesc();
	}

}
