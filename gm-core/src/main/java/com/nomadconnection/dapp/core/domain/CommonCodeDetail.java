package com.nomadconnection.dapp.core.domain;

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
public class CommonCodeDetail extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long idx;

	@Enumerated(EnumType.STRING)
	private CommonCodeType code;

	private String codeDesc;
	private String code1;
	private String code2;
	private String code3;
	private String code4;
	private String code5;
	private String value1;
	private String value2;
	private String value3;
	private String value4;
	private String value5;
}
