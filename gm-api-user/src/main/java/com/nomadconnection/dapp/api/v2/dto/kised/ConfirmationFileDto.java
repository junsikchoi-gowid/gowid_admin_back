package com.nomadconnection.dapp.api.v2.dto.kised;

import com.nomadconnection.dapp.core.domain.kised.ConfirmationFile;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationFileDto {

	private String orgFileName;

	private String fileName;

	private Long size;

	@Setter
	private String s3Link;

	private String s3Key;

	@Setter
	private Boolean isTransferToGw;

	public static ConfirmationFileDto of(MultipartFile file, String s3Key, String fileName){
		return ConfirmationFileDto.builder()
			.size(file.getSize())
			.s3Key(s3Key)
			.orgFileName(file.getOriginalFilename())
			.fileName(fileName)
			.s3Link("")
			.isTransferToGw(false)
			.build();
	}

	public static ConfirmationFile toEntity(ConfirmationFileDto dto){
		return ConfirmationFile.builder()
			.orgFileName(dto.getOrgFileName())
			.fileName(dto.getFileName())
			.size(dto.getSize())
			.s3Link(dto.getS3Link())
			.s3Key(dto.getS3Key())
			.isTransferToGw(dto.getIsTransferToGw())
			.build();
	}

}
