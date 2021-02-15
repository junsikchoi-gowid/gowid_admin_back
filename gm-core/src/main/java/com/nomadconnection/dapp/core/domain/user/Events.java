package com.nomadconnection.dapp.core.domain.user;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;
import javax.persistence.* ;
import java.time.LocalDateTime;


@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Events extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;
	private Long idxUser;
	private Long createUser;

	@Column(columnDefinition = "varchar(255) comment '이벤트명' ")
	private String eventName;

	@Column(columnDefinition = "DATETIME default 20201231010101 comment '시작일자'")
	private LocalDateTime startDate;

	@Column(columnDefinition = "DATETIME default 99991231010101 comment '종료일'")
	private LocalDateTime endDate;
}
