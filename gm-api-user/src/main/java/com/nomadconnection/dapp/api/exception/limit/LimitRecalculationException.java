package com.nomadconnection.dapp.api.exception.limit;

import com.nomadconnection.dapp.api.exception.AlreadyExistException;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.Getter;

@Getter
public class LimitRecalculationException extends AlreadyExistException {

	private String code;
	private String message;

	public LimitRecalculationException(ErrorCode.Api errorCodeType) {
		super(errorCodeType);
		code = errorCodeType.getCode();
		message = errorCodeType.getDesc();
	}

	public LimitRecalculationException(ErrorCode.Api errorCodeType, String extraMessage) {
		super(errorCodeType);
		code = errorCodeType.getCode();
		message = errorCodeType.getDesc() + " : " + extraMessage;
	}

}
