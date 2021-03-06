package com.nomadconnection.dapp.api.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Builder
@Accessors(fluent = true)
public class FileUploadException extends RuntimeException {

	public enum Category {
		UPLOAD_STOCKHOLDER_FILE,
		UPLOAD_CONFIRMATION_FILE
	}

	private Category category;
}
