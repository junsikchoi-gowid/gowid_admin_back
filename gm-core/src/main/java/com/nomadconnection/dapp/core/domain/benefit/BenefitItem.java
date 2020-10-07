package com.nomadconnection.dapp.core.domain.benefit;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BenefitItem extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	@ManyToOne(targetEntity = Benefit.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "idxBenefit", foreignKey = @ForeignKey(name = "FK_Benefit_BenefitItem"))
	private Benefit benefit;

	@OneToMany(mappedBy = "benefitItem")
	private List<BenefitPaymentItem> benefitPaymentItems;

	private String name;

	private Long account;

	private Long discount;

	private Integer minQuantity;

}
