package com.nomadconnection.dapp.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageConvertRespDto {

	private boolean isSuccess;

	private int totalPageCount;

}
