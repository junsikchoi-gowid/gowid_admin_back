package com.nomadconnection.dapp.api.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {

	private final String entity;
	private final Long idx;

	@Builder
	public EntityNotFoundException(String message, String entity, Long idx) {
		super(message);
		this.entity = entity;
		this.idx = idx;
	}
}
