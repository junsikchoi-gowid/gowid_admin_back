package com.nomadconnection.dapp.api.v2.dto.kised;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nomadconnection.dapp.api.dto.shinhan.DataPart1710;
import com.nomadconnection.dapp.core.domain.kised.ConfirmationFile;
import com.nomadconnection.dapp.core.domain.kised.Kised;
import com.nomadconnection.dapp.core.encryption.shinhan.Seed128;
import com.nomadconnection.dapp.core.utils.NumberUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@JsonInclude
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class KisedResponseDto {

	private final String projectId;
	private final String projectName;
	private final String startDate;
	private final String endDate;
	private final String licenseNo;
	private String orgName;
	private final Long cash;
	private Long spot;
	private final String bankCode;
	private final String accountNo;
	private final String accountHolder;
	private final KisedFileResponseDto kisedFileResponse;

	public static KisedResponseDto from(DataPart1710 dataPart1710){
		KisedResponseDto kisedResponseDto = null;
		String accountNo;

		if(dataPart1710 != null){
			accountNo = Seed128.decryptEcb(dataPart1710.getD010()).substring(0, 12); // 신한은행 계좌

			kisedResponseDto = KisedResponseDto.builder()
				.licenseNo(dataPart1710.getD001())
				.projectId(dataPart1710.getD002())
				.projectName(dataPart1710.getD003())
				.startDate(dataPart1710.getD004())
				.endDate(dataPart1710.getD005())
				.orgName(dataPart1710.getD006())
				.cash(NumberUtils.stringToLong(dataPart1710.getD007()))
				.spot(NumberUtils.stringToLong(dataPart1710.getD008()))
				.bankCode(dataPart1710.getD009())
				.accountNo(accountNo)
				.accountHolder(dataPart1710.getD011())
				.build();
		}

		return kisedResponseDto;
	}

	public static KisedResponseDto from(Kised kised){
		KisedResponseDto kisedResponseDto = KisedResponseDto.builder().build();
		if(kised != null){
			kisedResponseDto = KisedResponseDto.builder()
				.licenseNo(kised.getLicenseNo())
				.projectId(kised.getProjectId())
				.projectName(kised.getProjectName())
				.startDate(kised.getStartDate())
				.endDate(kised.getEndDate())
				.orgName(kised.getOrgName())
				.cash(kised.getCash())
				.spot(kised.getSpot())
				.bankCode(kised.getBankCode())
				.accountNo(kised.getAccountNo())
				.accountHolder(kised.getAccountHolder())
				.kisedFileResponse(KisedFileResponseDto.from(kised.getConfirmationFile()))
				.build();
		}

		return kisedResponseDto;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class KisedFileResponseDto {

		@ApiModelProperty("파일 식별자")
		private Long confirmationFileIdx;

		@ApiModelProperty("파일명")
		private String name;

		@ApiModelProperty("파일명(원본)")
		private String orgName;

		@ApiModelProperty("파일크기")
		private Long size;

		@ApiModelProperty("s3링크")
		private String s3Link;

		public static KisedFileResponseDto from(ConfirmationFile confirmationFile){
			KisedFileResponseDto response = KisedFileResponseDto.builder().build();

			if(confirmationFile != null){
				response = KisedFileResponseDto.builder()
					.confirmationFileIdx(confirmationFile.getIdx())
					.name(confirmationFile.getFileName())
					.orgName(confirmationFile.getOrgFileName())
					.size(confirmationFile.getSize())
					.s3Link(confirmationFile.getS3Link())
					.build();
			}
			return response;
		}
	}

}
