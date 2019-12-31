package com.nomadconnection.dapp.core.domain;


import com.mysql.cj.jdbc.Clob;
import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class Consent extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	private String title;
	private Clob contents;
	private boolean essential;
	private LocalDateTime usedAt; // 승인(사용)일시
}
