package com.nomadconnection.dapp.core.domain.benefit;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BenefitCategory extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	private String categoryGroupCode;

	private String categoryCode;

	@Column(nullable = false)
	private String categoryName;




}
