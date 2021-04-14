package com.nomadconnection.dapp.api.exception.shinhan;

import lombok.*;

@Getter
@Builder
@RequiredArgsConstructor
public class ShinhanInternalException extends RuntimeException {

	private final String code;
	private final String desc;
	private final String shinhanMessage;

}
