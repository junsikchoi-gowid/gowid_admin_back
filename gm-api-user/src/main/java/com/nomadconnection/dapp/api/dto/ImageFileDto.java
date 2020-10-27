package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.api.dto.shinhan.enums.ImageFileType;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageFileDto {
	private CardCompany cardCompany;
	private ImageFileType imageFileType;
	private String imageJson;
	private String fileName;
	private String licenseNo;
}
