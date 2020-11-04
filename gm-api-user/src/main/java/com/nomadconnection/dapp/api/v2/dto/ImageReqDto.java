package com.nomadconnection.dapp.api.v2.dto;

import com.nomadconnection.dapp.api.dto.shinhan.enums.ImageFileType;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageReqDto {

	private CardCompany cardCompany;

	private ImageFileType imageFileType;

}
