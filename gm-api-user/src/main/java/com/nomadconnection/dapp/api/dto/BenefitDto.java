package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.benefit.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

public class BenefitDto {

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BenefitReq {

		@ApiModelProperty("제목")
		@NotEmpty
		private String title;

		@ApiModelProperty("상세내용")
		@NotNull
		private String content;

		@ApiModelProperty("개요")
		@NotEmpty
		private String summary;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BenefitPaymentHistoryReq {

		@ApiModelProperty("Benefit ID")
		@NotEmpty
		private Long idxBenefit;

		@ApiModelProperty("표준 금액")
		@NotEmpty
		private Long standardPrice;

		@ApiModelProperty("최종 결제 금액")
		@NotEmpty
		private Long totalPrice;

		@ApiModelProperty("담당자 이름")
		@NotEmpty
		private String customerName;

		@ApiModelProperty("담당자 연락처")
		@NotEmpty
		private String customerMdn;

		@ApiModelProperty("담당자 부서명")
		@NotEmpty
		private String customerDeptName;

		@ApiModelProperty("담당자 이메일")
		@NotEmpty
		private String customerEmail;

		@ApiModelProperty("회사 이름")
		@NotEmpty
		private String companyName;

		@ApiModelProperty("회사 주소")
		@NotEmpty
		private String companyAddr;

		@ApiModelProperty("구매 상품 정보")
		@NotEmpty
		private List<BenefitPaymentItemReq> items;

		@ApiModelProperty("카드 번호")
		@NotEmpty
		private String cardNum;

		@ApiModelProperty("결제 승인 시간")
		private String paidAt;

		@ApiModelProperty("매출전표 URL")
		private String receiptUrl;

		@ApiModelProperty("거래 고유 번호")
		@NotEmpty
		private String impUid;

		@ApiModelProperty("결제 결과")
		@NotEmpty
		private Boolean errCode;

		@ApiModelProperty("결제 오류 메세지")
		private String errMessage;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BenefitPaymentItemReq {

		@ApiModelProperty("베네핏 Item ID")
		@NotEmpty
		private long idxBenefitItem;

		@ApiModelProperty("구매 수량")
		@NotEmpty
		private int quantity;

		@ApiModelProperty("구매 금액")
		@NotEmpty
		private long price;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BenefitRes {

		@ApiModelProperty("식별자")
		private Long idx;

		@ApiModelProperty("Vendor")
		private String vendor;

		@ApiModelProperty("Benefit 이름")
		private String name;

		@ApiModelProperty("Catchphrase")
		private String catchphrase;

		@ApiModelProperty("우선순")
		private Integer priority;

		@ApiModelProperty("hover Message")
		private String hoverMessage;

		@ApiModelProperty("Imager URL")
		private String imageUrl;

		@ApiModelProperty("(기본정보) 설명")
		private String basicInfoDesc;

		@ApiModelProperty("(기본정보) 상세")
		private String basicInfoDetail;

		@ApiModelProperty("(기본정보) 이용 방법")
		private String basicInfoGuide;

		@ApiModelProperty("(기본정보) 상세보기 Button Label")
		private String basicInfoExtraInfoLabel;

		@ApiModelProperty("(기본정보) 상세보기 Button Link URL")
		private String basicInfoExtraInfoLink;

		@ApiModelProperty("(기본정보) Action에 대한 Button Label")
		private String basicInfoButtonLabel;

		@ApiModelProperty("(기본정보) Action에 대한 Button Link URL")
		private String basicInfoButtonLink;

		@ApiModelProperty("(로그인 후) 설명")
		private String authInfoDesc;

		@ApiModelProperty("(로그인 후) 상세")
		private String authInfoDetail;

		@ApiModelProperty("(로그인 후) 이용 방법")
		private String authInfoGuide;

		@ApiModelProperty("(로그인 후) 상세보기 Button Label")
		private String authInfoExtraInfoLabel;

		@ApiModelProperty("(로그인 후) 상세보기 Button Link URL")
		private String authInfoExtraInfoLink;

		@ApiModelProperty("(로그인 후) Action에 대한 Button Label")
		private String authInfoButtonLabel;

		@ApiModelProperty("(로그인 후) Action에 대한 Button Link URL")
		private String authInfoButtonLink;

		@ApiModelProperty("문의 Email")
		private String email;

		@ApiModelProperty("문의 전화번호")
		private String tel;

		@ApiModelProperty("서비스 오픈 여부")
		private Integer activeApplying;

		@ApiModelProperty("AB 테스트 여부")
		private Integer activeAbTest;

		@ApiModelProperty("결제 필요 여부")
		private Integer activePayment;

		@ApiModelProperty("Modal 여부")
		private Integer applyLink;

		@ApiModelProperty("Benefit 세부 항목(제품군)")
		private List<BenefitDto.BenefitItemRes> benefitItems;

		@ApiModelProperty("Benefit 카테고리")
		private BenefitDto.BenefitCategoryRes benefitCategory;

		public static BenefitRes from(Benefit benefit) {
			if (benefit != null) {
				return BenefitRes.builder()
						.idx(benefit.idx())
						.vendor(benefit.vendor())
						.name(benefit.name())
						.catchphrase(benefit.catchphrase())
						.priority(benefit.priority())
						.hoverMessage(benefit.hoverMessage())
						.imageUrl(benefit.imageUrl())
						.basicInfoDesc(benefit.basicInfoDesc())
						.basicInfoDetail(benefit.basicInfoDetail())
						.basicInfoGuide(benefit.basicInfoGuide())
						.basicInfoExtraInfoLabel(benefit.basicInfoExtraInfoLabel())
						.basicInfoExtraInfoLink(benefit.basicInfoExtraInfoLink())
						.basicInfoButtonLabel(benefit.basicInfoButtonLabel())
						.basicInfoButtonLink(benefit.basicInfoButtonLink())
						.authInfoDesc(benefit.authInfoDesc())
						.authInfoDetail(benefit.authInfoDetail())
						.authInfoGuide(benefit.authInfoGuide())
						.authInfoExtraInfoLabel(benefit.authInfoExtraInfoLabel())
						.authInfoExtraInfoLink(benefit.authInfoExtraInfoLink())
						.authInfoButtonLabel(benefit.authInfoButtonLabel())
						.authInfoButtonLink(benefit.authInfoButtonLink())
						.email(benefit.email())
						.tel(benefit.tel())
						.activeApplying(benefit.activeApplying())
						.activeAbTest(benefit.activeAbTest())
						.activePayment(benefit.activePayment())
						.applyLink(benefit.applyLink())
						.benefitItems(benefit.benefitItems().stream().map(BenefitItemRes::from).collect(Collectors.toList()))
						.benefitCategory(BenefitDto.BenefitCategoryRes.from(benefit.benefitCategory()))
						.build();
			}
			return null;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BenefitPaymentHistoryRes {

		@ApiModelProperty("BenefitPaymentHistory ID")
		private Long idx;

		@ApiModelProperty("담당자 이름")
		private String customerName;

		@ApiModelProperty("담당자 연락처")
		private String customerMdn;

		@ApiModelProperty("담당자 이메일")
		private String customerEmail;

		@ApiModelProperty("담당자 부서명")
		private String customerDeptName;

		@ApiModelProperty("법인명")
		private String companyName;

		@ApiModelProperty("법인주소")
		private String companyAddr;

		@ApiModelProperty("카드번호")
		private String cardNum;

		@ApiModelProperty("표준 금액")
		private Long standardPrice;

		@ApiModelProperty("최종 결제 금액")
		private Long totalPrice;

		@ApiModelProperty("결제 승인 시간")
		private String paidAt;

		@ApiModelProperty("상태")
		private String status;

		@ApiModelProperty("에러 메세지")
		private String errMessage;

		@ApiModelProperty("benefit")
		private BenefitDto.BenefitRes benefit;

		@ApiModelProperty("benefit items")
		private List<BenefitDto.BenefitPaymentItemRes> benefitPaymentItems;

		public static BenefitPaymentHistoryRes from(BenefitPaymentHistory benefitPaymentHistory) {
			if (benefitPaymentHistory != null) {
				return BenefitPaymentHistoryRes.builder()
						.idx(benefitPaymentHistory.idx())
						.customerName(benefitPaymentHistory.customerName())
						.customerMdn(benefitPaymentHistory.customerMdn())
						.customerEmail(benefitPaymentHistory.customerEmail())
						.customerDeptName(benefitPaymentHistory.customerDeptName())
						.companyName(benefitPaymentHistory.companyName())
						.companyAddr(benefitPaymentHistory.companyAddr())
						.cardNum(benefitPaymentHistory.cardNum())
						.standardPrice(benefitPaymentHistory.standardPrice())
						.totalPrice(benefitPaymentHistory.totalPrice())
						.paidAt(benefitPaymentHistory.paidAt())
						.status(benefitPaymentHistory.status())
						.errMessage(benefitPaymentHistory.errMessage())
						.benefit(BenefitDto.BenefitRes.from(benefitPaymentHistory.benefit()))
						.benefitPaymentItems(benefitPaymentHistory.benefitPaymentItems().stream().map(BenefitDto.BenefitPaymentItemRes::from).collect(Collectors.toList()))
						.build();
			}
			return null;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BenefitPaymentItemRes {

		@ApiModelProperty("식별자")
		private Long idx;

		@ApiModelProperty("Benefit")
		private BenefitDto.BenefitItemRes benefitItem;

		@ApiModelProperty("수량")
		private Integer quantity;

		@ApiModelProperty("금액")
		private Long price;

		public static BenefitPaymentItemRes from(BenefitPaymentItem benefitPaymentItem) {
			if (benefitPaymentItem != null) {
				return BenefitPaymentItemRes.builder()
						.idx(benefitPaymentItem.idx())
						.benefitItem(BenefitDto.BenefitItemRes.from(benefitPaymentItem.benefitItem()))
						.quantity(benefitPaymentItem.quantity())
						.price(benefitPaymentItem.price())
						.build();
			}
			return null;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BenefitItemRes {

		@ApiModelProperty("식별자")
		private Long idx;

		@ApiModelProperty("항목 이름")
		private String name;

		@ApiModelProperty("원가")
		private Long account;

		@ApiModelProperty("할인금액")
		private Long discount;

		@ApiModelProperty("최소구매수량")
		private Integer minQuantity;

		public static BenefitItemRes from(BenefitItem benefitItem) {
			if (benefitItem != null) {
				return BenefitItemRes.builder()
						.idx(benefitItem.idx())
						.name(benefitItem.name())
						.account(benefitItem.account())
						.discount(benefitItem.discount())
						.minQuantity(benefitItem.minQuantity())
						.build();
			}
			return null;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BenefitCategoryRes {

		@ApiModelProperty("식별자")
		private Long idx;

		@ApiModelProperty("카테고리 그룹 코드")
		private String categoryGroupCode;

		@ApiModelProperty("카테고리 코드")
		private String categoryCode;

		@ApiModelProperty("카테고리 이름")
		private String categoryName;

		@ApiModelProperty("노출 우선순위")
		private Integer priority;

		public static BenefitCategoryRes from(BenefitCategory benefitCategory) {
			if (benefitCategory != null) {
				return BenefitCategoryRes.builder()
						.idx(benefitCategory.idx())
						.categoryGroupCode(benefitCategory.categoryGroupCode())
						.categoryCode(benefitCategory.categoryCode())
						.categoryName(benefitCategory.categoryName())
						.priority(benefitCategory.priority())
						.build();
			}
			return null;
		}
	}
}
