package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.Corp;
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

	public static CorpDto from(Corp corp) {
		CorpDto corpDto = CorpDto.builder()
				.resBusinessItems(corp.resBusinessItems())
				.resBusinessTypes(corp.resBusinessTypes())
				.resBusinessmanType(corp.resBusinessmanType())
				.resCompanyIdentityNo(corp.resCompanyIdentityNo())
				.resCompanyNm(corp.resCompanyNm())
				.resIssueNo(corp.resIssueNo())
				.resIssueOgzNm(corp.resIssueOgzNm())
				.resJointIdentityNo(corp.resJointIdentityNo())
				.resJointRepresentativeNm(corp.resJointRepresentativeNm())
				.resOpenDate(corp.resOpenDate())
				.resOriGinalData(corp.resOriGinalData())
				.resRegisterDate(corp.resRegisterDate())
				.resUserAddr(corp.resUserAddr())
				.resUserIdentiyNo(corp.resUserIdentiyNo())
				.resUserNm(corp.resUserNm())
				.build();
		return corpDto;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CorpRegister { // 법인정보등록

//		@ApiModelProperty("법인명")
//		private String name;
//
//		@ApiModelProperty("사업자등록번호")
//		private String bizRegNo;

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
