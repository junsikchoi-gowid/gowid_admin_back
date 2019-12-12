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
public class CorpStockholdersListResx {

	private Long resxStockholdersListSize; // 파일크기

	private String resxStockholdersListPath; // 파일경로
	private String resxStockholdersListFilenameOrigin; // 파일명(원본)
}
