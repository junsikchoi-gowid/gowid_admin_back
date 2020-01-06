package com.nomadconnection.dapp.core.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@SuppressWarnings({"unused"})
public class ErrorResponse {

	@Getter
	@Builder
	public static class FieldError {
		private String field;
		private String value;
		private String reason;
	}

	private String category;
	private String error;
	private String description;
	private List<FieldError> fieldErrors;
	private LocalDateTime current;

	@Builder
	public ErrorResponse(String category, String error, String description, List<FieldError> fieldErrors) {
		this.category = category;
		this.error = error;
		this.description = description;
		this.fieldErrors = fieldErrors;
		this.current = LocalDateTime.now();
	}

	public static ErrorResponse from(ErrorCodeDescriptor descriptor) {
		return ErrorResponse.from(descriptor, null);
	}

	public static ErrorResponse from(ErrorCodeDescriptor descriptor, List<FieldError> errors) {
		return ErrorResponse.builder()
				.category(descriptor.category())
				.error(descriptor.error())
				.description(descriptor.description())
				.fieldErrors(errors)
				.build();
	}


	public static ErrorResponse from(String value, String reason) {
		return ErrorResponse.builder()
				.error(value)
				.description(reason)
				.build();
	}
}
