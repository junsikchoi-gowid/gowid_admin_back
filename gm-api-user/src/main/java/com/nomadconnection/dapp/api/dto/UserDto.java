package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.MemberAuthority;
import com.nomadconnection.dapp.core.domain.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@SuppressWarnings({"unused"})
public class UserDto {

	@ApiModelProperty("선택약관동의여부")
	private boolean consent;

	@ApiModelProperty("이메일(계정)")
	private String email;

	@ApiModelProperty("이름")
	private String name;

	@ApiModelProperty("연락처(폰)")
	private String mdn;

	@Builder
	public UserDto(boolean consent, String email, String name, String mdn) {
		this.consent = consent;
		this.email = email;
		this.name = name;
		this.mdn = mdn;
	}

	public static UserDto from(User user) {
		return UserDto.builder()
				.consent(user.consent())
				.email(user.email())
				.name(user.name())
				.mdn(user.mdn())
				.build();
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserRegister {

		@ApiModelProperty("선택약관동의여부")
		private boolean consent;

		@ApiModelProperty("이메일(계정)")
		private String email;

		@ApiModelProperty("비밀번호")
		private String password;

		@ApiModelProperty("이름")
		private String name;

		@ApiModelProperty("연락처(폰)")
		private String mdn;

		@ApiModelProperty("인증코드(4 digits, 초대된 멤버인 경우에만)")
		private String verificationCode;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MemberRegister {

		@ApiModelProperty("이름")
		private String name;

		@ApiModelProperty("이메일(계정)")
		private String email;

		@ApiModelProperty("멤버권한")
		private MemberAuthority authority;

		@ApiModelProperty("식별자(부서)")
		private Long idxDept;

		@ApiModelProperty("월한도(단위: 원)")
		private Long creditLimit;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RegisterBrandUser {
		@ApiModelProperty("이메일(계정)")
		private String email;

		@ApiModelProperty("비밀번호")
		private String password;

		@ApiModelProperty("이름")
		private String userName;

		@ApiModelProperty("연락처(폰)")
		private String mdn;

		@ApiModelProperty("sms 수신여부")
		private Boolean smsReception;

		@ApiModelProperty("email 수신여부")
		private Boolean emailReception;

		@ApiModelProperty("이용약관 정보")
		private List<ConsentDto.RegDto> consents;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RegisterBrandCorp {
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
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class registerUserUpdate {

		@ApiModelProperty("결제계좌정보")
		private String userName;

		@ApiModelProperty("전화번호")
		private String mdn;

		@ApiModelProperty("email")
		private String email;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class registerUserPasswordUpdate {
		@ApiModelProperty("email")
		private String oldPassword;

		@ApiModelProperty("email")
		private String newPassword;
	}
}
