package com.nomadconnection.dapp.core.domain.benefit;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Benefit extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	@Column(nullable = false)
	private String name;

	@Lob
	@Column(nullable = false)
	private String catchphrase;

	@Column(nullable = false)
	private Integer priority;

	@Column(nullable = false)
	private String hoverMessage;

	@Column(nullable = false)
	private String imageUrl;

	private String detailImageUrl;

	private String detailMobileImageUrl;

	@Column(nullable = false)
	private String basicInfoDesc;

	@Lob
	@Column(nullable = false)
	private String basicInfoDetail;

	@Lob
	@Column(nullable = false)
	private String basicInfoGuide;

	private String basicInfoExtraInfoLabel;

	private String basicInfoExtraInfoLink;

	private String authInfoDesc;

	@Lob
	private String authInfoDetail;

	@Lob
	private String authInfoGuide;

	private String authInfoExtraInfoLabel;

	private String authInfoExtraInfoLink;

	@Column(columnDefinition = "TINYINT", nullable = false)
	private Integer activeApplying;

	@Column(columnDefinition = "TINYINT", nullable = false)
	private Integer activePayment;

	@Column(columnDefinition = "TINYINT", nullable = false)
	private Integer applyLink;

	@OneToMany(mappedBy = "benefit")
	private List<BenefitPaymentHistory> benefitPaymentHistories;

	@OneToMany(mappedBy = "benefit")
	private List<BenefitItem> benefitItems;

	@OneToMany(mappedBy = "benefit")
	private List<BenefitProvider> benefitProviders;

	@ManyToOne(targetEntity = BenefitCategory.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "idxBenefitCategory", foreignKey = @ForeignKey(name = "FK_BenefitCategory_Benefit"))
	private BenefitCategory benefitCategory;

	@Builder.Default
	private boolean disabled = false;
}
