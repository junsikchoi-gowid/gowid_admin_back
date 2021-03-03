package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.corp.CorpBranch;
import com.nomadconnection.dapp.core.domain.user.MemberAuthority;
import com.nomadconnection.dapp.core.domain.user.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorpDto {

	@ApiModelProperty(value = "식별자", example = "1")
	private Long idx;

	@ApiModelProperty(value = "종목", example = "응용 소프트웨어 개발 및 공급업|전자상거래 소매업|전자상거래 소매 중개업|컴퓨터 및 사무용 기계ㆍ장비 임대업|경영컨설팅")
	private String resBusinessItems;

	@ApiModelProperty(value = "업태", example = "정보통신업|도매 및 소매업|도매 및 소매업|사업시설 관리, 사업지원 및 임대 서비스업|서비스")
	private String resBusinessTypes;

	@ApiModelProperty(value = "사업자종류", example = "법인사업자")
	private String resBusinessmanType;

	@ApiModelProperty(value = "사업자등록번호", example = "261-81-25793")
	private String resCompanyIdentityNo;

	@ApiModelProperty(value = "법인명", example = "주식회사 고위드(GOWID Inc)")
	private String resCompanyNm;

	@ApiModelProperty(value = "발급(승인)번호", example = "8409-997-8151-772")
	private String resIssueNo;

	@ApiModelProperty(value = "발급기관", example = "강남세무서")
	private String resIssueOgzNm;

	@ApiModelProperty(value = "공동사업자 주민번호", example = "111-11-11111")
	private String resJointIdentityNo;

	@ApiModelProperty(value = "공동사업자 성명(법인명)", example = "주식회사 고위도")
	private String resJointRepresentativeNm;

	@ApiModelProperty(value = "개업일", example = "20150210")
	private String resOpenDate;

	@ApiModelProperty(value = "원문 DATA", example = "")
	private String resOriGinalData;

	@ApiModelProperty(value = "사업자등록일", example = "20150213")
	private String resRegisterDate;

	@ApiModelProperty(value = "사업장소재지(주소)", example = "서울특별시 강남구 도산대로 317, 14층(신사동, 호림아트센터 1빌딩)")
	private String resUserAddr;

	@ApiModelProperty(value = "주민(법인)등록번호", example = "110111-5639343")
	private String resUserIdentiyNo;

	@ApiModelProperty(value = "성명(대표자)", example = "김항기")
	private String resUserNm;

	public static CorpDto from(Corp corp) {
		return CorpDto.builder()
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

			if (user.profileResx() != null) {
				//
				//	todo: make or get profile image uri
				//
			}
			return member;
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CorpBranchDto {

		@ApiModelProperty(value = "식별자", example = "1")
		private Long idx;

		@ApiModelProperty(value = "사업자번호", example = "398-86-00876")
		private String resCompanyIdentityNo;

		@ApiModelProperty(value = "법인명", example = "주식회사 고위드(GOWID Inc)")
		private String resCompanyNm;

		@ApiModelProperty(value = "식별자(법인)", example = "101")
		private Long idxCorp;

		public static CorpBranchDto from(CorpBranch corpBranch) {
			return CorpBranchDto.builder()
					.idx(corpBranch.idx())
					.idxCorp(corpBranch.corp().idx())
					.resCompanyNm(corpBranch.resCompanyNm())
					.resCompanyIdentityNo(corpBranch.resCompanyIdentityNo())
					.build();
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CorpInfoDto {

		@ApiModelProperty("기존법인정보")
		private CorpDto corpDto;
		private List<CorpBranchDto> corpBranchDtos;
	}

}
