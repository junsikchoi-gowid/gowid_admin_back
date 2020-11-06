package com.nomadconnection.dapp.api.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapingLogDto {

	private String email;
	private String code;
	private String message;
	private String extraMessage;

}
