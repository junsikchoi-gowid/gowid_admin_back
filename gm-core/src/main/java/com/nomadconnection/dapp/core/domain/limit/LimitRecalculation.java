package com.nomadconnection.dapp.core.domain.limit;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationDto.LimitRecalculationDetail;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"idxCorp", "date"}, name = "UK_title_answer"))
@Entity
public class LimitRecalculation extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long idx;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "idxCorp", foreignKey = @ForeignKey(name = "FK_Corp_LimitRecalculation"), columnDefinition = "bigint(20) COMMENT '법인 idx'", nullable = false)
	private Corp corp;

	@Column(columnDefinition = "date COMMENT '요청일자'", nullable = false)
	private LocalDate date;

	@Column(columnDefinition = "bigint(20) COMMENT '현재사용금액'", nullable = false)
	private Long currentUsedAmount;

	@Column(columnDefinition = "bigint(20) COMMENT '희망한도'", nullable = false)
	private Long hopeLimit;

	@Enumerated(value = EnumType.STRING)
	@Column(columnDefinition = "varchar(20) DEFAULT 'EMAIL' COMMENT '연락받을 매체 종류'")
	private ContactType contactType;

	@Column(columnDefinition = "varchar(100) COMMENT '연결되지 않은 계좌 정보'")
	private String accountInfo;

	@Column(columnDefinition = "varchar(2000) COMMENT '한도 재산출 요청 메세지'")
	private String contents;

	@Enumerated(value = EnumType.STRING)
	@Column(columnDefinition = "varchar(20) DEFAULT 'REQUESTED' COMMENT '심사상태'", nullable = false)
	private ReviewStatus reviewStatus;

	public static LimitRecalculation of(Corp corp, LimitRecalculationDetail dto){
		return LimitRecalculation.builder()
			.corp(corp).date(dto.getDate())
			.hopeLimit(Long.parseLong(corp.cardIssuanceInfo().card().hopeLimit()))
			.currentUsedAmount(dto.getCurrentUsedAmount())
			.contents(dto.getContents())
			.contactType(dto.getContactType())
			.accountInfo(dto.getAccountInfo())
			.reviewStatus(ReviewStatus.REQUESTED)
			.build();
	}

}
