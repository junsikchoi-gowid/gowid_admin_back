package com.nomadconnection.dapp.core.domain.embed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Data
@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

	private String bankAccount; // 계좌번호
	private String bankAccountHolder; // 예금주
	private String bankName; // 은행명
}
