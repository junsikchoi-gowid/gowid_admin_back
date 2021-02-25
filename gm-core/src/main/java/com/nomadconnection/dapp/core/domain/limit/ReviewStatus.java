package com.nomadconnection.dapp.core.domain.limit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewStatus {
	REQUESTED("접수완료"),
	CANCEL("접수취소"),
	REVIEWING("심사중"),
	REJECT("심사완료-부결"),
	APPROVED("심사완료-승인");

	private final String status;

}
