package com.nomadconnection.dapp.api.dto.shinhan.enums;

import lombok.Getter;

@Getter
public enum ImageFileType {

	CORP_LICENSE(1510),
	FINANCIAL_STATEMENTS(1520),
	CORP_REGISTRATION(1530),
	GUARANTEE(9991),
	ETC(9999);

	private int fileType;

	ImageFileType(int fileType){
		this.fileType = fileType;
	}



}
