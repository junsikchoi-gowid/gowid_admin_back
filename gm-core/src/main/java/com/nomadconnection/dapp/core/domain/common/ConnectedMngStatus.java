package com.nomadconnection.dapp.core.domain.common;

import lombok.*;

@Getter
@AllArgsConstructor
public enum ConnectedMngStatus{
	NORMAL("정상"),
	ERROR("오류"),
	DELETE("삭제"),
	STOP("중지");

	private String status;
}
