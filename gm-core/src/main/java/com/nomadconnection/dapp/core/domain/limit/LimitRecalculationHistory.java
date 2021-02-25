package com.nomadconnection.dapp.core.domain.limit;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationRequestDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"idxLimitRecalculation", "date"}, name = "UK_LimitRecalculation_Date"))
@Entity
public class LimitRecalculationHistory extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long idx;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idxLimitRecalculation"
		, foreignKey = @ForeignKey(name = "FK_LimitRecalculation_LimitRecalculationHistory")
		, columnDefinition = "bigint(20) COMMENT '한도재심사 idx'", nullable = false)
	private LimitRecalculation limitRecalculation;

	@Column(columnDefinition = "datetime COMMENT '요청일시'", nullable = false)
	private LocalDateTime date;

	@Column(columnDefinition = "bigint(20) DEFAULT '0' COMMENT '현재사용금액'")
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

	public static LimitRecalculationHistory of(LimitRecalculation limitRecalculation, LimitRecalculationRequestDto dto){
		LocalDateTime now = LocalDateTime.now();

		return LimitRecalculationHistory.builder()
			.limitRecalculation(limitRecalculation).date(now)
			.hopeLimit(Long.parseLong(limitRecalculation.corp().cardIssuanceInfo().card().hopeLimit()))
			.currentUsedAmount(dto.getCurrentUsedAmount())
			.contents(dto.getContents())
			.contactType(dto.getContactType())
			.accountInfo(dto.getAccountInfo())
			.build();
	}

}
