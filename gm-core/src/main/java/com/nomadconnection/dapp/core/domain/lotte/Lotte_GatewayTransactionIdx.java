package com.nomadconnection.dapp.core.domain.lotte;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lotte_GatewayTransactionIdx extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	@Column(nullable = false, updatable = false)
	private Integer interfaceId;
}
