package com.nomadconnection.dapp.resx.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

@Getter
@Builder
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class FailedToSaveException extends RuntimeException {

	private final Throwable cause;
	private final Path path;

	public FailedToSaveException(String message, Throwable cause, Path path) {
		super(message, cause);
		this.path = path;
		this.cause = cause;
	}
}
