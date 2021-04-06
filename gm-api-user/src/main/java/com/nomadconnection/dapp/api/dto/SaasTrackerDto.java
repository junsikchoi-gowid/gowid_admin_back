package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.repository.saas.SaasCategoryRepository;
import com.nomadconnection.dapp.core.domain.repository.saas.SaasPaymentHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.saas.SaasPaymentInfoRepository;
import com.nomadconnection.dapp.core.domain.saas.*;
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
	public static class SaasTrackerUsageReq {

		@ApiModelProperty("회사명")
		@NotNull
		private String companyName;

		@ApiModelProperty("담당자명")
		@NotNull
		private String managerName;

		@ApiModelProperty("이메일")
		@NotNull
		private String managerEmail;
	}

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
	public static class UpdateSaasPaymentInfoReq {

		@ApiModelProperty("결제 유형")
		private Integer paymentType;

		@ApiModelProperty("이용기한")
		private String expirationDate;

		@ApiModelProperty("메모")
		private String memo;

		@ApiModelProperty("결제 여부")
		private Boolean disabled;
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

		@ApiModelProperty("결제 금액 목록")
		private List<UsageSumsByPaymentRes> paymentList;

		@ApiModelProperty("결제 예정 목록")
		private List<UsageSumsByPaymentRes> forecastList;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UsageSumsByPaymentRes {

		@ApiModelProperty("년월")
		private String pdate;

		@ApiModelProperty("총 결제 금액")
		private Long psum;

		@ApiModelProperty("결제 금액 증감률")
		private String mom;

		public static UsageSumsByPaymentRes from(SaasPaymentHistoryRepository.UsageSumsDto usageSumsByPayment) {
			if(usageSumsByPayment != null) {
				return UsageSumsByPaymentRes.builder()
						.pdate(usageSumsByPayment.getPDate())
						.psum(usageSumsByPayment.getPSum())
						.build();
			}
			return null;
		}
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
						.accountNumber(history.accountNumber())
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
		private List<UseSaasInfoInCategoryRes> listOfSaas;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UseSaasInfoInCategoryRes {

		@ApiModelProperty("idxSaasInfo")
		private Long idxSaasInfo;

		@ApiModelProperty("SaaS 이름")
		private String name;

		@ApiModelProperty("SaaS 이미지 이름")
		private String imageName;

		@ApiModelProperty("결제유형 목록")
		private List<Integer> paymentTypeList;

		public static UseSaasInfoInCategoryRes from(SaasCategoryRepository.UseSaasByCategoryDto useSaasInfoInCategory) {
			if(useSaasInfoInCategory != null) {
				return UseSaasInfoInCategoryRes.builder()
						.idxSaasInfo(useSaasInfoInCategory.getIdxSaasInfo())
						.name(useSaasInfoInCategory.getName())
						.imageName(useSaasInfoInCategory.getImageName())
						.build();
			}
			return null;
		}
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

//		@ApiModelProperty("알림여부")
//		private Boolean activeAlert;

//		@ApiModelProperty("구독여부")
//		private Boolean activeSubscription;

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

		@ApiModelProperty("전월 결제액")
		private Long lastMonthPaymentPrice;

		@ApiModelProperty("전전월 대비 전월 증감율")
		private Double mom;

		@ApiModelProperty("신규 SaaS 여부")
		private Boolean isNew;

		public static UseSaasRes from(SaasPaymentInfoRepository.SubscriptSaasDto subscriptSaasDto) {
			if(subscriptSaasDto != null) {
				return UseSaasRes.builder()
						.idxSaasInfo(subscriptSaasDto.getIdxSaasInfo())
						.currentPaymentDate(subscriptSaasDto.getCurrentPaymentDate())
						.currentPaymentPrice(subscriptSaasDto.getCurrentPaymentPrice())
						.paymentMethod(subscriptSaasDto.getPaymentMethod())
						.managerName(subscriptSaasDto.getManagerName())
						.managerEmail(subscriptSaasDto.getManagerEmail())
						.saasName(subscriptSaasDto.getSaasName())
						.saasImageName(subscriptSaasDto.getSaasImageName())
						.categoryName(subscriptSaasDto.getCategoryName())
						.lastMonthPaymentPrice(subscriptSaasDto.getLastMonthPaymentPrice())
						.isNew(subscriptSaasDto.getIsNew())
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

		@ApiModelProperty("정기 구독 결제 목록")
		private List<SaasPaymentScheduleDetailRes> regularList;

		@ApiModelProperty("최근 결제 목록")
		private List<SaasCurrentPaymentAtCalendarListRes> currentPaymentList;

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

		@ApiModelProperty("이용기한")
		private String expirationDate;

		@ApiModelProperty("해지필요 여부")
		private Boolean isTerminateRequired;

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
						.accountNumber(saasPaymentInfo.accountNumber())
						.managerName(hasSaasPaymentMangeInfo ? saasPaymentInfo.saasPaymentManageInfo().managerName() : null)
						.managerEmail(hasSaasPaymentMangeInfo ? saasPaymentInfo.saasPaymentManageInfo().managerEmail() : null)
						.paymentType(saasPaymentInfo.paymentType())
						.currentPaymentDate(saasPaymentInfo.currentPaymentDate())
						.currentPaymentPrice(saasPaymentInfo.currentPaymentPrice())
						.paymentScheduleDate(saasPaymentInfo.paymentScheduleDate())
						.activeSubscription(saasPaymentInfo.activeSubscription())
						.expirationDate(saasPaymentInfo.expirationDate())
						.isTerminateRequired(!StringUtils.isEmpty(saasPaymentInfo.paymentScheduleDate()) && !StringUtils.isEmpty(saasPaymentInfo.expirationDate()) ?
							(Integer.parseInt(CommonUtil.cutString(saasPaymentInfo.paymentScheduleDate(), 6)) >= Integer.parseInt(saasPaymentInfo.expirationDate()))
						: false)
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

		@ApiModelProperty("이용기한")
		private String expirationDate;

		@ApiModelProperty("해지필요 여부")
		private Boolean isTerminateRequired;

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
						.accountNumber(saasPaymentInfo.accountNumber())
						.paymentType(saasPaymentInfo.paymentType())
						.paymentDate(saasPaymentInfo.currentPaymentDate().startsWith(CommonUtil.getNowYYYYMM()) ? saasPaymentInfo.currentPaymentDate()
								: (!StringUtils.isEmpty(saasPaymentInfo.paymentScheduleDate()) && Integer.parseInt(saasPaymentInfo.paymentScheduleDate()) >= Integer.parseInt(CommonUtil.getNowYYYYMMDD()) ? saasPaymentInfo.paymentScheduleDate() : null))
						.paymentPrice(saasPaymentInfo.currentPaymentPrice())
						.expirationDate(saasPaymentInfo.expirationDate())
						.isTerminateRequired(!StringUtils.isEmpty(saasPaymentInfo.paymentScheduleDate()) && !StringUtils.isEmpty(saasPaymentInfo.expirationDate()) ?
								(Integer.parseInt(CommonUtil.cutString(saasPaymentInfo.paymentScheduleDate(), 6)) >= Integer.parseInt(saasPaymentInfo.expirationDate()))
								: false)
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
	public static class SaasCurrentPaymentAtCalendarListRes {

		@ApiModelProperty("SaaS ID")
		private Long idxSaasInfo;

		@ApiModelProperty("SaaS Payment History ID")
		private Long idxSaasPaymentHistory;

		@ApiModelProperty("SaaS 이름")
		private String saasName;

		@ApiModelProperty("SaaS 이미지 파일 이름")
		private String saasImageName;

		@ApiModelProperty("결제수단")
		private Integer paymentMethod;

		@ApiModelProperty("결제유형")
		private Integer paymentType;

		@ApiModelProperty("결제기관")
		private String organization;

		@ApiModelProperty("결제기관코드")
		private String organizationCode;

		@ApiModelProperty("카드번호")
		private String cardNumber;

		@ApiModelProperty("계좌번호")
		private String accountNumber;

		@ApiModelProperty("최근 결제일")
		private String currentPaymentDate;

		@ApiModelProperty("최근 결제액")
		private Long currentPaymentPrice;

		@ApiModelProperty("해지필요 여부")
		private Boolean isTerminateRequired;

		@ApiModelProperty("담당자 이름")
		private String managerName;

		@ApiModelProperty("담당자 이메일")
		private String managerEmail;

		public static SaasCurrentPaymentAtCalendarListRes from(SaasPaymentHistory saasPaymentHistory) {
			if(saasPaymentHistory != null) {
				return SaasCurrentPaymentAtCalendarListRes.builder()
						.idxSaasInfo(saasPaymentHistory.saasInfo().idx())
						.idxSaasPaymentHistory(saasPaymentHistory.idx())
						.saasName(saasPaymentHistory.saasInfo().name())
						.saasImageName(saasPaymentHistory.saasInfo().imageName())
						.paymentMethod(saasPaymentHistory.paymentMethod())
						.organization(SaasOrganizationType.getType(saasPaymentHistory.organization()).getOrgName())
						.organizationCode(saasPaymentHistory.organization())
						.cardNumber(StringUtils.isEmpty(saasPaymentHistory.cardNumber()) ? null : CommonUtil.extractTextFromLast(saasPaymentHistory.cardNumber(), 4))
						.accountNumber(saasPaymentHistory.accountNumber())
						.currentPaymentDate(saasPaymentHistory.paymentDate())
						.currentPaymentPrice(saasPaymentHistory.paymentPrice())
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

		@ApiModelProperty("최근 결제 내역")
		private List<SaasCurrentPaymentAtCalendarListRes> currentPaymentList;

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

		@ApiModelProperty("카테고리 이름")
		private String categoryName;

		@ApiModelProperty("SaaS 이미지 파일 이름")
		private String saasImageName;

		@ApiModelProperty("홈페이지 URL")
		private String homepageUrl;

		@ApiModelProperty("가격표 URL")
		private String priceUrl;

		@ApiModelProperty("SaaS 설명")
		private String saasDesc;

		@ApiModelProperty("결제 수단 목록")
		private List<SaasPaymentInfoRes> saasPaymentInfos;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaasPaymentInfoRes {

		@ApiModelProperty("SaaSPaymentInfo Idx")
		private Long idxSaasPaymentInfo;

		@ApiModelProperty("SaaS Info Idx")
		private Long idxSaasInfo;

		@ApiModelProperty("SaaS 이름")
		private String saasName;

		@ApiModelProperty("최근 결제 날짜")
		private String currentPaymentDate;

		@ApiModelProperty("결제 유형")
		private Integer paymentMethod;

		@ApiModelProperty("기관코드")
		private String organizationCode;

		@ApiModelProperty("기관명")
		private String organization;

		@ApiModelProperty("계좌번호")
		private String accountNumber;

		@ApiModelProperty("카드번호")
		private String cardNumber;

		@ApiModelProperty("결제 종류")
		private Integer paymentType;

		@ApiModelProperty("이용기한")
		private String expirationDate;

		@ApiModelProperty("메모")
		private String memo;

		@ApiModelProperty("결제수단 사용 여부")
		private Boolean disabled;

		public static SaasPaymentInfoRes from(SaasPaymentInfo saasPaymentInfo) {
			if(saasPaymentInfo != null) {
				return SaasPaymentInfoRes.builder()
						.idxSaasPaymentInfo(saasPaymentInfo.idx())
						.idxSaasInfo(saasPaymentInfo.saasInfo().idx())
						.saasName(saasPaymentInfo.saasInfo().name())
						.currentPaymentDate(saasPaymentInfo.currentPaymentDate())
						.paymentMethod(saasPaymentInfo.paymentMethod())
						.organizationCode(saasPaymentInfo.organization())
						.organization(StringUtils.isEmpty(saasPaymentInfo.organization()) ? "" : SaasOrganizationType.getType(saasPaymentInfo.organization()).getOrgName())
						.cardNumber(StringUtils.isEmpty(saasPaymentInfo.cardNumber()) ? null : CommonUtil.extractTextFromLast(saasPaymentInfo.cardNumber(), 4))
						.accountNumber(saasPaymentInfo.accountNumber())
						.paymentType(saasPaymentInfo.paymentType())
						.expirationDate(saasPaymentInfo.expirationDate())
						.memo(saasPaymentInfo.memo())
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

		@ApiModelProperty("적요")
		private String item;

		public static SaasPaymentHistoryRes from(SaasPaymentHistory saasPaymentHistory) {
			if(saasPaymentHistory != null) {
				return SaasPaymentHistoryRes.builder()
						.idx(saasPaymentHistory.idx())
						.paymentDate(saasPaymentHistory.paymentDate())
						.paymentMethod(saasPaymentHistory.paymentMethod())
						.organization(SaasOrganizationType.getType(saasPaymentHistory.organization()).getOrgName())
						.paymentPrice(saasPaymentHistory.paymentPrice())
						.cardNumber(StringUtils.isEmpty(saasPaymentHistory.cardNumber()) ? null : CommonUtil.extractTextFromLast(saasPaymentHistory.cardNumber(), 4))
						.accountNumber(saasPaymentHistory.accountNumber())
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
	public static class SaaSInsightsRes {

		@ApiModelProperty("새로운 사용 SaaS 목록")
		private List<SaasPaymentInfoRes> newSaasList;

		@ApiModelProperty("지난달 최고 지출 목록")
		private List<SaasMaxTop5Res> bestPaymentTop5List;

		@ApiModelProperty("증가율 기준 Top 5 목록")
		private List<SaasMaxTop5Res> momSortList;
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
						.accountNumber(saasPaymentInfo.accountNumber())
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

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
    public static class SaasInfoRes {

		@ApiModelProperty("SaaS Info Idx")
		private Long idxSaasInfo;

//		@ApiModelProperty("Saas Categort Idx")
//		private Long idxSaasCategory;

		@ApiModelProperty("SaaS 이름")
		private String name;

		@ApiModelProperty("이미지 이름")
		private String imageName;

//		@ApiModelProperty("한글 이름")
//		private String korName;

		public static SaasInfoRes from(SaasInfo saasInfo) {
			if(saasInfo != null) {
				return SaasInfoRes.builder()
						.idxSaasInfo(saasInfo.idx())
//						.idxSaasCategory(saasInfo.saasCategory().idx())
						.name(saasInfo.name())
						.imageName(saasInfo.imageName())
//						.korName(saasInfo.korName())
						.build();
			}
			return null;
		}
    }
}