package com.nomadconnection.dapp.core.domain.lotte;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
public class Lotte_D1000 extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	@Column(nullable = false)
	private Long idxCorp;

	@Column
	private String transferDate;

	@Column(columnDefinition = "varchar(13)  DEFAULT '' COMMENT   '사업자등록번호'")
	private String bzno;

	@Column(columnDefinition = "varchar(1)  DEFAULT '' COMMENT   '신규대상여부'")
	private String bzNewYn;
}
