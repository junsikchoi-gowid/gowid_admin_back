package com.nomadconnection.dapp.api.exception.v2;


import com.nomadconnection.dapp.api.exception.v2.base.BaseException;
import com.nomadconnection.dapp.api.exception.v2.base.Results;

public class ResourceNotFoundException extends BaseException {

	public ResourceNotFoundException(String code, String desc, String extraMessage) {
		super(code, desc, extraMessage);
	}

	public ResourceNotFoundException(Results results) {
		super(results.getCode(), results.getDesc(), results.getExtraMessage());
	}

}
