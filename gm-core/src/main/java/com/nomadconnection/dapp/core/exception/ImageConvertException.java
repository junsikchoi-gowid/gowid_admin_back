package com.nomadconnection.dapp.core.exception;


import com.nomadconnection.dapp.core.exception.error.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ImageConvertException extends RuntimeException {

	private String code;
	private String desc;

	public ImageConvertException(ErrorMessage errorMessage){
		super(errorMessage.getCode());
		code = errorMessage.getCode();
		desc = errorMessage.getDesc();
	}

	public ImageConvertException(ErrorMessage errorMessage, Throwable e){
		super(errorMessage.getCode(), e);
		code = errorMessage.getCode();
		desc = errorMessage.getDesc();
	}

}
