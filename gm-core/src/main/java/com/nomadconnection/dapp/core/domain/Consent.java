package com.nomadconnection.dapp.core.domain;


import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

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

	@Length(min = 1, max =55, message = "제목은 255자리 이하로 입력해 주시기바랍니다." )
    private String title;

	@Column(length = 65535, columnDefinition = "Text")
	private String contents;
    private String version;

    private boolean corpStatus; // 법인여부
	private boolean essential; // 필수 선택 여부
	private boolean enabled; // 현재 사용 여부
	private LocalDateTime usedAt; // 승인(사용)일시
	private Long consentOrder; // 순서
}
