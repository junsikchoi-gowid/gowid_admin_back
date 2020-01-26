package com.nomadconnection.dapp.api.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Builder
@Accessors(fluent = true)
public class MismatchedException extends RuntimeException {

	public enum Category {
		PASSWORD, VERIFICATION_CODE, VALID_THRU, JWT_SUBJECT, CORP, CARD, OWNER,
	}

	private Category category;
	private Object object;
}