package com.nomadconnection.dapp.api.exception.v2.base;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BaseException extends RuntimeException {

	private final Result result;
	private final Object data;

	public BaseException(String code, String desc) {
		this.result = Result.builder().code(code).desc(desc).build();
		this.data = null;
	}

	public BaseException(String code, String desc, String extraMessage) {
		this.result = Result.builder().code(code).desc(desc).extraMessage(extraMessage).build();
		this.data = null;
	}

	@Getter
	@Builder
	public static class Result {
		private String code;
		private String desc;
		private String extraMessage;
	}

}
