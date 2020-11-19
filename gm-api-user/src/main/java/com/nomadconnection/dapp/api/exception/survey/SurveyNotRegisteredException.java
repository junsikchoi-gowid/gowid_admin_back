package com.nomadconnection.dapp.api.exception.survey;

import com.nomadconnection.dapp.api.exception.api.NotRegisteredException;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.Getter;

@Getter
public class SurveyNotRegisteredException extends NotRegisteredException {

	private String code;
	private String message;

	public SurveyNotRegisteredException(ErrorCode.Api errorCodeType) {
		super(errorCodeType);
		code = errorCodeType.getCode();
		message = errorCodeType.getDesc();
	}

}
