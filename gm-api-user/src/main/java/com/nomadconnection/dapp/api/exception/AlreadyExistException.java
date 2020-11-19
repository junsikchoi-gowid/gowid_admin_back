package com.nomadconnection.dapp.api.exception;

import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Getter
@Accessors(fluent = true)
public class AlreadyExistException extends RuntimeException {

	private final HttpStatus status;

	private final String category;
	private final String resource;

	public AlreadyExistException(ErrorCode.Api errorCodeType){
		status = HttpStatus.CONFLICT;
		category = errorCodeType.getCode();
		resource = errorCodeType.getDesc();
	}

	@Builder
	public AlreadyExistException(String message, HttpStatus status, String category, String resource) {
		super(message);
		this.status = status != null ? status : HttpStatus.BAD_REQUEST;
		this.category = category;
		this.resource = resource;
	}
}
