package com.nomadconnection.dapp.core.domain.benefit;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.consent.Consent;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.ColumnDefault;

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
public class Benefit extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	@Column(nullable = false)
	private String vendor;

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

	@Column(nullable = false)
	private String basicInfoDesc;

	@Column(nullable = false)
	private String basicInfoDetail;

	@Column(nullable = false)
	private String basicInfoGuide;

	private String basicInfoExtraInfoLabel;

	private String basicInfoExtraInfoLink;

	@ColumnDefault("신청하기")
	private String basicInfoButtonLabel;

	private String basicInfoButtonLink;

	private String authInfoDesc;

	private String authInfoDetail;

	private String authInfoGuide;

	private String authInfoExtraInfoLabel;

	private String authInfoExtraInfoLink;

	private String authInfoButtonLabel;

	private String authInfoButtonLink;

	private String email;

	private String tel;

	@Column(columnDefinition = "TINYINT", nullable = false)
	private Integer activeApplying;

	@Column(columnDefinition = "TINYINT", nullable = false)
	private Integer activeAbTest;

	@Column(columnDefinition = "TINYINT", nullable = false)
	private Integer activePayment;

	@OneToMany(mappedBy = "benefit")
	private List<BenefitPaymentHistory> benefitPaymentHistories;

	@OneToMany(mappedBy = "benefit")
	private List<BenefitItem> benefitItems;

	@ManyToOne(targetEntity = BenefitCategory.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "idxBenefitCategory", foreignKey = @ForeignKey(name = "FK_BenefitCategory_Benefit"))
	private BenefitCategory benefitCategory;

	@Builder.Default
	private boolean disabled = false;
}
