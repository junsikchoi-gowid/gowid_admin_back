package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.ConnectedMng;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class ConnectedMngDto {


	@ApiModelProperty("식별자")
	private Long idx;

	@ApiModelProperty("인증서이름")
	private String name;

	@ApiModelProperty("발급일자")
	private String startDate;

	@ApiModelProperty("종료일자")
	private String endDate;

	@ApiModelProperty("설명1")
	private String desc1;

	@ApiModelProperty("설명2")
	private String desc2;

	@ApiModelProperty("connectedId")
	private String connectedId;

	@ApiModelProperty("식별자(사용자)")
	private Long idxUser;

	public static ConnectedMngDto from(ConnectedMng connectedMng){
		return ConnectedMngDto.builder()
				.idx(connectedMng.idx())
				.connectedId(connectedMng.connectedId())
				.name(connectedMng.name())
				.startDate(connectedMng.startDate())
				.endDate(connectedMng.endDate())
				.desc1(connectedMng.desc1())
				.desc2(connectedMng.desc2())
				.build();
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ConnectedMngMember{

		@ApiModelProperty("식별자")
		private Long idx;

		@ApiModelProperty("connectedId")
		private String connectedId;

		@ApiModelProperty("식별자(사용자)")
		private Long idxUser;

		public static ConnectedMngMember from(ConnectedMng connectedMng){
			ConnectedMngMember member = ConnectedMngMember.builder()
					.idx(connectedMng.idx())
					.connectedId(connectedMng.connectedId())
					.idxUser(connectedMng.idxUser())
					.build();

			return member;
		}
	}



	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FindAccount {

		@ApiModelProperty("이름")
		private String name;

		@ApiModelProperty("연락처")
		private String mdn;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ConnectedIdList {

		@ApiModelProperty("인증키")
		private String key;

		@ApiModelProperty("비밀번호")
		private String password;

		@Override
		public String toString() {
			return String.format("%s(key=%s, password=********)", getClass().getSimpleName(), key);
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ConnectedId {
		@ApiModelProperty("der 파일 위치")
		private String derPath;

		@ApiModelProperty("key 파일 위치")
		private String keyPath;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Account {

		@ApiModelProperty("패스워드")
		private String password1;

		@ApiModelProperty("인증서이름")
		private String name;

		@ApiModelProperty("발급일")
		private String startDate;

		@ApiModelProperty("만료일")
		private String endDate;

		@ApiModelProperty("설명1")
		private String desc1;

		@ApiModelProperty("설명2")
		private String desc2;

		@ApiModelProperty("certFile")
		private String certFile;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Account2 {

		@ApiModelProperty("패스워드")
		private String password1;

		@ApiModelProperty("인증서이름")
		private String name;

		@ApiModelProperty("발급일")
		private String startDate;

		@ApiModelProperty("만료일")
		private String endDate;

		@ApiModelProperty("설명1")
		private String desc1;

		@ApiModelProperty("설명2")
		private String desc2;

		@ApiModelProperty("der")
		private String derPath;

		@ApiModelProperty("key")
		private String keyPath;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DeleteAccount {
		@ApiModelProperty("idxConnectedId")
		private Long idxConnectedId;
	}
}
