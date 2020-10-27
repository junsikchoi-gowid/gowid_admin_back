package com.nomadconnection.dapp.api.v2.exception;

import com.nomadconnection.dapp.api.v2.exception.error.ErrorMessage;

public class ImageConvertException extends RuntimeException {

	private String code;
	private String desc;

	public ImageConvertException(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public ImageConvertException(ErrorMessage errorMessage){
		this(errorMessage.getCode(), errorMessage.getDesc());
	}
}
