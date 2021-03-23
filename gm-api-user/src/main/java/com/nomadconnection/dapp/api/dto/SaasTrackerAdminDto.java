package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.repository.saas.SaasCategoryRepository;
import com.nomadconnection.dapp.core.domain.repository.saas.SaasPaymentHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.saas.SaasPaymentInfoRepository;
import com.nomadconnection.dapp.core.domain.saas.SaasOrganizationType;
import com.nomadconnection.dapp.core.domain.saas.SaasPaymentHistory;
import com.nomadconnection.dapp.core.domain.saas.SaasPaymentInfo;
import com.nomadconnection.dapp.core.domain.saas.SaasTrackerProgress;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;

public class SaasTrackerAdminDto {

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaasTrackerUserRes {

		@ApiModelProperty("idxUser")
		private Long idxUser;

		@ApiModelProperty("회사명")
		private String companyName;

		@ApiModelProperty("사용자명")
		private String userName;

		@ApiModelProperty("이메일")
		private String email;

		@ApiModelProperty("최종 업데이트 일자")
		private String processDate;

		@ApiModelProperty("상태")
		private Integer status;

		@ApiModelProperty("진행단계")
		private Integer step;

		public static SaasTrackerUserRes from(SaasTrackerProgress saasTrackerProgress) {
			if(saasTrackerProgress != null) {
				return SaasTrackerUserRes.builder()
						.idxUser(saasTrackerProgress.user().idx())
						.companyName(ObjectUtils.isEmpty(saasTrackerProgress.user().corp()) ? null : saasTrackerProgress.user().corp().resCompanyNm())
						.userName(saasTrackerProgress.user().name())
						.email(saasTrackerProgress.user().email())
						.processDate(saasTrackerProgress.processDate())
						.status(saasTrackerProgress.status())
						.step(saasTrackerProgress.step())
						.build();
			}
			return null;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaasPaymentHistoryRes {

		@ApiModelProperty("idx")
		private Long idx;

		@ApiModelProperty("SaaS 이름")
		private String saasName;

		@ApiModelProperty("SaaS 이미지 이름")
		private String saasImageName;

		@ApiModelProperty("기관명")
		private String organization;

		@ApiModelProperty("결제 일자")
		private String paymentDate;

		@ApiModelProperty("결제 금액")
		private Long paymentPrice;

		@ApiModelProperty("결제 수단")
		private Integer paymentMethod;

		@ApiModelProperty("계좌번호")
		private String accountNumber;

		@ApiModelProperty("카드번호")
		private String cardNumber;

		@ApiModelProperty("해외 구분")
		private Integer foreignType;

		@ApiModelProperty("결제 단위")
		private String currency;

		@ApiModelProperty("항목")
		private String item;

		public static SaasPaymentHistoryRes from(SaasPaymentHistory saasPaymentHistory) {
			if(saasPaymentHistory != null) {
				return SaasPaymentHistoryRes.builder()
						.idx(saasPaymentHistory.idx())
						.saasName(saasPaymentHistory.saasInfo().name())
						.saasImageName(saasPaymentHistory.saasInfo().imageName())
						.organization(SaasOrganizationType.getType(saasPaymentHistory.organization()).getOrgName())
						.paymentDate(saasPaymentHistory.paymentDate())
						.paymentPrice(saasPaymentHistory.paymentPrice())
						.paymentMethod(saasPaymentHistory.paymentMethod())
						.accountNumber(saasPaymentHistory.accountNumber())
						.cardNumber(saasPaymentHistory.cardNumber())
						.foreignType(saasPaymentHistory.foreignType())
						.currency(saasPaymentHistory.currency())
						.item(saasPaymentHistory.item())
						.build();
			}
			return null;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaasPaymentInfoRes {

		@ApiModelProperty("idx")
		private Long idx;

		@ApiModelProperty("idxSaasInfo")
		private Long idxSaasInfo;

		@ApiModelProperty("SaaS 이름")
		private String saasName;

		@ApiModelProperty("SaaS 이미지 이름")
		private String saasImageName;

		@ApiModelProperty("기관명")
		private String organization;

		@ApiModelProperty("기관코드")
		private String organizationCode;

		@ApiModelProperty("최근 결제 일자")
		private String currentPaymentDate;

		@ApiModelProperty("최근 결제 금액")
		private Long currentPaymentPrice;

		@ApiModelProperty("결제 유형")
		private Integer paymentType;

		@ApiModelProperty("결제 수단")
		private Integer paymentMethod;

		@ApiModelProperty("계좌번호")
		private String accountNumber;

		@ApiModelProperty("카드번호")
		private String cardNumber;

		@ApiModelProperty("예상 결제 일자")
		private String paymentScheduleDate;

		@ApiModelProperty("구독 여부")
		private Boolean activeSubscription;

		@ApiModelProperty("중복 여부")
		private Boolean isDup;

		@ApiModelProperty("사용 여부")
		private Boolean disabled;

		public static SaasPaymentInfoRes from(SaasPaymentInfo saasPaymentInfo) {
			if(saasPaymentInfo != null) {
				return SaasPaymentInfoRes.builder()
						.idx(saasPaymentInfo.idx())
						.idxSaasInfo(saasPaymentInfo.saasInfo().idx())
						.saasName(saasPaymentInfo.saasInfo().name())
						.saasImageName(saasPaymentInfo.saasInfo().imageName())
						.organization(SaasOrganizationType.getType(saasPaymentInfo.organization()).getOrgName())
						.organizationCode(saasPaymentInfo.organization())
						.currentPaymentDate(saasPaymentInfo.currentPaymentDate())
						.currentPaymentPrice(saasPaymentInfo.currentPaymentPrice())
						.paymentType(saasPaymentInfo.paymentType())
						.paymentMethod(saasPaymentInfo.paymentMethod())
						.accountNumber(saasPaymentInfo.accountNumber())
						.cardNumber(saasPaymentInfo.cardNumber())
						.paymentScheduleDate(saasPaymentInfo.paymentScheduleDate())
						.activeSubscription(saasPaymentInfo.activeSubscription())
						.isDup(saasPaymentInfo.isDup())
						.disabled(saasPaymentInfo.disabled())
						.build();
			}
			return null;
		}

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaasPaymentHistoryReq {

		@ApiModelProperty("idxUser")
		@NotNull(message = "사용자는 반드시 입력되어야 합니다.")
		private Long idxUser;

		@ApiModelProperty("idxSaasInfo")
		@NotNull(message = "SaaS 정보는 반드시 입력되어야 합니다.")
		private Long idxSaasInfo;

		@ApiModelProperty("기관코드")
		@NotNull(message = "기관코드는 반드시 입력되어야 합니다.")
		private String organization;

		@ApiModelProperty("결제 일자")
		@NotNull(message = "결제 일자는 반드시 입력되어야 합니다.")
		private String paymentDate;

		@ApiModelProperty("결제 금액")
		@NotNull(message = "결제 금액은 반드시 입력되어야 합니다.")
		private Long paymentPrice;

		@ApiModelProperty("결제 수단")
		@NotNull(message = "결제 수단은 반드시 입력되어야 합니다.")
		private Integer paymentMethod;

		@ApiModelProperty("계좌 번호")
		private String accountNumber;

		@ApiModelProperty("카드 번호")
		private String cardNumber;

		@ApiModelProperty("혜외 구분")
		@NotNull(message = "혜외 구분은 반드시 입력되어야 합니다.")
		private Integer foreignType;

		@ApiModelProperty("결제 단위")
		@NotNull(message = "결제 단위는 반드시 입력되어야 합니다.")
		private String currency;

		@ApiModelProperty("적요")
		private String item;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaasPaymentInfoReq {

		@ApiModelProperty("idxUser")
		@NotNull(message = "사용자는 반드시 입력되어야 합니다.")
		private Long idxUser;

		@ApiModelProperty("idxSaasInfo")
		private Long idxSaasInfo;

		@ApiModelProperty("기관코드")
		@NotNull(message = "기관코드는 반드시 입력되어야 합니다.")
		private String organization;

		@ApiModelProperty("최근 결제 일자")
		@NotNull(message = "결제 일자는 반드시 입력되어야 합니다.")
		private String currentPaymentDate;

		@ApiModelProperty("최근 결제 금액")
		@NotNull(message = "결제 금액은 반드시 입력되어야 합니다.")
		private Long currentPaymentPrice;

		@ApiModelProperty("결제 유형")
		@NotNull(message = "결제 유형은 반드시 입력되어야 합니다.")
		private Integer paymentType;

		@ApiModelProperty("결제 수단")
		@NotNull(message = "결제 수단은 반드시 입력되어야 합니다.")
		private Integer paymentMethod;

		@ApiModelProperty("계좌 번호")
		private String accountNumber;

		@ApiModelProperty("카드 번호")
		private String cardNumber;

		@ApiModelProperty("예상 결제 일자")
		private String paymentScheduleDate;

		@ApiModelProperty("구독 여부")
		@NotNull(message = "구독 여부는 반드시 입력되어야 합니다.")
		private Boolean activeSubscription;

		@ApiModelProperty("중복 여부")
		@NotNull(message = "중복 여부는 반드시 입력되어야 합니다.")
		private Boolean isDup;

		@ApiModelProperty("사용 여부")
		@NotNull(message = "사용 여부는 반드시 입력되어야 합니다.")
		private Boolean disabled;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UpdateSaasPaymentInfoReq {

		@ApiModelProperty("기관코드")
		@NotNull(message = "기관코드는 반드시 입력되어야 합니다.")
		private String organization;

		@ApiModelProperty("최근 결제 일자")
		@NotNull(message = "결제 일자는 반드시 입력되어야 합니다.")
		private String currentPaymentDate;

		@ApiModelProperty("최근 결제 금액")
		@NotNull(message = "결제 금액은 반드시 입력되어야 합니다.")
		private Long currentPaymentPrice;

		@ApiModelProperty("결제 유형")
		@NotNull(message = "결제 수단은 반드시 입력되어야 합니다.")
		private Integer paymentType;

		@ApiModelProperty("계좌 번호")
		private String accountNumber;

		@ApiModelProperty("카드 번호")
		private String cardNumber;

		@ApiModelProperty("예상 결제 일자")
		private String paymentScheduleDate;

		@ApiModelProperty("구독 여부")
		@NotNull(message = "구독 여부는 반드시 입력되어야 합니다.")
		private Boolean activeSubscription;

		@ApiModelProperty("중복 여부")
		@NotNull(message = "중복 여부는 반드시 입력되어야 합니다.")
		private Boolean isDup;
	}
}