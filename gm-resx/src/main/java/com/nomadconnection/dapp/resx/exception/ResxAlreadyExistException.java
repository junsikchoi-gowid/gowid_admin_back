package com.nomadconnection.dapp.resx.exception;

import lombok.Builder;
import lombok.Getter;

import java.nio.file.Path;

@Getter
public class ResxAlreadyExistException extends RuntimeException {

	private final Path path;

	@Builder
	public ResxAlreadyExistException(String message, Path path) {
		super(message);
		this.path = path;
	}
}
