package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.common.IssuanceProgressType;
import com.nomadconnection.dapp.core.domain.common.IssuanceStatusType;
import com.nomadconnection.dapp.core.domain.user.Events;
import com.nomadconnection.dapp.core.domain.user.Role;
import com.nomadconnection.dapp.core.domain.user.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class UserDto {

	@ApiModelProperty("선택약관동의여부")
	private boolean consent;

	@ApiModelProperty("이메일(계정)")
	private String email;

	@ApiModelProperty("이름")
	private String name;

	@ApiModelProperty("연락처(폰)")
	private String mdn;

	@ApiModelProperty("초기화여부")
	private boolean isReset;

	@Builder
	public UserDto(boolean consent, String email, String name, String mdn, boolean isReset) {
		this.consent = consent;
		this.email = email;
		this.name = name;
		this.mdn = mdn;
		this.isReset = isReset;
	}

	public static UserDto from(User user) {
		return UserDto.builder()
				.consent(user.consent())
				.email(user.email())
				.name(user.name())
				.mdn(user.mdn())
				.isReset(user.isReset())
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

		@Override
		public String toString() {
			return String.format("%s(email=%s, password=********)", getClass().getSimpleName(), email);
		}
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

		@ApiModelProperty("비밀번호")
		private String password;

		@ApiModelProperty("멤버역할")
		private Role role;

		@ApiModelProperty("임시비밀번호여부")
		private boolean hasTmpPassword;

		@Override
		public String toString() {
			return String.format("%s(email=%s, password=********)", getClass().getSimpleName(), email);
		}
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

		@ApiModelProperty("회사명")
		private String corpName;

		@ApiModelProperty("직책")
		private String position;

		@ApiModelProperty("연락처(폰)")
		private String mdn;

		@ApiModelProperty("sms 수신여부")
		private Boolean smsReception;

		@ApiModelProperty("email 수신여부")
		private Boolean emailReception;

		@ApiModelProperty("이용약관 정보")
		private List<ConsentDto.RegDto> consents;

		@ApiModelProperty("이벤트 명")
		private String eventName;

		@Override
		public String toString() {
			return String.format("%s(email=%s, password=********)", getClass().getSimpleName(), email);
		}
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

		@ApiModelProperty("이름")
		private String userName;

		@ApiModelProperty("전화번호")
		private String mdn;

		@ApiModelProperty("email")
		private String email;

		@ApiModelProperty("sms수신여부")
		private Boolean isSendSms;

		@ApiModelProperty("email수신여부")
		private Boolean isSendEmail;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class registerUserPasswordUpdate {
		@ApiModelProperty("이전 패스워드")
		private String oldPassword;

		@ApiModelProperty("새로운 패스워드")
		private String newPassword;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RegisterUserConsent {
		@ApiModelProperty("이용약관 정보")
		private List<ConsentDto.RegDto> consents;

		@ApiModelProperty("카드종류")
		private CardType cardType;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class IssuanceProgressRes {

		@ApiModelProperty("진행단계. NOT_SIGNED: 전자서명전, SIGNED: 서명완료, P_1200: 신규/기존 여부 체크, P_15XX: 스크래핑 전문 전송, P_AUTO_CHECK: 자동심사(1000/1400), P_IMG: 이미지전송 , P_1600: 수동심사결과(1600), P_1100: 카드신청정보 전송  , P_1800: 전자서명값 전송")
		private IssuanceProgressType progress;

		@ApiModelProperty("상태. DEFAULT:초기상태(실행전), SUCCESS:성공, FAILED:실패")
		private IssuanceStatusType status;

		@ApiModelProperty("카드사명")
		private String cardCompany;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LimitReview {
		@ApiModelProperty("카드한도")
		private Long cardLimit;

		@ApiModelProperty("계좌정보")
		private String accountInfo;

		@ApiModelProperty("기타사항")
		private String etc;

		@ApiModelProperty("휴대폰안내")
		private Boolean enablePhone;

		@ApiModelProperty("이메일안내")
		private Boolean enableEmail;

		@ApiModelProperty("법인명")
		private String companyName;

		@ApiModelProperty("희망한도")
		private String hopeLimit;
	}

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ExternalIdRes {

		@ApiModelProperty("외부아이디")
		private String externalId;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DeleteUserAccount {
		@ApiModelProperty("패스워드")
		private String password;

		@ApiModelProperty("탈퇴사유")
		private String reason;

		@Override
		public String toString() {
			return String.format("%s(reason=%s, password=********)", getClass().getSimpleName(), reason);
	}

}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class EventsInfo {
		@ApiModelProperty("idx")
		private Long idx;

		@ApiModelProperty("idxUser")
		private Long idxUser;

		@ApiModelProperty("createUser")
		private Long createUser;

		@ApiModelProperty("이벤트명")
		private String eventName;

		@ApiModelProperty("시작일자")
		private LocalDateTime startDate;

		@ApiModelProperty("종료일자")
		private LocalDateTime endDate;

		public static EventsInfo from(Events events) {
			return EventsInfo.builder()
					.idx(events.idx())
					.idxUser(events.idxUser())
					.eventName(events.eventName())
					.createUser(events.createUser())
					.endDate(events.endDate())
					.build();
		}
	}

}
