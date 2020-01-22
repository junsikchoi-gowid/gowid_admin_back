package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.MemberAuthority;
import com.nomadconnection.dapp.core.domain.User;
import com.nomadconnection.dapp.core.domain.embed.Address;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorpDto {

	@ApiModelProperty("식별자(법인)")
	private Long idx;

	@ApiModelProperty("법인명")
	private String name;

	@ApiModelProperty("사업자등록번호(10 Digits)")
	private String bizRegNo;

//	@ApiModelProperty("법인인감증명서")
//	private String uriRegSeal;

	private String resBusinessItems;
	private String resBusinessTypes;
	private String resBusinessmanType;
	private String resCompanyIdentityNo;
	private String resCompanyNm;
	private String resIssueNo;
	private String resIssueOgzNm;
	private String resJointIdentityNo;
	private String resJointRepresentativeNm;
	private String resOpenDate;
	private String resOriGinalData;
	private String resRegisterDate;
	private String resUserAddr;
	private String resUserIdentiyNo;
	private String resUserNm;

	@ApiModelProperty("카드회사 etc. 삼성/현대")
	private String typeOfCardCorp;

	@ApiModelProperty("수령지(주소)")
	private Address recipientAddress;

	@ApiModelProperty("법인총한도")
	private Long creditLimit;



	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CorpRegister { // 법인정보등록

		@ApiModelProperty("법인명")
		private String name;

		@ApiModelProperty("사업자등록번호")
		private String bizRegNo;

//		@ApiModelProperty("법인인감증명서")
//		private MultipartFile resxRegSeal;

		private String resBusinessItems;
		private String resBusinessTypes;
		private String resBusinessmanType;
		private String resCompanyIdentityNo;
		private String resCompanyNm;
		private String resIssueNo;
		private String resIssueOgzNm;
		private String resJointIdentityNo;
		private String resJointRepresentativeNm;
		private String resOpenDate;
		private String resOriGinalData;
		private String resRegisterDate;
		private String resUserAddr;
		private String resUserIdentiyNo;
		private String resUserNm;

		@ApiModelProperty("카드회사 etc. 삼성/현대")
		private String typeOfCardCorp;

		@ApiModelProperty("희망법인총한도")
		private Long reqCreditLimit;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CorpAuthCert { // 법인공인인증

		@ApiModelProperty("법인명")
		private String name;

		@ApiModelProperty("사업자등록번호")
		private String bizRegNo;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CorpMember {

		@ApiModelProperty("식별자(사용자)")
		private Long idxUser;

		@ApiModelProperty("식별자(부서)")
		private Long idxDept;

		@ApiModelProperty("식별자(카드)")
		private Long idxCard;

		@ApiModelProperty("권한(마스터/어드민/멤버)")
		private MemberAuthority authority;

		@ApiModelProperty("이메일")
		private String email;

		@ApiModelProperty("이름")
		private String name;

		@ApiModelProperty("연락처")
		private String mdn;

		@ApiModelProperty("부서명")
		private String dept;

		@ApiModelProperty("카드번호")
		private String cardNo;

		@ApiModelProperty("경로(이미지)")
		private String uriProfileImage;

		@ApiModelProperty("월한도")
		private Long creditLimit;

		public static CorpMember from(User user) {
			CorpMember member = CorpMember.builder()
					.idxUser(user.idx())
					.authority(MemberAuthority.from(user.authorities()))
					.email(user.email())
					.name(user.name())
					.mdn(user.mdn())
					.creditLimit(user.creditLimit())
					.build();
			if (MemberAuthority.MASTER.equals(member.authority) && user.corp() != null) {
				member.setCreditLimit(user.corp().creditLimit());
			}
			if (user.dept() != null) {
				member.setIdxDept(user.dept().idx());
				member.setDept(user.dept().name());
			}
			if (user.profileResx() != null) {
				//
				//	todo: make or get profile image uri
				//
			}
			return member;
		}
	}
}
