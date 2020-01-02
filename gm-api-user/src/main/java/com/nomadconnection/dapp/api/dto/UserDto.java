package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.MemberAuthority;
import com.nomadconnection.dapp.core.domain.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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
	public static class RegisterUserCorp {

		@ApiModelProperty("선택약관동의여부")
		private boolean idxConsent;

		@ApiModelProperty("이메일(계정)")
		private String email;

		@ApiModelProperty("비밀번호")
		private String password;

		@ApiModelProperty("이름")
		private String userName;

		@ApiModelProperty("연락처(폰)")
		private String mdn;

		@ApiModelProperty("인증코드(4 digits, 초대된 멤버인 경우에만)")
		private String verificationCode;

		@ApiModelProperty("법인명")
		private String corpName;

		@ApiModelProperty("사업자등록번호")
		private String bizRegNo;

		@ApiModelProperty("주주명부")
		private MultipartFile resxShareholderList;

		@ApiModelProperty("희망법인총한도")
		private Long reqCreditLimit;

		@ApiModelProperty("결제계좌정보")
		private BankAccountDto bankAccount;

		@ApiModelProperty("이용약관 정보")
		private List<ConsentDto.RegDto> consents;
	}
}
