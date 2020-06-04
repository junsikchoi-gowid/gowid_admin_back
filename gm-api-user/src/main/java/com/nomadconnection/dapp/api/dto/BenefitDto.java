package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.Benefit;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class BenefitDto {

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BenefitReq {

		@ApiModelProperty("제목")
		@NotEmpty
		private String title;

		@ApiModelProperty("상세내용")
		@NotNull
		private String content;

		@ApiModelProperty("개요")
		@NotEmpty
		private String summary;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BenefitRes {

		@ApiModelProperty("식별자")
		private Long idx;

		@ApiModelProperty("제목")
		private String title;

		@ApiModelProperty("상세내용")
		private String content;

		@ApiModelProperty("개요")
		private String summary;

		@ApiModelProperty("파일명(원본)")
		private String orgfname;

		@ApiModelProperty("파일명")
		private String fname;

		@ApiModelProperty("파일크기")
		private Long fsize;

		@ApiModelProperty("s3링크주소")
		private String s3Link;

		public static BenefitRes from(Benefit benefit) {
			if (benefit != null) {
				return BenefitRes.builder()
						.idx(benefit.idx())
						.title(benefit.title())
						.content(benefit.content())
						.summary(benefit.summary())
						.orgfname(benefit.orgfname())
						.fname(benefit.fname())
						.fsize(benefit.size())
						.s3Link(benefit.s3Link())
						.build();
			}
			return null;
		}
	}
}
