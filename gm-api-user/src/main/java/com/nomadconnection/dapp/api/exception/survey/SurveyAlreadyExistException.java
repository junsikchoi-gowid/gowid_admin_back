package com.nomadconnection.dapp.api.exception.survey;

import com.nomadconnection.dapp.api.exception.AlreadyExistException;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.Getter;

@Getter
public class SurveyAlreadyExistException extends AlreadyExistException {

	private String code;
	private String message;

	public SurveyAlreadyExistException(ErrorCode.Api errorCodeType) {
		super(errorCodeType);
		code = errorCodeType.getCode();
		message = errorCodeType.getDesc();
	}

}
