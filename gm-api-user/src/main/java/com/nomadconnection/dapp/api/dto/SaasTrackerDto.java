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

public class SaasTrackerDto {

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaasTrackerReportsReq {

		@ApiModelProperty("제보타입")
		@NotNull(message = "제보타입은 반드시 입력되어야 합니다.")
		private Integer reportType;

		@ApiModelProperty("SaaS 이름")
		private String saasName;

		@ApiModelProperty("결제 수단")
		private Integer paymentMethod;

		@ApiModelProperty("최근 결제 금액")
		private Long paymentPrice;

		@ApiModelProperty("제보 내용")
		private String issue;

		@ApiModelProperty("무료 사용 만료일")
		private String experationDate;

		@ApiModelProperty("무료 사용 만료 알림")
		private Boolean activeExperationAlert;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UpdateSaasInfoReq {

		@ApiModelProperty("담당자 이름")
		private String managerName;

		@ApiModelProperty("담당자 이메일")
		private String managerEmail;

		@ApiModelProperty("구독 여부")
		private Boolean activeSubscription;

		@ApiModelProperty("알림 여부")
		private Boolean activeAlert;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UsageCategoriesDetailsRes {

		@ApiModelProperty("기준 월")
		private String pdate;

		@ApiModelProperty("카테고리 별 SaaS 지출 목록")
		private List<SaasPaymentHistoryRepository.UsageSumsDetailsDto> listOfCategories;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UsageSumsRes {

		@ApiModelProperty("년월")
		private String pdate;

		@ApiModelProperty("총 결제 금액")
		private Long psum;

		@ApiModelProperty("결제 금액 증감률")
		private String mom;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UsageRes {

		@ApiModelProperty("SaaS Info Idx")
		private Long idxSaasInfo;

		@ApiModelProperty("SaaS 이름")
		private String saasName;

		@ApiModelProperty("SaaS 이미지 파일 이름")
		private String saasImageName;

		@ApiModelProperty("카테고리 이름")
		private String categoryName;

		@ApiModelProperty("결제 일시")
		private String paymentDate;

		@ApiModelProperty("결제 금액")
		private Long paymentPrice;

		@ApiModelProperty("결제 유형")
		private Integer paymentMethod;

		@ApiModelProperty("기관명")
		private String organization;

		@ApiModelProperty("카드번호")
		private String cardNumber;

		@ApiModelProperty("계좌번호")
		private String accountNumber;

		public static UsageRes from(SaasPaymentHistory history) {
			if(history != null) {
				return UsageRes.builder()
						.idxSaasInfo(history.saasInfo().idx())
						.saasName(history.saasInfo().name())
						.saasImageName(history.saasInfo().imageName())
						.categoryName(history.saasInfo().saasCategory().name())
						.paymentDate(history.paymentDate())
						.paymentPrice(history.paymentPrice())
						.paymentMethod(history.paymentMethod())
						.organization(SaasOrganizationType.getType(history.organization()).getOrgName())
						.cardNumber(StringUtils.isEmpty(history.cardNumber()) ? null : CommonUtil.extractTextFromLast(history.cardNumber(), 4))
						.accountNumber(StringUtils.isEmpty(history.accountNumber()) ? null : CommonUtil.extractTextFromLast(history.accountNumber(), 4))
						.build();
			}
			return null;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaasCategoriesRes {

		@ApiModelProperty("카테고리 ID")
		private Long idxSaasCategory;

		@ApiModelProperty("카테고리 이름")
		private String categoryName;

		@ApiModelProperty("사용중인 SaaS 목록")
		private List<SaasCategoryRepository.UseSaasByCategoryDto> listOfSaas;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UseSaasListRes {
		private List<UseSaasRes> subscriptionList;
		private List<UseSaasRes> unsubscriptionList;
	}


	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UseSaasRes {

		@ApiModelProperty("SaaS ID")
		private Long idxSaasInfo;

		@ApiModelProperty("최근 결제일")
		private String currentPaymentDate;

		@ApiModelProperty("최근 결제액")
		private Long currentPaymentPrice;

		@ApiModelProperty("결제수단")
		private Integer paymentMethod;

		@ApiModelProperty("결제유형")
		private Integer paymentType;

		@ApiModelProperty("결제유형 목록")
		private List<Integer> paymentTypeList;

		@ApiModelProperty("알림여부")
		private Boolean activeAlert;

		@ApiModelProperty("구독여부")
		private Boolean activeSubscription;

		@ApiModelProperty("담당자명")
		private String managerName;

		@ApiModelProperty("담당자이메일")
		private String managerEmail;

		@ApiModelProperty("SaaS 이름")
		private String saasName;

		@ApiModelProperty("SaaS 이미지 파일 이름")
		private String saasImageName;

		@ApiModelProperty("카테고리 이름")
		private String categoryName;

		public static UseSaasRes from(SaasPaymentInfo saasPaymentInfo) {
			if(saasPaymentInfo != null) {
				boolean hasSaasPaymentMangeInfo = !ObjectUtils.isEmpty(saasPaymentInfo.saasPaymentManageInfo());
				return UseSaasRes.builder()
						.idxSaasInfo(saasPaymentInfo.saasInfo().idx())
						.currentPaymentDate(saasPaymentInfo.currentPaymentDate())
						.currentPaymentPrice(saasPaymentInfo.currentPaymentPrice())
						.paymentMethod(saasPaymentInfo.paymentMethod())
						.activeAlert(hasSaasPaymentMangeInfo ? saasPaymentInfo.saasPaymentManageInfo().activeAlert() : null)
						.activeSubscription(saasPaymentInfo.activeSubscription())
						.managerName(hasSaasPaymentMangeInfo ? saasPaymentInfo.saasPaymentManageInfo().managerName() : null)
						.managerEmail(hasSaasPaymentMangeInfo ? saasPaymentInfo.saasPaymentManageInfo().managerEmail() : null)
						.saasName(saasPaymentInfo.saasInfo().name())
						.saasImageName(saasPaymentInfo.saasInfo().imageName())
						.categoryName(saasPaymentInfo.saasInfo().saasCategory().name())
						.build();
			}
			return null;
		}

		public static UseSaasRes from(SaasPaymentInfoRepository.SubscriptSaasDto subscriptSaasDto) {
			if(subscriptSaasDto != null) {
				return UseSaasRes.builder()
						.idxSaasInfo(subscriptSaasDto.getIdxSaasInfo())
						.currentPaymentDate(subscriptSaasDto.getCurrentPaymentDate())
						.currentPaymentPrice(subscriptSaasDto.getCurrentPaymentPrice())
						.paymentMethod(subscriptSaasDto.getPaymentMethod())
						.activeAlert(subscriptSaasDto.getActiveAlert())
						.activeSubscription(subscriptSaasDto.getActiveSubscription())
						.managerName(subscriptSaasDto.getManagerName())
						.managerEmail(subscriptSaasDto.getManagerEmail())
						.saasName(subscriptSaasDto.getSaasName())
						.saasImageName(subscriptSaasDto.getSaasImageName())
						.categoryName(subscriptSaasDto.getCategoryName())
						.build();
			}
			return null;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaasPaymentScheduleRes {

		@ApiModelProperty("정기 결제 목록")
		private List<SaasPaymentScheduleDetailRes> regularList;

		@ApiModelProperty("비정기 결제 목록")
		private List<SaasPaymentScheduleDetailRes> irregularList;

		@ApiModelProperty("미분류 목록")
		private List<SaasPaymentScheduleDetailRes> unclassifiedList;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaasPaymentScheduleDetailRes {

		@ApiModelProperty("SaaS Info ID")
		private Long idxSaasInfo;

		@ApiModelProperty("SaaS Payment Info ID")
		private Long idxSaasPaymentInfo;

		@ApiModelProperty("SaaS 이름")
		private String saasName;

		@ApiModelProperty("SaaS 이미지 파일 이름")
		private String saasImageName;

		@ApiModelProperty("결제수단")
		private Integer paymentMethod;

		@ApiModelProperty("결제기관")
		private String organization;

		@ApiModelProperty("카드번호")
		private String cardNumber;

		@ApiModelProperty("계좌번호")
		private String accountNumber;

		@ApiModelProperty("담당자명")
		private String managerName;

		@ApiModelProperty("담당자이메일")
		private String managerEmail;

		@ApiModelProperty("결제유형")
		private Integer paymentType;

		@ApiModelProperty("최근 결제일")
		private String currentPaymentDate;

		@ApiModelProperty("최근 결제액")
		private Long currentPaymentPrice;

		@ApiModelProperty("최근 결제액")
		private String paymentScheduleDate;

		@ApiModelProperty("구독여부")
		private Boolean activeSubscription;

		@ApiModelProperty("알림여부")
		private Boolean activeAlert;

		public static SaasPaymentScheduleDetailRes from(SaasPaymentInfo saasPaymentInfo) {
			if(saasPaymentInfo != null) {
				boolean hasSaasPaymentMangeInfo = !ObjectUtils.isEmpty(saasPaymentInfo.saasPaymentManageInfo());
				return SaasPaymentScheduleDetailRes.builder()
						.idxSaasInfo(saasPaymentInfo.saasInfo().idx())
						.idxSaasPaymentInfo(saasPaymentInfo.idx())
						.saasName(saasPaymentInfo.saasInfo().name())
						.saasImageName(saasPaymentInfo.saasInfo().imageName())
						.paymentMethod(saasPaymentInfo.paymentMethod())
						.organization(SaasOrganizationType.getType(saasPaymentInfo.organization()).getOrgName())
						.cardNumber(StringUtils.isEmpty(saasPaymentInfo.cardNumber()) ? null : CommonUtil.extractTextFromLast(saasPaymentInfo.cardNumber(), 4))
						.accountNumber(StringUtils.isEmpty(saasPaymentInfo.accountNumber()) ? null : CommonUtil.extractTextFromLast(saasPaymentInfo.accountNumber(), 4))
						.managerName(hasSaasPaymentMangeInfo ? saasPaymentInfo.saasPaymentManageInfo().managerName() : null)
						.managerEmail(hasSaasPaymentMangeInfo ? saasPaymentInfo.saasPaymentManageInfo().managerEmail() : null)
						.paymentType(saasPaymentInfo.paymentType())
						.currentPaymentDate(saasPaymentInfo.currentPaymentDate())
						.currentPaymentPrice(saasPaymentInfo.currentPaymentPrice())
						.paymentScheduleDate(saasPaymentInfo.paymentScheduleDate())
						.activeSubscription(saasPaymentInfo.activeSubscription())
						.activeAlert(hasSaasPaymentMangeInfo ? saasPaymentInfo.saasPaymentManageInfo().activeAlert() : null)
						.build();
			}
			return null;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaasPaymentScheduleAtCalendarListRes {

		@ApiModelProperty("SaaS ID")
		private Long idxSaasInfo;

		@ApiModelProperty("SaaS Payment Info ID")
		private Long idxSaasPaymentInfo;

		@ApiModelProperty("SaaS 이름")
		private String saasName;

		@ApiModelProperty("SaaS 이미지 파일 이름")
		private String saasImageName;

		@ApiModelProperty("결제수단")
		private Integer paymentMethod;

		@ApiModelProperty("결제기관")
		private String organization;

		@ApiModelProperty("카드번호")
		private String cardNumber;

		@ApiModelProperty("계좌번호")
		private String accountNumber;

		@ApiModelProperty("결제유형")
		private Integer paymentType;

		@ApiModelProperty("결제일")
		private String paymentDate;

		@ApiModelProperty("결제예상금액")
		private Long paymentPrice;

		@ApiModelProperty("결제예정일")
		private String paymentScheduleDate;

		public static SaasPaymentScheduleAtCalendarListRes from(SaasPaymentInfo saasPaymentInfo) {
			if(saasPaymentInfo != null) {
				return SaasPaymentScheduleAtCalendarListRes.builder()
						.idxSaasInfo(saasPaymentInfo.saasInfo().idx())
						.idxSaasPaymentInfo(saasPaymentInfo.idx())
						.saasName(saasPaymentInfo.saasInfo().name())
						.saasImageName(saasPaymentInfo.saasInfo().imageName())
						.paymentMethod(saasPaymentInfo.paymentMethod())
						.organization(SaasOrganizationType.getType(saasPaymentInfo.organization()).getOrgName())
						.cardNumber(StringUtils.isEmpty(saasPaymentInfo.cardNumber()) ? null : CommonUtil.extractTextFromLast(saasPaymentInfo.cardNumber(), 4))
						.accountNumber(StringUtils.isEmpty(saasPaymentInfo.accountNumber()) ? null : CommonUtil.extractTextFromLast(saasPaymentInfo.accountNumber(), 4))
						.paymentType(saasPaymentInfo.paymentType())
						.paymentDate(saasPaymentInfo.currentPaymentDate().startsWith(CommonUtil.getNowYYYYMM()) ? saasPaymentInfo.currentPaymentDate()
								: (!StringUtils.isEmpty(saasPaymentInfo.paymentScheduleDate()) && Integer.parseInt(saasPaymentInfo.paymentScheduleDate()) >= Integer.parseInt(CommonUtil.getNowYYYYMMDD()) ? saasPaymentInfo.paymentScheduleDate() : null))
						.paymentPrice(saasPaymentInfo.currentPaymentPrice())
						.paymentScheduleDate(saasPaymentInfo.paymentScheduleDate())
						.build();
			}
			return null;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaasPaymentScheduleAtCalendarRes {

		@ApiModelProperty("결제 목록(캘린더)")
		private List<SaasPaymentScheduleAtCalendarListRes> calendarList;

		@ApiModelProperty("다가올 결제 목록")
		private List<SaasPaymentScheduleAtCalendarListRes> scheduleList;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaasPaymentDetailInfoRes {

		@ApiModelProperty("SaaS ID")
		private Long idxSaasInfo;

		@ApiModelProperty("SaaS 이름")
		private String saasName;

		@ApiModelProperty("관리자 이름")
		private String managerName;

		@ApiModelProperty("관리자 이메일")
		private String managerEmail;

		@ApiModelProperty("알림 여부")
		private Boolean activeAlert;

		@ApiModelProperty("구독 여부")
		private Boolean activeSubscription;

		@ApiModelProperty("결제 수단 목록")
		private List<SaasPaymentInfoRes> saasPaymentInfos;

		@ApiModelProperty("결제 이력")
		private List<SaasPaymentHistoryRes> saasPaymentHistories;

		@ApiModelProperty("결제 이력에 따른 차트 데이터")
		private List<SaasPaymentHistoryRepository.UsageSumsDto> listOfSums;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaasPaymentInfoRes {

		@ApiModelProperty("SaaS Info Idx")
		private Long idxSaasInfo;

		@ApiModelProperty("SaaS 이름")
		private String saasName;

		@ApiModelProperty("최근 결제 날짜")
		private String currentPaymentDate;

		@ApiModelProperty("결제 유형")
		private Integer paymentMethod;

		@ApiModelProperty("기관명")
		private String organization;

		@ApiModelProperty("계좌번호")
		private String accountNumber;

		@ApiModelProperty("카드번호")
		private String cardNumber;

		@ApiModelProperty("결제 종류")
		private Integer paymentType;

		@ApiModelProperty("구독 여부")
		private Boolean activeSubscription;

		public static SaasPaymentInfoRes from(SaasPaymentInfo saasPaymentInfo) {
			if(saasPaymentInfo != null) {
				return SaasPaymentInfoRes.builder()
						.idxSaasInfo(saasPaymentInfo.saasInfo().idx())
						.saasName(saasPaymentInfo.saasInfo().name())
						.currentPaymentDate(saasPaymentInfo.currentPaymentDate())
						.paymentMethod(saasPaymentInfo.paymentMethod())
						.organization(SaasOrganizationType.getType(saasPaymentInfo.organization()).getOrgName())
						.cardNumber(StringUtils.isEmpty(saasPaymentInfo.cardNumber()) ? null : CommonUtil.extractTextFromLast(saasPaymentInfo.cardNumber(), 4))
						.accountNumber(StringUtils.isEmpty(saasPaymentInfo.accountNumber()) ? null : CommonUtil.extractTextFromLast(saasPaymentInfo.accountNumber(), 4))
						.paymentType(saasPaymentInfo.paymentType())
						.activeSubscription(saasPaymentInfo.activeSubscription())
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

		@ApiModelProperty("Idx")
		private Long idx;

		@ApiModelProperty("결제 날짜")
		private String paymentDate;

		@ApiModelProperty("결제 유형")
		private Integer paymentMethod;

		@ApiModelProperty("기관명")
		private String organization;

		@ApiModelProperty("결제금액")
		private Long paymentPrice;

		@ApiModelProperty("계좌번호")
		private String accountNumber;

		@ApiModelProperty("카드번호")
		private String cardNumber;

		public static SaasPaymentHistoryRes from(SaasPaymentHistory saasPaymentHistory) {
			if(saasPaymentHistory != null) {
				return SaasPaymentHistoryRes.builder()
						.idx(saasPaymentHistory.idx())
						.paymentDate(saasPaymentHistory.paymentDate())
						.paymentMethod(saasPaymentHistory.paymentMethod())
						.organization(SaasOrganizationType.getType(saasPaymentHistory.organization()).getOrgName())
						.paymentPrice(saasPaymentHistory.paymentPrice())
						.cardNumber(StringUtils.isEmpty(saasPaymentHistory.cardNumber()) ? null : CommonUtil.extractTextFromLast(saasPaymentHistory.cardNumber(), 4))
						.accountNumber(StringUtils.isEmpty(saasPaymentHistory.accountNumber()) ? null : CommonUtil.extractTextFromLast(saasPaymentHistory.accountNumber(), 4))
						.build();
			}
			return null;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaaSInsightsRes {

		@ApiModelProperty("새로운 사용 SaaS 목록")
		private List<SaasPaymentInfoRes> newSaasList;

		@ApiModelProperty("지난달 최고 지출 목록")
		private List<SaasMaxTop5Res> bestPaymentTop5List;

		@ApiModelProperty("중복결제 의심 목록")
		private List<SaasDuplicatePaymentRes> duplicatePaymentList;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaasPaymentInfoInsightRes {

		@ApiModelProperty("SaaS 이름")
		private String saasName;

		@ApiModelProperty("최근결제일")
		private String currentPaymentDate;

		@ApiModelProperty("결제 유형")
		private Integer paymentMethod;

		@ApiModelProperty("기관명")
		private String organization;

		@ApiModelProperty("카드번호")
		private String cardNumber;

		@ApiModelProperty("계좌번호")
		private String accountNumber;

		public static SaasPaymentInfoInsightRes from(SaasPaymentInfo saasPaymentInfo) {
			if(saasPaymentInfo != null) {
				return SaasPaymentInfoInsightRes.builder()
						.saasName(saasPaymentInfo.saasInfo().name())
						.currentPaymentDate(saasPaymentInfo.currentPaymentDate())
						.paymentMethod(saasPaymentInfo.paymentMethod())
						.organization(SaasOrganizationType.getType(saasPaymentInfo.organization()).getOrgName())
						.cardNumber(StringUtils.isEmpty(saasPaymentInfo.cardNumber()) ? null : CommonUtil.extractTextFromLast(saasPaymentInfo.cardNumber(), 4))
						.accountNumber(StringUtils.isEmpty(saasPaymentInfo.accountNumber()) ? null : CommonUtil.extractTextFromLast(saasPaymentInfo.accountNumber(), 4))
						.build();
			}
			return null;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaasMaxTop5Res {

		@ApiModelProperty("Saas Info Idx")
		private Long idxSaasInfo;

		@ApiModelProperty("SaaS 이름")
		private String saasName;

		@ApiModelProperty("결제금액")
		private Long payment;

		@ApiModelProperty("증감율")
		private Double mom;

		public static SaasMaxTop5Res from(SaasPaymentHistoryRepository.BestPaymentTop5Dto dto) {
			if(dto != null) {
				return SaasMaxTop5Res.builder()
						.idxSaasInfo(dto.getIdxSaasInfo())
						.saasName(dto.getName())
						.payment(dto.getbSum())
						.mom(ObjectUtils.isEmpty(dto.getMom()) ? 0 : dto.getMom())
						.build();
			}
			return null;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaasDuplicatePaymentRes {

		@ApiModelProperty("SaaS Info Idx")
		private Long idxSaasInfo;

		@ApiModelProperty("SaaS 이름")
		private String saasName;

		@ApiModelProperty("중복건수")
		private Integer count;

		public static SaasDuplicatePaymentRes from(SaasPaymentHistoryRepository.DuplicatePaymentDto dto) {
			if(dto != null) {
				return SaasDuplicatePaymentRes.builder()
						.idxSaasInfo(dto.getIdxSaasInfo())
						.saasName(dto.getName())
						.count(dto.getCount())
						.build();
			}
			return null;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaaSTrackerProgressRes {

		@ApiModelProperty("준비 상태")
		private Integer status;

		@ApiModelProperty("진행 단계")
		private Integer step;

		@ApiModelProperty("데이터 갱신 날짜")
		private String processDate;

		public static SaaSTrackerProgressRes from(SaasTrackerProgress progress) {
			if(progress != null) {
				return SaaSTrackerProgressRes.builder()
						.status(progress.status())
						.step(progress.step())
						.processDate(progress.processDate())
						.build();
			}
			return null;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaasNewTop5Res {

		@ApiModelProperty("SaaS Info Idx")
		private Long idxSaasInfo;

		@ApiModelProperty("SaaS 이름")
		private String saasName;

		@ApiModelProperty("최근 결제 날짜")
		private String currentPaymentDate;

		@ApiModelProperty("결제 유형")
		private Integer paymentMethod;

		@ApiModelProperty("기관명")
		private String organization;

		@ApiModelProperty("계좌번호")
		private String accountNumber;

		@ApiModelProperty("카드번호")
		private String cardNumber;

		@ApiModelProperty("결제 종류")
		private Integer paymentType;

		public static SaasPaymentInfoRes from(SaasPaymentHistory saasPaymentHistory) {
			if(saasPaymentHistory != null) {
				return SaasPaymentInfoRes.builder()
						.idxSaasInfo(saasPaymentHistory.saasInfo().idx())
						.saasName(saasPaymentHistory.saasInfo().name())
						.currentPaymentDate(saasPaymentHistory.paymentDate())
						.paymentMethod(saasPaymentHistory.paymentMethod())
						.organization(SaasOrganizationType.getType(saasPaymentHistory.organization()).getOrgName())
						.cardNumber(StringUtils.isEmpty(saasPaymentHistory.cardNumber()) ? null : CommonUtil.extractTextFromLast(saasPaymentHistory.cardNumber(), 4))
						.accountNumber(StringUtils.isEmpty(saasPaymentHistory.accountNumber()) ? null : CommonUtil.extractTextFromLast(saasPaymentHistory.accountNumber(), 4))
						.paymentType(ObjectUtils.isEmpty(saasPaymentHistory.accountNumber()) ? 1 : 2)
						.build();
			}
			return null;
		}
	}
}