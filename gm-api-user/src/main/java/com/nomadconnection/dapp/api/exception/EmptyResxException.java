package com.nomadconnection.dapp.api.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@RequiredArgsConstructor
public class EmptyResxException extends RuntimeException {

	private final MultipartFile resx;

	@Builder
	public EmptyResxException(String message, Throwable cause, MultipartFile resx) {
		super(message, cause);
		this.resx = resx;
	}
}
