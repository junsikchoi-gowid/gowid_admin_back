package com.nomadconnection.dapp.core.domain.kised;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(uniqueConstraints ={
		@UniqueConstraint(columnNames = {"licenseNo", "projectId"}, name = "UK_LicenseNo_ProjectId"),
		@UniqueConstraint(columnNames = {"idxConfirmationFile"}, name = "UK_ConfirmationFile")
})
public class Kised extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	@OneToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="idxConfirmationFile", foreignKey = @ForeignKey(name = "FK_Kised_ConfirmationFile"), referencedColumnName = "idx")
	private ConfirmationFile confirmationFile;

	@OneToOne(mappedBy = "kised")
	private CardIssuanceInfo cardIssuanceInfo;

	@Column(columnDefinition = "varchar(10) COMMENT '과제번호'", nullable = false, updatable = false)
	private String projectId;

	@Builder.Default
	@Column(columnDefinition = "varchar(200) COMMENT '과제명'", nullable = false)
	private String projectName = "";

	@Builder.Default
	@Column(columnDefinition = "char(8) COMMENT '총 시작일'", nullable = false)
	private String startDate = "";

	@Builder.Default
	@Column(columnDefinition = "char(8) COMMENT '총 종료일'", nullable = false)
	private String endDate = "";

	@Builder.Default
	@Column(columnDefinition = "varchar(12) COMMENT '사업자번호'", nullable = false)
	private String licenseNo = "";

	@Column(columnDefinition = "varchar(50) COMMENT '기관이름'")
	private String orgName;

	@Builder.Default
	@Column(columnDefinition = "bigint(20) COMMENT '총예산현금'", nullable = false)
	private Long cash = 0L;

	@Column(columnDefinition = "bigint(20) COMMENT '총예산현물'")
	private Long spot;

	@Builder.Default
	@Column(columnDefinition = "varchar(3) COMMENT '은행코드'", nullable = false)
	private String bankCode = "";

	@Builder.Default
	@Column(columnDefinition = "varchar(50) COMMENT '계좌번호'", nullable = false)
	private String accountNo = "";

	@Builder.Default
	@Column(columnDefinition = "varchar(50) COMMENT '예금주명'", nullable = false)
	private String accountHolder = "";

	public void updateConfirmationFile(ConfirmationFile confirmationFile){
		this.confirmationFile = confirmationFile;
	}

	public void save(CardIssuanceInfo cardIssuanceInfo){
		cardIssuanceInfo.kised(this);
		this.cardIssuanceInfo = cardIssuanceInfo;
	}

}
