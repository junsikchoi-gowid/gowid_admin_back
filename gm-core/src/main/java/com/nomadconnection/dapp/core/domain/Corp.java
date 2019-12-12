package com.nomadconnection.dapp.core.domain;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.embed.Address;
import com.nomadconnection.dapp.core.domain.embed.BankAccount;
import com.nomadconnection.dapp.core.domain.embed.CorpStockholdersListResx;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Corp extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long idx;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idxUser", foreignKey = @ForeignKey(name = "FK_User_Corp"))
	private User user; // 법인을 등록한 사용자

	private String name; // 법인명

	@Column(length = 10)
	@EqualsAndHashCode.Include
	private String bizRegNo; // 사업자등록번호(10 Digits)

	@Embedded
	private CorpStockholdersListResx resxStockholdersList;

	@Embedded
	private Address recipientAddress; // 수령지

	private Integer staffs; // 직원수

//	@OneToMany(mappedBy = "corp")
//	private List<Resx> resxList; // 법인인감증명서, 주주명부

//	@OneToOne
//	@JoinColumn(name = "idxResxCorpRegSeal")
//	private Resx resxCorpRegSeal; // 법인인감증명서 정보

//	@OneToOne
//	@JoinColumn(name = "idxResxCorpShareholderList")
//	private Resx resxCorpShareholderList; // 주주명부 정보

	private Long reqCreditLimit; // 희망법인총한도
	private Long creditLimit; // 법인총한도

	@Embedded
	private BankAccount bankAccount; // 결제계좌

	@Enumerated(EnumType.STRING)
	private CorpStatus status; // pending/denied/approved
}
