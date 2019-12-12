package com.nomadconnection.dapp.core.domain;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.embed.Address;
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
public class CardIssuance extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idx;

	@ManyToOne
	@JoinColumn(name = "idxCorp", foreignKey = @ForeignKey(name = "FK_Corp_CardIssuance"))
	private Corp corp;

	@ManyToOne
	@JoinColumn(name = "idxUser", foreignKey = @ForeignKey(name = "FK_User_CardIssuance"))
	private User user; // 발급신청한사용자

	private Integer staffs; // 직원수
	private Integer reqCards; // 희망카드개수

	@Enumerated(EnumType.STRING)
	private CardStatementReception reception; // 명세서수령방법

	private String recipient; // 수령인
	private String recipientNo; // 수령인연락처

	@Embedded
	private Address recipientAddress; // 수령지

	private boolean substituteRecipient; // 부재시대리수령여부
}
