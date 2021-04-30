package com.nomadconnection.dapp.core.domain.limit;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Accessors(fluent = true)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"idxCorp"}, name = "UK_Corp"))
@Entity
public class LimitRecalculation extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long idx;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idxCorp", foreignKey = @ForeignKey(name = "FK_Corp_LimitRecalculation"), columnDefinition = "bigint(20) COMMENT '법인 idx'", nullable = false)
	private Corp corp;

	@Enumerated(value = EnumType.STRING)
	@Column(columnDefinition = "varchar(20) DEFAULT 'REQUESTED' COMMENT '심사상태'", nullable = false)
	private ReviewStatus reviewStatus;

	@Builder.Default
	@OneToMany(mappedBy = "limitRecalculation")
	private List<LimitRecalculationHistory> limitRecalculationHistories = new ArrayList<>();

	public static LimitRecalculation of(Corp corp){
		return LimitRecalculation.builder()
			.corp(corp)
			.reviewStatus(ReviewStatus.REQUESTED)
			.build();
	}

	public void update(ReviewStatus reviewStatus){
		this.reviewStatus = reviewStatus;
	}

}
