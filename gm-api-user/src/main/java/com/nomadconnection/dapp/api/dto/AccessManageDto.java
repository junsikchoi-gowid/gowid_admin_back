package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.common.CommonCodeDetail;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.common.ConnectedMngStatus;
import com.nomadconnection.dapp.core.domain.res.ResConCorpList;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessManageDto {

	@ApiModelProperty("패스워드")
	private String password1;

	@ApiModelProperty("인증서이름")
	private String name;

	@ApiModelProperty("발급일")
	private String startDate;

	@ApiModelProperty("만료일")
	private String endDate;

	@ApiModelProperty("설명1.")
	private String desc1;

	@ApiModelProperty("설명2.")
	private String desc2;

	@ApiModelProperty("certFile")
	private String certFile;

	@ApiModelProperty("기관코드")
	private String organization;

	@ApiModelProperty("기관타입")
	private String businessType;

	@ApiModelProperty("인증서 타입")
	private String type;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ResConCorpStatusDto {

		@ApiModelProperty("기관코드")
		private String organization;

		@ApiModelProperty("idxConnectedMngs")
		private List<Long> idxConnectedMngs;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ResConCorpListDto {
		@ApiModelProperty("idx")
		private Long idx;

		@ApiModelProperty("idxCorp")
		private Long idxCorp;

		@ApiModelProperty("connectedId")
		private String connectedId;

		@ApiModelProperty("idx ConnectedId List")
		private List<Long> idxConnectedIdList;

		@ApiModelProperty("기관타입")
		private String businessType;

		@ApiModelProperty("고객 구분")
		private String clientType;

		@ApiModelProperty("결과코드")
		private String code;

		@ApiModelProperty("로그인구분")
		private String loginType;

		@ApiModelProperty("국가코드")
		private String countryCode;

		@ApiModelProperty("기관코드")
		private String organization;

		@ApiModelProperty("추가 메시지")
		private String extraMessage;

		@ApiModelProperty("메시지")
		private String message;

		@ApiModelProperty("사용유무")
		private ConnectedMngStatus status;

		public static AccessManageDto.ResConCorpListDto from(ResConCorpList dto){
			return ResConCorpListDto.builder()
					.idxCorp(dto.idxCorp())
					.connectedId(dto.connectedId())
					.clientType(dto.clientType())
					.code(dto.code())
					.loginType(dto.loginType())
					.countryCode(dto.countryCode())
					.organization(dto.organization())
					.extraMessage(dto.extraMessage())
					.message(dto.message())
					.status(dto.status())
					.build();
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Account{
		@ApiModelProperty("idx")
		private Long idx;

		@ApiModelProperty("패스워드")
		private String password1;

		@ApiModelProperty("인증서이름")
		private String name;

		@ApiModelProperty("발급일")
		private String startDate;

		@ApiModelProperty("만료일")
		private String endDate;

		@ApiModelProperty("설명1.")
		private String desc1;

		@ApiModelProperty("설명2.")
		private String desc2;

		@ApiModelProperty("certFile")
		private String certFile;

		@ApiModelProperty("기관정보")
		private String corpCode;

		@ApiModelProperty("인증서 타입")
		private CommonCodeType type;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AccessCodeType {

		@ApiModelProperty("지역코드")
		private String code;

		@ApiModelProperty("지역명")
		private String name;

		public static AccessManageDto.AccessCodeType from(CommonCodeDetail code) {
			if (code != null) {
				return AccessCodeType.builder()
						.code(code.code1())
						.name(code.value1())
						.build();
			}
			return null;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AccessInfoAll{
		@ApiModelProperty("connectedId")
		List<ConnectedMngDto> connectedMngDto;

		@ApiModelProperty("accessInfo")
		List<ResConCorpListDto> resConCorpListDtoList;
	}
}
