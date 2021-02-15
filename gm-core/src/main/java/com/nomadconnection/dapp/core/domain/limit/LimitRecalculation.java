package com.nomadconnection.dapp.core.domain.limit;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idx", callSuper = false)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"title", "answer"}, name = "UK_title_answer"))
public class LimitRecalculation extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long idx;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@Column(columnDefinition = "bigint(20) COMMENT '법인 idx'", nullable = false)
	private Corp corp;

	@Column(columnDefinition = "date COMMENT '요청일자'", nullable = false)
	private LocalDate date;

	@Column(columnDefinition = "bigint(20) COMMENT '현재사용금액'", nullable = false)
	private Long currentUsedAmount;

	@Column(columnDefinition = "bigint(20) COMMENT '희망한도'", nullable = false)
	private Long hopeLimit;

	@Enumerated(value = EnumType.STRING)
	@Column(columnDefinition = "varchar(20) DEFAULT 'EMAIL' COMMENT '연락받을 매체 종류'", nullable = false)
	private ContactType contactType;

	@Column(columnDefinition = "varchar(100) COMMENT '연락받을 매체'", nullable = false)
	private String contact;

	@Column(columnDefinition = "varchar(2000) COMMENT '한도 재산출 요청 메세지'")
	private String contents;

	@Enumerated(value = EnumType.STRING)
	@Column(columnDefinition = "varchar(20) DEFAULT 'REQUESTED' COMMENT '심사상태'", nullable = false)
	private ReviewStatus reviewStatus;

	@Enumerated(value = EnumType.STRING)
	@Column(columnDefinition = "varchar(20) DEFAULT 'NOLIMIT' COMMENT '한도상태'", nullable = false)
	private GrantStatus grantStatus;

}
