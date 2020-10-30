package com.nomadconnection.dapp.api.v2.dto;

import com.nomadconnection.dapp.api.dto.shinhan.enums.ImageFileType;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ImageReqDto {

	private final CardCompany cardCompany;
	private final ImageFileType imageFileType;

}
