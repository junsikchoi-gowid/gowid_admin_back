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
public class Address {

	private String addressZipCode; // 우편번호
	private String addressBasic; // 기본주소
	private String addressDetails; // 상세주소
}
