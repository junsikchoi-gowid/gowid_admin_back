package com.nomadconnection.dapp.core.domain.common;


import com.nomadconnection.dapp.core.domain.audit.BaseTime;
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

	private Long idxCorp;
	private String name;
	private String startDate;
	private String endDate;
	private String desc1;
	private String desc2;

	@Column(columnDefinition = "varchar(2)    DEFAULT '' COMMENT '업무구분'")
	private String type;
}
