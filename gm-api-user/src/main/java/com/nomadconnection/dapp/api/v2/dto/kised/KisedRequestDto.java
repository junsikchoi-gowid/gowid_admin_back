package com.nomadconnection.dapp.api.v2.dto.kised;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class KisedRequestDto {

	private final Long cardIssuanceInfoIdx;

	@Length(max = 10)
	@ApiModelProperty(value = "사업자번호", example = "2618125793")
	private final String licenseNo;

	@Length(max = 8)
	@ApiModelProperty(value = "창진원 과제번호", example = "10346785")
	private final String projectId;

}
