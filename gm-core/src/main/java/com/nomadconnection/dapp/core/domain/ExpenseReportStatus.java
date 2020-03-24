package com.nomadconnection.dapp.core.domain;

@SuppressWarnings("unused")
public enum ExpenseReportStatus {

	PENDING, // 미제출
	SUBMITTED, // 제출(승인대기)
	REJECTED, // 반려
	APPROVED, // 승인
}
