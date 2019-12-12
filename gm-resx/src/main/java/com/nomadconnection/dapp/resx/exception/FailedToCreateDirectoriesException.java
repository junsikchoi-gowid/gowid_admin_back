package com.nomadconnection.dapp.resx.exception;

import lombok.Builder;
import lombok.Getter;

import java.nio.file.Path;

@Getter
public class FailedToCreateDirectoriesException extends RuntimeException {

	private final Path path;

	@Builder
	public FailedToCreateDirectoriesException(String message, Throwable cause, Path path) {
		super(message, cause);
		this.path = path;
	}
}
