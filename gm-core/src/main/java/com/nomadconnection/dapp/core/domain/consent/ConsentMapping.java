package com.nomadconnection.dapp.core.domain.consent;


import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(fluent = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class ConsentMapping {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	private Long idxUser;
	private Long idxConsent;

	@Column(nullable = false)
	private boolean status;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "varchar(20) DEFAULT 'GOWID' COMMENT '카드 종류'")
	private CardType cardType;
}
