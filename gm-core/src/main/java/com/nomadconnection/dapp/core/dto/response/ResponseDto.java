package com.nomadconnection.dapp.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseDto {

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Idx {

		private Long idx;
	}
}
