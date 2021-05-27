package com.nomadconnection.dapp.core.domain.benefit;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BenefitPaymentHistory extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;		// Id

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "idxUser", foreignKey = @ForeignKey(name = "FK_User_BenefitPaymentHistory"))
	private User user;	// User

	@ManyToOne(targetEntity = Benefit.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "idxBenefit", foreignKey = @ForeignKey(name = "FK_Benefit_BenefitPaymentHistory"))
	private Benefit benefit;		// Benefit

	@Column(nullable = false)
	private Long standardPrice;		// 표준 가격

	@Column(nullable = false)
	private Long totalPrice;		// 최종 결제 금액

	@Column(nullable = false)
	private String customerName;	// 담당자 이름

	@Column(nullable = false)
	private String customerMdn;		// 담당자 연락처

	@Column(nullable = false)
	private String customerDeptName;	// 담당자 부서 이름

	@Column(nullable = false)
	private String customerEmail;		// 담당자 이메일

	@Column(nullable = false)
	private String companyName;		// 회사명

	@Column(nullable = false)
	private String companyAddr;		// 회사 주소

	@Column(nullable = false)
	private String cardNum;		// 카드 번호

	@Column(nullable = false)
	private String status;		// 상태

	@Column(columnDefinition = "TINYINT", nullable = false)
	private Integer errCode;		// 결제 결과

	private String errMessage;	// 결제 에러 메세지

	@Column(columnDefinition = "TINYINT", nullable = false)
	private Integer sendPaymentMailErrCode;		// 결제 메일 전송 결과

	private String sendPaymentMailErrMessage;	// 결제 메일 전송 에러 메세지

	@Column(columnDefinition = "TINYINT", nullable = false)
	private Integer sendOrderMailErrCode;		// 발주서 메일 전송 결과

	private String sendOrderMailErrMessage;		// 발주서 메일 전송 에러 메세지

	@Column(nullable = false)
	private String paidAt;		// 결제 승인 시간

	private String receiptUrl;		// 매출전표 URL

	private String impUid;		// 거래 고유 번호

	@OneToMany(mappedBy = "benefitPaymentHistory", cascade = CascadeType.ALL)
	private List<BenefitPaymentItem> benefitPaymentItems;

}
