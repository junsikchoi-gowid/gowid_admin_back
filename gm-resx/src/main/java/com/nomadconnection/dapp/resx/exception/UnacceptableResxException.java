package com.nomadconnection.dapp.resx.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class UnacceptableResxException extends RuntimeException {

	private final String name;
	private final MultipartFile resx;

	@Builder
	public UnacceptableResxException(String message, Throwable cause, String name, MultipartFile resx) {
		super(message, cause);
		this.name = name;
		this.resx = resx;
	}
}
