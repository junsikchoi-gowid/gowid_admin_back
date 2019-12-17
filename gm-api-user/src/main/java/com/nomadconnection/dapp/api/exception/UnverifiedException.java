package com.nomadconnection.dapp.api.exception;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@Builder
@RequiredArgsConstructor
public class UnverifiedException extends RuntimeException {

	public enum Resource {
		@ApiModelProperty("CARD VERIFICATION CODE") CVC,
		@ApiModelProperty("CARD VALID THRU") CVT,
	}

	private final Long idx;
	private final Resource resource;
}
