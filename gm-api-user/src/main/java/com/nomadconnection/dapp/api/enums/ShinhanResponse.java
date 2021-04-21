package com.nomadconnection.dapp.api.enums;

import com.nomadconnection.dapp.api.dto.shinhan.enums.ShinhanInterfaceId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ShinhanResponse {

	// 1710
	SH1710_SUCCESS(ShinhanInterfaceId.SH1710, "00", "success"),
	SH1710_PARAMETER_NOT_EXISTS(ShinhanInterfaceId.SH1710, "01", "parameter does not exists"),
	SH1710_NOT_MATCHED_PROJECT_ID(ShinhanInterfaceId.SH1710, "02", "projectId is not matched"),
	SH1710_SHINHAN_INTERNAL_ERROR(ShinhanInterfaceId.SH1710, "03", "shinhan internal error"),
	SH1710_ACCOUNT_NOT_EXISTS(ShinhanInterfaceId.SH1710, "04", "account does not exists"),
	SH1710_INVALID_PROJECT(ShinhanInterfaceId.SH1710, "05", "It's a invalid project"),

	// DEFAULT
	SH_DEFAULT(null, "99", "code does not exists")
	;

	private final ShinhanInterfaceId interfaceId;
	private final String responseCode;
	private final String responseMessage;

	public static ShinhanResponse findByInterfaceIdAndResponseCode(ShinhanInterfaceId interfaceId, String responseCode) {
		return Arrays.stream(ShinhanResponse.values())
			.filter(
				response -> response.getInterfaceId().equals(interfaceId) && response.getResponseCode().equals(responseCode))
			.findFirst()
			.orElse(ShinhanResponse.SH_DEFAULT);
	}

}
