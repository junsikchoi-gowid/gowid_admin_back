package com.nomadconnection.dapp.core.domain.common;


import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.corp.Corp;
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
@SuppressWarnings("unused")
public class ConnectedMng extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	@Column(nullable = false)
	private String connectedId;

	@Column(nullable = false)
	private Long idxUser;

	//todo 향후 연결처리해줘야함
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idxCorp",
			foreignKey = @ForeignKey(name = "FK_Corp_ConnectedMng"),
			columnDefinition = "bigint(20) COMMENT '법인 idx'",
			nullable = true)
	private Corp corp;

	private String name;
	private String startDate;
	private String endDate;
	private String desc1;
	private String desc2;
	private String issuer;
	private String serialNumber;

	@Column(columnDefinition = "varchar(2)    DEFAULT '' COMMENT '업무구분'")
	private String type;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "varchar(20)  DEFAULT 'NORMAL' COMMENT '카드발급 상태'")
	private ConnectedMngStatus status;
}
