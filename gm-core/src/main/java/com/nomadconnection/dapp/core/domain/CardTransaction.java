package com.nomadconnection.dapp.core.domain;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CardTransactionCustomRepository;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

@SqlResultSetMapping(
		name="PerDailyDtoMapping",
		classes = @ConstructorResult(
				targetClass = CardTransactionCustomRepository.PerDailyDto.class,
				columns = {
						@ColumnResult(name="asUsedAt", type = String.class),
						@ColumnResult(name="week", type = String.class),
						@ColumnResult(name="usedAmount", type = Long.class),
				})
)
@SqlResultSetMapping(
		name="CardListDtoMapping",
		classes = @ConstructorResult(
				targetClass = CardTransactionCustomRepository.CardListDto.class,
				columns = {
						@ColumnResult(name = "cardIdx", type = Long.class),
						@ColumnResult(name = "cardNo", type = String.class),
						@ColumnResult(name = "idxUser", type = Long.class),
//						@ColumnResult(name = "userName", type = String.class),
//						@ColumnResult(name = "deptName", type = String.class),
						@ColumnResult(name = "usedAmount", type = Long.class),
				})
)
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class CardTransaction extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	@Builder.Default
	@Column(nullable = false)
	private ExpenseReportStatus status = ExpenseReportStatus.PENDING; // 지출보고 상태

	private String approvalNo; // 승인번호
	private String memberStoreName; // 가맹점 명
	private String memberStoreCorpNo; // 가맹점 사업자등록번호
	private String memberStoreAddress; // 가맹점 주소

	private String memberStoreAddressDepth1;
	private String memberStoreAddressDepth2;

	private Long usedAmount; // 결제금액

	private LocalDateTime usedAt; // 승인(사용)일시

	@ManyToOne
	@JoinColumn(name = "idxCard", foreignKey = @ForeignKey(name = "FK_Card_CardTransaction"))
	private Card card;
}
