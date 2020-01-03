package com.nomadconnection.dapp.api.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
// import org.springframework.http.HttpStatus;

@Getter
@Builder
@RequiredArgsConstructor
public class BusinessException extends RuntimeException {

	// private final HttpStatus status;
	private final String category;
	private final String resource;

	private final Boolean resFlag;
	private final String businessErrorMessage;

//	@Builder
//	public BusinessException(String message, HttpStatus status, String category, String resource , Boolean resFlag , String businessErrorMessage ) {
//		super(message);
//		this.status = status != null ? status : HttpStatus.BAD_REQUEST;
//		this.category = category;
//		this.resource = resource;
//
//		this.resFlag = resFlag;
//		this.businessErrorMessage = businessErrorMessage;
//	}
}
