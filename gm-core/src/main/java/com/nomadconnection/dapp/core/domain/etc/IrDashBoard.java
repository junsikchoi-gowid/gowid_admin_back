package com.nomadconnection.dapp.core.domain.etc;


import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class IrDashBoard extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	@Length(min = 1, max =30, message = "타입은 30자 이하로 입력해주시기 바랍니다." )
	@Column
	private String irType;

	@Length(min = 1, max =255, message = "제목은 255자리 이하로 입력해 주시기바랍니다." )
	@Column
    private String title;

	@Column(length = 65535, columnDefinition = "Text")
	private String contents;

	@Column
	private Long idxUser;

	@Column(columnDefinition = "BIT(1) NOT NULL COMMENT '사용유무'")
	private Boolean enable;

}
