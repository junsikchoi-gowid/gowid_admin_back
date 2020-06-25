package com.nomadconnection.dapp.api.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Builder
@Accessors(fluent = true)
public class FileUploadException extends RuntimeException {

	public enum Category {
		STOCKHOLDER
	}

	private Category category;
}
