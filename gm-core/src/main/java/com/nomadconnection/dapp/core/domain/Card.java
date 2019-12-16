package com.nomadconnection.dapp.core.domain;


import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class Card extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	@Column(nullable = false)
	private String cardNo;

	@Column(nullable = false)
	private String password; // 4 digits

	@Column(nullable = false)
	private String cvc; // card verification code

	@Column(nullable = false)
	private String cvt; // card valid thru, MM/YY

	@Enumerated(EnumType.STRING)
	private CardStatus status;

	@Column(nullable = false)
	@Builder.Default
	private boolean disabled = false;

	@Column(nullable = false)
	private boolean domestic; // 국내결제 가능여부

	@Column(nullable = false)
	private boolean overseas; // 해외결제 가능여부

	@Column(nullable = false)
	private Long creditLimit; // 카드한도

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idxCorp", foreignKey = @ForeignKey(name = "FK_Corp_Card"))
	private Corp corp; // 법인

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idxUser", foreignKey = @ForeignKey(name = "FK_User_Card"))
	private User owner; // 소유자
}
