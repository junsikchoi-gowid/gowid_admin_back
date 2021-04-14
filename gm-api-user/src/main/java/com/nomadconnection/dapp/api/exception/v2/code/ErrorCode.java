package com.nomadconnection.dapp.api.exception.v2.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// Http Status 4xx
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "1002", "resource not found"),
	DUPLICATED_RESOURCE(HttpStatus.CONFLICT, "1003", "resource is duplicated");

	private final HttpStatus status;
	private final String code;
	private final String desc;

}
