package com.nomadconnection.dapp.api.v2.dto.saas;

import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.saas.SaasCheckInfo;
import com.nomadconnection.dapp.core.domain.saas.SaasOrganizationType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class SaasTrackerCheckListDto {

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CheckInfoReq {

		@ApiModelProperty("체크리스트 확인 여부")
		@NotNull
		private Boolean checked;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CheckCountRes {
		private int needCancelCnt;			// 해지필요 건수
		private int newSaasCnt;				// 신규등록 건수
		private int reRegistrationCnt;		// 재등록 건수
		private int strangePaymentCnt;		// 이상결제 건수
		private int freeChangeCnt;			// 무료인데 결제된 의심 건수
		private int increasedPaymentCnt;	// 결제 급등 건수
		private int freeExpirationCnt;		// 무료만료알림 건수
		private int duplicatePaymentCnt;	// 중복결제의심 건수
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CheckDataListRes {
		List<CheckData> needCancelList;			// 해지필요 목록
		List<CheckData> newSaasList;			// 신규등록 목록
		List<CheckData> reRegistrationList;		// 재등록 목록
		List<CheckData> strangePaymentList;		// 이상결제 목록
		List<CheckData> freeChangeList;			// 무료인데 결된 의심 목록
		List<CheckData> increasedPaymentList;	// 결제 급등 목록
		List<CheckData> freeExpirationList;		// 무료만료알림 목록
		List<CheckData> duplicatePaymentList;	// 중복결제의심 목록
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CheckData {
		private Long idxSaasCheckInfo;		// 체크리스트 idx
		private Boolean checked;			// 체크 여부
		private String mom;					// 증가율
		private Long amountPrice;			// 누적 결제액
		private Long amountIncreasePrice;	// 누적 결제 증가액
		private Integer increaseMonth;		// 금액 급증 달
		private Long idxSaasCheckCategory;	// SaaS 체크리스트 항목 idx
		private String checkCategoryName;	// SaaS 체크리스트 항목 이름
		private Long idxSaasInfo;			// SaaS idx
		private String saasName;			// SaaS 이름
		private String saasImageName;		// SaaS 이미지 이름
		private Long idxSaasPaymentInfo;	// SaaS Payment Info
		private Integer paymentType;		// 결제 유형
		private Integer paymentMethod;		// 결제 수단
		private String organization;		// 결제 기관명
		private String organizationCode;	// 결제 기관 코드
		private String cardNumber;			// 카드번호
		private String accountNumber;		// 계좌번호
		private String currentPaymentDate;	// 최근 결제일
		private Long currentPaymentPrice;	// 최근 결제 금액
		private String paymentScheduleDate;	// 예상 결제일
		private String expirationDate;		// 만료일자
		private String managerName;			// 담당자 이름
		private String managerEmail;		// 담당자 이메일
		private LocalDateTime createdAt;	// 생성일
		private LocalDateTime updatedAt;	// 수정일

		public static CheckData from(SaasCheckInfo saasCheckInfo) {
			if(saasCheckInfo != null) {
				return CheckData.builder()
					.idxSaasCheckInfo(saasCheckInfo.idx())
					.checked(saasCheckInfo.checked())
					.mom(saasCheckInfo.mom())
					.amountPrice(saasCheckInfo.amountPrice())
					.amountIncreasePrice(saasCheckInfo.amountIncreasePrice())
					.increaseMonth(saasCheckInfo.increaseMonth())
					.idxSaasCheckCategory(saasCheckInfo.saasCheckCategory().idx())
					.checkCategoryName(saasCheckInfo.saasCheckCategory().name().name())
					.idxSaasInfo(saasCheckInfo.saasPaymentInfo().saasInfo().idx())
					.saasName(saasCheckInfo.saasPaymentInfo().saasInfo().name())
					.saasImageName(saasCheckInfo.saasPaymentInfo().saasInfo().imageName())
					.idxSaasPaymentInfo(saasCheckInfo.saasPaymentInfo().idx())
					.paymentType(saasCheckInfo.saasPaymentInfo().paymentType())
					.paymentMethod(saasCheckInfo.saasPaymentInfo().paymentMethod())
					.organization(SaasOrganizationType.getType(saasCheckInfo.saasPaymentInfo().organization()).getOrgName())
					.organizationCode(saasCheckInfo.saasPaymentInfo().organization())
					.cardNumber(StringUtils.isEmpty(saasCheckInfo.saasPaymentInfo().cardNumber()) ? null : CommonUtil.extractTextFromLast(saasCheckInfo.saasPaymentInfo().cardNumber(), 4))
					.accountNumber(saasCheckInfo.saasPaymentInfo().accountNumber())
					.currentPaymentDate(saasCheckInfo.saasPaymentInfo().currentPaymentDate())
					.currentPaymentPrice(saasCheckInfo.saasPaymentInfo().currentPaymentPrice())
					.paymentScheduleDate(saasCheckInfo.saasPaymentInfo().paymentScheduleDate())
					.expirationDate(saasCheckInfo.saasPaymentInfo().expirationDate())
					.managerName(ObjectUtils.isEmpty(saasCheckInfo.saasPaymentInfo().saasPaymentManageInfo()) ? null : saasCheckInfo.saasPaymentInfo().saasPaymentManageInfo().managerName())
					.managerEmail(ObjectUtils.isEmpty(saasCheckInfo.saasPaymentInfo().saasPaymentManageInfo()) ? null : saasCheckInfo.saasPaymentInfo().saasPaymentManageInfo().managerEmail())
					.createdAt(saasCheckInfo.getCreatedAt())
					.updatedAt(saasCheckInfo.getUpdatedAt())
					.build();
			}
			return null;
		}
	}
}