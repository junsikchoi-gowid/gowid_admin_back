package com.nomadconnection.dapp.api.v2.dto.kised;

import com.nomadconnection.dapp.core.domain.kised.ConfirmationFile;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationFileResponse {

	@ApiModelProperty("파일 식별자")
	private Long fileIdx;

	@ApiModelProperty("파일명")
	private String name;

	@ApiModelProperty("파일명(원본)")
	private String orgName;

	@ApiModelProperty("파일크기")
	private Long size;

	@ApiModelProperty("s3링크")
	private String s3Link;

	@ApiModelProperty("gw전송여부")
	private Boolean isTransferToGw;

	public static ConfirmationFileResponse from(ConfirmationFile file) {

		return ConfirmationFileResponse.builder()
			.fileIdx(file.getIdx())
			.name(file.getFileName())
			.orgName(file.getOrgFileName())
			.size(file.getSize())
			.s3Link(file.getS3Link())
			.isTransferToGw(file.getIsTransferToGw())
			.build();
	}
}
