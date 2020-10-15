package com.nomadconnection.dapp.core.domain.benefit;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Getter
@Setter
@Accessors(fluent = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BenefitPaymentItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	@ManyToOne(targetEntity = BenefitPaymentHistory.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "idxBenefitPaymentHistory", foreignKey = @ForeignKey(name = "FK_BenefitPaymentHistory_BenefitPaymentItem"))
	private BenefitPaymentHistory benefitPaymentHistory;

	@ManyToOne(targetEntity = BenefitItem.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "idxBenefitItem", foreignKey = @ForeignKey(name = "FK_BenefitItem_BenefitPaymentItem"))
	private BenefitItem benefitItem;

	private Integer quantity;

	private Long price;

	public BenefitItem getBenefitItem() {
		return this.benefitItem;
	}

	public Integer getQuantity() {
		return this.quantity;
	}

	public Long getPrice() {
		return this.price;
	}
}
