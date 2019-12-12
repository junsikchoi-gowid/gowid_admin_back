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
public class UserProfileResx {

	private Long profileResxSize; // 파일크기
	private String profileResxFilename; // 파일명
	private String profileResxFilenameOrigin; // 파일명(원본)
}
