package com.nomadconnection.dapp.api.exception;

import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CodeNotFoundException extends RuntimeException {

	private CommonCodeType code;
	private String desc;

	public CodeNotFoundException(CommonCodeType code){
		this.code = code;
		this.desc = getDesc(code);
	}

	private String getDesc(CommonCodeType code){
		StringBuilder descBuilder = new StringBuilder();
		return descBuilder.append("[").append(code).append("]").append("not found code").toString();
	}

}
