package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.Card;
import com.nomadconnection.dapp.core.domain.CardStatementReception;
import com.nomadconnection.dapp.core.domain.CardStatus;
import com.nomadconnection.dapp.core.dto.AddressDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings({"unused"})
public class CardDto {

	@ApiModelProperty("식별자(카드)")
	private Long idx;

	@ApiModelProperty("카드번호(12 or 13 digits)")
	private String cardNo;

	@ApiModelProperty("카드인증코드(3 digits, card verification code)")
	private String cvc;

	@ApiModelProperty("카드유효기간(MM/YY, card valid thru)")
	private String cvt;

	@ApiModelProperty("카드상태")
	private CardStatus status;

	@ApiModelProperty("국내결제 가능여부")
	private boolean domestic;

	@ApiModelProperty("해외결제 가능여부")
	private boolean overseas;

	@ApiModelProperty("카드한도")
	private Long creditLimit;

	@ApiModelProperty("법인카드한도")
	private Long corpCreditLimit;

	@ApiModelProperty("등록일시")
	private LocalDateTime createdAt;

	@ApiModelProperty("마지막 수정일시")
	private LocalDateTime updatedAt;

	public static CardDto from(Card card) {
		return CardDto.builder()
				.idx(card.idx())
				.cardNo(card.cardNo())
				.cvc(card.cvc())
				.cvt(card.cvt())
				.status(card.status())
				.domestic(card.domestic())
				.overseas(card.overseas())
				.creditLimit(card.creditLimit())
				.corpCreditLimit(card.corp().creditLimit())
				.createdAt(card.getCreatedAt())
				.updatedAt(card.getUpdatedAt())
				.build();
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CardBasicInfo {

		@ApiModelProperty("식별자(카드)")
		private Long idx;

		@ApiModelProperty("카드번호")
		private String cardNo;

		@ApiModelProperty("사용여부")
		private boolean disabled;

		@ApiModelProperty("카드상태")
		private CardStatus status;

		@ApiModelProperty("국내결제 가능여부")
		private boolean domestic;

		@ApiModelProperty("해외결제 가능여부")
		private boolean overseas;

		@ApiModelProperty("카드한도")
		private Long creditLimit;

		@ApiModelProperty("등록일시")
		private LocalDateTime createdAt;

		@ApiModelProperty("마지막 수정일시")
		private LocalDateTime updatedAt;

		public static CardBasicInfo from(Card card) {
			return CardBasicInfo.builder()
					.idx(card.idx())
					.cardNo(card.cardNo())
					.disabled(card.disabled())
					.status(card.status())
					.domestic(card.domestic())
					.overseas(card.overseas())
					.creditLimit(card.creditLimit())
					.createdAt(card.getCreatedAt())
					.updatedAt(card.getUpdatedAt())
					.build();
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CardIssuance {

		@ApiModelProperty("식별자(발급신청)")
		private Long idx;

		@ApiModelProperty("직원수")
		private Integer staffs;

		@ApiModelProperty("희망카드개수")
		private Integer reqCards;

		@ApiModelProperty("명세서수령방법")
		private CardStatementReception reception;

		@ApiModelProperty("수령인")
		private String recipient;

		@ApiModelProperty("수령인연락처")
		private String recipientNo;

		@ApiModelProperty("수령지")
		private AddressDto recipientAddress;

		@ApiModelProperty("부재시대리수령여부")
		private boolean substituteRecipient;

		public static CardIssuance from(com.nomadconnection.dapp.core.domain.CardIssuance issuance) {
			return CardIssuance.builder()
					.idx(issuance.idx())
					.staffs(issuance.staffs())
					.reqCards(issuance.reqCards())
					.reception(issuance.reception())
					.recipient(issuance.recipient())
					.recipientNo(issuance.recipientNo())
					.recipientAddress(AddressDto.builder()
							.zip(issuance.recipientAddress().getAddressZipCode())
							.basic(issuance.recipientAddress().getAddressBasic())
							.detail(issuance.recipientAddress().getAddressDetails())
							.build())
					.substituteRecipient(issuance.substituteRecipient())
					.build();
		}

		@Data
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
		public static class CardIssuanceRequest {

			@ApiModelProperty("직원수")
			private Integer staffs;

			@ApiModelProperty("희망카드개수")
			private Integer reqCards;

			@ApiModelProperty("명세서수령방법")
			private CardStatementReception reception;

			@ApiModelProperty("수령인")
			private String recipient;

			@ApiModelProperty("수령인연락처")
			private String recipientNo;

			@ApiModelProperty("수령지")
			private AddressDto recipientAddress;

			@ApiModelProperty("부재시대리수령여부")
			private boolean substituteRecipient;

			public CardIssuanceRequest(com.nomadconnection.dapp.core.domain.CardIssuance issuance) {
				this.staffs = issuance.staffs();
				this.reqCards = issuance.reqCards();
				this.reception = issuance.reception();
				this.recipient = issuance.recipient();
				this.recipientNo = issuance.recipientNo();
				this.recipientAddress = AddressDto.builder()
						.zip(issuance.recipientAddress().getAddressZipCode())
						.basic(issuance.recipientAddress().getAddressBasic())
						.detail(issuance.recipientAddress().getAddressDetails())
						.build();
				this.substituteRecipient = issuance.substituteRecipient();
			}
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CardReissue {

		@ApiModelProperty("수령인")
		private String recipient;

		@ApiModelProperty("수령인연락처")
		private String recipientNo;

		@ApiModelProperty("수령지")
		private AddressDto recipientAddress;

		@ApiModelProperty("부재시대리수령여부")
		private boolean substituteRecipient;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CardRegister {

		@ApiModelProperty("카드번호(12 or 13 digits")
		private String cardNo; // 12(or 13) digits

		@ApiModelProperty("카드인증코드(3 digits)")
		private String cvc; // card verification code (3 digits)

		@ApiModelProperty("카드비밀번호(4 digits)")
		private String password; // 4 digits

		@ApiModelProperty("카드유효기간(MM/YY)")
		private String cvt; // card valid thru, MM/YY
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CardPatch {

		@ApiModelProperty("국내결제 가능여부(값이 있는 경우 패치)")
		private Boolean domestic;

		@ApiModelProperty("해외결제 가능여부(값이 있는 경우 패치)")
		private Boolean overseas;

		@ApiModelProperty("월한도(값이 있는 경우 패치)")
		private Long creditLimit;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CardPasswordReset {

		@ApiModelProperty("카드인증코드(cvc: card verification code)")
		private String cvc;

		@ApiModelProperty("카드유효기간(cvt: card valid thru, MM/YY)")
		private String cvt;

		@ApiModelProperty("패스워드")
		private String password;

		@Override
		public String toString() {
			return String.format("CardDto.CardPatch(cvc=%s, password=%s)", cvc, Strings.isEmpty(password) ? "(null)" : "********");
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CardStatusPatch {

		@ApiModelProperty("카드상태")
		private CardStatus status;

		@ApiModelProperty("카드인증코드(분실신고해제시필요, card verification code)")
		private String cvc;

		@ApiModelProperty("카드유효기간(분실신고해제시필요, card valid thru, mm/yy)")
		private String cvt;

		@ApiModelProperty("카드비밀번호(분실신고해제시필요)")
		private String password;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CardAuthentication {

		@ApiModelProperty("카드 비밀번호(4 digits)")
		private String password;

		public String toString() {
			return "CardAuthentication(password=****)";
		}
	}

//	@Data
//	@Builder
//	@NoArgsConstructor
//	@AllArgsConstructor
//	public static class CardMember {
//
//		private String name;
//		private String email;
//		private MemberAuthority authority;
//		private DeptDto dept;
//		private Long monthlyLimit; // 월한도
//	}

}
